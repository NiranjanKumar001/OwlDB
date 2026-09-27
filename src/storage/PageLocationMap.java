package storage;

import java.util.HashMap;
import java.util.Map;

/*
 * Maps logical page IDs to physical file offsets.
 *
 * For example:
 *   pageId 0 -> offset 0
 *   pageId 1 -> offset 128
 */
public class PageLocationMap {

    private Map<Integer, Long> locations;

    public PageLocationMap() {

        locations = new HashMap<>();
    }

    /*
     * Store or update the offset for a page ID.
     */
    public void put(
            int pageId,
            long offset) {

        locations.put(
                pageId,
                offset);
    }

    /*
     * Get the offset for a page ID.
     * Returns null if page ID is not found.
     */
    public Long get(
            int pageId) {

        return locations.get(
                pageId);
    }

    /*
     * Check if a page ID exists in the map.
     */
    public boolean contains(
            int pageId) {

        return locations.containsKey(
                pageId);
    }

    /*
     * Number of mapped pages.
     */
    public int size() {

        return locations.size();
    }
}
