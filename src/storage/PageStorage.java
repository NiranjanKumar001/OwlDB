package storage;

import page.Page;

import java.io.File;
import java.io.IOException;

/*
 * Coordinates page persistence using PageFileManager and PageLocationMap.
 *
 * Provides explicit page-level save and load operations.
 */
public class PageStorage {

    private PageFileManager pageFileManager;

    private PageLocationMap pageLocationMap;

    public PageStorage() {

        this(
                new PageFileManager(),
                new PageLocationMap());
    }

    public PageStorage(
            File pageFile) {

        this(
                new PageFileManager(pageFile),
                new PageLocationMap());
    }

    public PageStorage(
            PageFileManager pageFileManager) {

        this(
                pageFileManager,
                new PageLocationMap());
    }

    public PageStorage(
            PageFileManager pageFileManager,
            PageLocationMap pageLocationMap) {

        this.pageFileManager = pageFileManager;

        this.pageLocationMap = pageLocationMap;
    }

    /*
     * Persist a page to disk and record its offset.
     */
    public long savePage(
            Page page) throws IOException {

        if (page == null) {

            throw new IllegalArgumentException(
                    "Page cannot be null.");
        }

        long offset = pageFileManager.writePage(
                page);

        pageLocationMap.put(
                page.getPageId(),
                offset);

        return offset;
    }

    /*
     * Load a persisted page from disk by its page ID.
     * Returns null if the page has not been persisted.
     */
    public Page loadPage(
            int pageId) throws IOException {

        Long offset = pageLocationMap.get(
                pageId);

        if (offset == null) {

            return null;
        }

        return pageFileManager.readPage(
                offset);
    }

    public PageFileManager getPageFileManager() {

        return pageFileManager;
    }

    public PageLocationMap getPageLocationMap() {

        return pageLocationMap;
    }
}
