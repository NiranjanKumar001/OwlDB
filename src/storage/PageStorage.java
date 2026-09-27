package storage;

import page.Page;

import java.io.File;
import java.io.IOException;

/*
 * Coordinates page persistence using PageFileManager and PageLocationMap.
 *
 * Supports restoring page-location metadata from an existing metadata file on startup.
 */
public class PageStorage {

    private static final String DEFAULT_LOCATION_FILE = "pages/page_locations.data";

    private PageFileManager pageFileManager;

    private PageLocationMap pageLocationMap;

    private File locationFile;

    public PageStorage() throws IOException {

        this(
                new PageFileManager(),
                new PageLocationMap(),
                new File(DEFAULT_LOCATION_FILE));
    }

    public PageStorage(
            File pageFile) throws IOException {

        this(
                new PageFileManager(pageFile),
                new PageLocationMap(),
                (File) null);
    }

    public PageStorage(
            File pageFile,
            File locationFile) throws IOException {

        this(
                new PageFileManager(pageFile),
                new PageLocationMap(),
                locationFile);
    }

    public PageStorage(
            PageFileManager pageFileManager) throws IOException {

        this(
                pageFileManager,
                new PageLocationMap(),
                (File) null);
    }

    public PageStorage(
            PageFileManager pageFileManager,
            File locationFile) throws IOException {

        this(
                pageFileManager,
                new PageLocationMap(),
                locationFile);
    }

    public PageStorage(
            PageFileManager pageFileManager,
            String locationFilePath) throws IOException {

        this(
                pageFileManager,
                new PageLocationMap(),
                locationFilePath != null ? new File(locationFilePath) : null);
    }

    public PageStorage(
            PageFileManager pageFileManager,
            PageLocationMap pageLocationMap) throws IOException {

        this(
                pageFileManager,
                pageLocationMap,
                (File) null);
    }

    public PageStorage(
            PageFileManager pageFileManager,
            PageLocationMap pageLocationMap,
            String locationFilePath) throws IOException {

        this(
                pageFileManager,
                pageLocationMap,
                locationFilePath != null ? new File(locationFilePath) : null);
    }

    public PageStorage(
            PageFileManager pageFileManager,
            PageLocationMap pageLocationMap,
            File locationFile) throws IOException {

        this.pageFileManager = pageFileManager;

        this.pageLocationMap = pageLocationMap;

        this.locationFile = locationFile;

        if (locationFile != null && locationFile.exists()) {

            this.pageLocationMap.load(locationFile);
        }
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

    /*
     * Save the current page-location metadata to the configured metadata file.
     */
    public void savePageLocations() throws IOException {

        if (locationFile == null) {

            throw new IllegalStateException(
                    "No metadata file configured for saving page locations.");
        }

        pageLocationMap.save(locationFile);
    }

    public void savePageLocations(
            File file) throws IOException {

        pageLocationMap.save(file);
    }

    public void savePageLocations(
            String filePath) throws IOException {

        pageLocationMap.save(filePath);
    }

    public PageFileManager getPageFileManager() {

        return pageFileManager;
    }

    public PageLocationMap getPageLocationMap() {

        return pageLocationMap;
    }

    public File getLocationFile() {

        return locationFile;
    }
}
