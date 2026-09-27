package storage;

import page.Page;

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
 * Uses LinkedHashMap with access-order to achieve average O(1) lookup, update,
 * insertion, and eviction.
 */
public class BufferPool {

    private int capacity;

    private Map<Integer, Page> pages;

    public BufferPool(
            int capacity) {

        if (capacity <= 0) {

            throw new IllegalArgumentException(
                    "Buffer pool capacity must be greater than zero: " + capacity);
        }

        this.capacity = capacity;

        this.pages = new LinkedHashMap<>(capacity, 0.75f, true);
    }

    public Page getPage(
            int pageId) {

        return pages.get(pageId);
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

    public void clear() {

        pages.clear();
    }
}
