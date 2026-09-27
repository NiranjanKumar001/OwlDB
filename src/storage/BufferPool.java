package storage;

import page.Page;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/*
 * In-memory buffer pool with Least Recently Used (LRU) eviction,
 * dirty page tracking, and page pinning support.
 *
 * For a database targeting 10M+ records, disk pages cannot all fit in RAM.
 * The buffer pool keeps frequently accessed pages in memory with a fixed capacity.
 *
 * Page Pinning & Safe Lifecycle:
 * Pages currently in use by active operations are pinned (pin count > 0).
 * Pinned pages MUST NOT be evicted.
 * Callers use getPageAndPin(pageId) to immediately acquire and pin a page,
 * and releasePage(pageId) when finished using it.
 * Eviction selects the least recently used UNPINNED page.
 * If all cached pages are pinned, eviction fails safely.
 *
 * Dirty Page Tracking & Safe Write-Back:
 * When a cached page is modified, it is marked dirty.
 * Clean unpinned pages can be safely discarded during LRU eviction.
 * Dirty unpinned pages MUST be written back to disk before being evicted.
 * If disk write fails during eviction, the dirty page is NOT evicted.
 *
 * Uses LinkedHashMap with access-order for average O(1) page access and eviction.
 */
public class BufferPool {

    private int capacity;

    private PageStorage pageStorage;

    private Map<Integer, Page> pages;

    private Set<Integer> dirtyPages;

    private Map<Integer, Integer> pinCounts;

    public BufferPool(
            int capacity) {

        this(capacity, null);
    }

    public BufferPool(
            int capacity,
            PageStorage pageStorage) {

        if (capacity <= 0) {

            throw new IllegalArgumentException(
                    "Buffer pool capacity must be greater than zero: " + capacity);
        }

        this.capacity = capacity;

        this.pageStorage = pageStorage;

        this.pages = new LinkedHashMap<>(capacity, 0.75f, true);

        this.dirtyPages = new HashSet<>();

        this.pinCounts = new HashMap<>();
    }

    /*
     * Lookup page in memory cache only.
     * Updates LRU recency if present. Does not read from disk.
     */
    public Page getPage(
            int pageId) {

        return pages.get(pageId);
    }

    /*
     * Retrieve a page from cache, or load from PageStorage on cache miss.
     * If loaded from disk, the page is inserted into the pool (evicting LRU unpinned if full).
     * If the page does not exist on disk, returns null without modifying the cache.
     */
    public Page getPage(
            int pageId,
            PageStorage storage) throws IOException {

        Page cachedPage = pages.get(pageId);

        if (cachedPage != null) {

            return cachedPage;
        }

        if (storage == null) {

            throw new IllegalArgumentException(
                    "PageStorage cannot be null.");
        }

        Page loadedPage = storage.loadPage(pageId);

        if (loadedPage != null) {

            putPage(loadedPage, storage);
        }

        return loadedPage;
    }

    /*
     * Retrieve a page using the pre-configured PageStorage.
     */
    public Page getPageFromStorage(
            int pageId) throws IOException {

        if (this.pageStorage == null) {

            throw new IllegalStateException(
                    "No PageStorage configured for this BufferPool.");
        }

        return getPage(pageId, this.pageStorage);
    }

    /*
     * Retrieve a page and immediately pin it to protect it from eviction.
     * Uses pre-configured PageStorage if available, or memory cache.
     * If the page does not exist, returns null without creating pin metadata.
     */
    public Page getPageAndPin(
            int pageId) throws IOException {

        Page page;

        if (this.pageStorage != null) {

            page = getPage(pageId, this.pageStorage);

        } else {

            page = getPage(pageId);
        }

        if (page != null) {

            pinPage(pageId);
        }

        return page;
    }

    /*
     * Retrieve a page and immediately pin it using the specified PageStorage.
     * If the page does not exist, returns null without creating pin metadata.
     */
    public Page getPageAndPin(
            int pageId,
            PageStorage storage) throws IOException {

        Page page = getPage(pageId, storage);

        if (page != null) {

            pinPage(pageId);
        }

        return page;
    }

    public void putPage(
            Page page) throws IOException {

        putPage(page, this.pageStorage);
    }

    public void putPage(
            Page page,
            PageStorage storage) throws IOException {

        if (page == null) {

            throw new IllegalArgumentException(
                    "Page cannot be null.");
        }

        if (storage == null) {

            storage = this.pageStorage;
        }

        int pageId = page.getPageId();

        if (pages.containsKey(pageId)) {

            if (isPinned(pageId)) {

                throw new IllegalStateException(
                        "Cannot replace pinned page " + pageId + ".");
            }

            if (isDirty(pageId)) {

                if (page != pages.get(pageId)) {

                    throw new IllegalStateException(
                            "Cannot replace dirty page " + pageId + ": page has unsaved modifications.");
                }

                // Same instance already dirty: update recency, keep dirty
                pages.put(pageId, page);
                return;
            }

            pages.put(pageId, page);
            dirtyPages.remove(pageId);
            return;
        }

        if (pages.size() >= capacity) {

            Map.Entry<Integer, Page> victimEntry = null;

            for (Map.Entry<Integer, Page> entry : pages.entrySet()) {

                int candidateId = entry.getKey();

                if (!isPinned(candidateId)) {

                    victimEntry = entry;
                    break;
                }
            }

            if (victimEntry == null) {

                throw new IllegalStateException(
                        "Cannot evict: buffer pool is full and all pages are pinned.");
            }

            int victimPageId = victimEntry.getKey();
            Page victimPage = victimEntry.getValue();

            if (dirtyPages.contains(victimPageId)) {

                if (storage == null) {

                    throw new IllegalStateException(
                            "Cannot evict dirty page " + victimPageId + ": no PageStorage configured.");
                }

                storage.savePage(victimPage);
                dirtyPages.remove(victimPageId);
            }

            pages.remove(victimPageId);
            pinCounts.remove(victimPageId);
        }

        pages.put(pageId, page);
        dirtyPages.remove(pageId);

        if (pages.size() > capacity) {

            throw new IllegalStateException(
                    "Capacity invariant violated: buffer pool size " + pages.size()
                            + " exceeds capacity " + capacity);
        }
    }

