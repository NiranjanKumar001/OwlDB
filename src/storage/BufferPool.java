package storage;

import page.Page;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/*
 * In-memory buffer pool with Least Recently Used (LRU) eviction.
 *
 * For a database targeting 10M+ records, disk pages cannot all fit in RAM.
 * The buffer pool keeps frequently accessed pages in memory with a fixed capacity.
 * When the pool reaches capacity, the least recently used page is evicted
 * to make space for incoming pages, keeping hot pages in cache without unbounded memory growth.
 *
 * Connects to the storage layer (PageStorage) to load missing pages on demand.
 *
 * Uses LinkedHashMap with access-order to achieve average O(1) lookup, update,
 * insertion, and eviction.
 */
public class BufferPool {

    private int capacity;

    private PageStorage pageStorage;

    private Map<Integer, Page> pages;

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
     * If loaded from disk, the page is inserted into the pool (evicting LRU if full).
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

            putPage(loadedPage);
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

    public void putPage(
            Page page) {

        if (page == null) {

            throw new IllegalArgumentException(
                    "Page cannot be null.");
        }

        int pageId = page.getPageId();

        if (!pages.containsKey(pageId) && pages.size() >= capacity) {

            int eldestPageId = pages.keySet().iterator().next();
            pages.remove(eldestPageId);
        }

        pages.put(pageId, page);
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
    }
}
