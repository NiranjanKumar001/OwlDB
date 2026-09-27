package storage;

import page.Page;

import java.util.HashMap;
import java.util.Map;

/*
 * In-memory buffer pool that caches disk pages with a fixed maximum capacity.
 *
 * Provides average O(1) page lookup, insertion, and replacement.
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

        this.pages = new HashMap<>();
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

        if (pages.containsKey(pageId)) {

            pages.put(pageId, page);
            return;
        }

        if (pages.size() >= capacity) {

            throw new IllegalStateException(
                    "Buffer pool is full (capacity: " + capacity + ").");
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