    /*
     * Pin a page to indicate it is currently in use and must not be evicted.
     * Increments the page's pin count. If page is not in cache, does nothing.
     */
    public void pinPage(
            int pageId) {

        if (!pages.containsKey(pageId)) {

            return;
        }

        int current = pinCounts.getOrDefault(pageId, 0);
        pinCounts.put(pageId, current + 1);
    }

    /*
     * Unpin a page, decrementing its pin count.
     * When pin count reaches zero, the page becomes eligible for future eviction.
     */
    public void unpinPage(
            int pageId) {

        if (!pages.containsKey(pageId)) {

            return;
        }

        int current = pinCounts.getOrDefault(pageId, 0);

        if (current <= 0) {

            throw new IllegalStateException(
                    "Cannot unpin page " + pageId + ": pin count is already zero.");
        }

        int next = current - 1;

        if (next == 0) {

            pinCounts.remove(pageId);

        } else {

            pinCounts.put(pageId, next);
        }
    }

    /*
     * Safely release a previously pinned page, decrementing its pin count.
     * Counterpart to getPageAndPin. When pin count reaches zero, the page
     * becomes eligible for eviction.
     * Does NOT automatically flush dirty pages; write-back occurs during
     * explicit flush or eviction.
     */
    public void releasePage(
            int pageId) {

        unpinPage(pageId);
    }

    /*
     * Get the current pin count of a cached page.
     * Returns 0 if unpinned or not in the buffer pool.
     */
    public int getPinCount(
            int pageId) {

        if (!pages.containsKey(pageId)) {

            return 0;
        }

        return pinCounts.getOrDefault(pageId, 0);
    }

    /*
     * Check if a page is currently pinned (pin count > 0).
     */
    public boolean isPinned(
            int pageId) {

        return getPinCount(pageId) > 0;
    }

    /*
     * Mark a cached page as dirty.
     * If the page is not in the buffer pool, does nothing.
     */
    public void markDirty(
            int pageId) {

        if (!pages.containsKey(pageId)) {

            return;
        }

        dirtyPages.add(pageId);
    }

    /*
     * Check if a cached page is dirty.
     * Returns false if the page is clean or not present in the buffer pool.
     */
    public boolean isDirty(
            int pageId) {

        return pages.containsKey(pageId) && dirtyPages.contains(pageId);
    }

    /*
     * Flush a single page to the configured PageStorage if it is dirty.
     * If clean or not cached, no disk write occurs.
     */
    public void flushPage(
            int pageId) throws IOException {

        if (this.pageStorage == null) {

            throw new IllegalStateException(
                    "No PageStorage configured for this BufferPool.");
        }

        flushPage(pageId, this.pageStorage);
    }

    /*
     * Flush a single page to the specified PageStorage if it is dirty.
     * If clean or not cached, no disk write occurs.
     */
    public void flushPage(
            int pageId,
            PageStorage storage) throws IOException {

        if (storage == null) {

            throw new IllegalArgumentException(
                    "PageStorage cannot be null.");
        }

        if (!dirtyPages.contains(pageId)) {

            return;
        }

        Page page = null;

        for (Map.Entry<Integer, Page> entry : pages.entrySet()) {

            if (entry.getKey().equals(pageId)) {

                page = entry.getValue();
                break;
            }
        }

        if (page != null) {

            storage.savePage(page);
            dirtyPages.remove(pageId);
        }
    }

    /*
     * Flush all dirty pages to the configured PageStorage and mark them clean.
     * Pages remain in the BufferPool.
     */
    public void flushAll() throws IOException {

        if (this.pageStorage == null) {

            throw new IllegalStateException(
                    "No PageStorage configured for this BufferPool.");
        }

        flushAll(this.pageStorage);
    }

    /*
     * Flush all dirty pages to the specified PageStorage and mark them clean.
     * Pages remain in the BufferPool.
     */
    public void flushAll(
            PageStorage storage) throws IOException {

        if (storage == null) {

            throw new IllegalArgumentException(
                    "PageStorage cannot be null.");
        }

        for (Map.Entry<Integer, Page> entry : pages.entrySet()) {

            int pageId = entry.getKey();

            if (dirtyPages.contains(pageId)) {

                storage.savePage(entry.getValue());
                dirtyPages.remove(pageId);
            }
        }
    }

    public boolean containsPage(
            int pageId) {

        return pages.containsKey(pageId);
    }

    public int size() {

        return pages.size();
    }

    public int getCapacity() {

        return capacity;
    }

    public PageStorage getPageStorage() {

        return pageStorage;
    }

    public void setPageStorage(
            PageStorage pageStorage) {

        this.pageStorage = pageStorage;
    }

    public void clear() {

        pages.clear();
        dirtyPages.clear();
        pinCounts.clear();
    }
}
