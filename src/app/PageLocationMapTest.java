package app;

import page.Page;
import row.Row;
import storage.PageFileManager;
import storage.PageLocationMap;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class PageLocationMapTest {

    public static void main(String[] args) {

        run();
    }

    public static void run() {

        System.out.println("Running PageLocationMap regression tests...");

        PageLocationMap map = new PageLocationMap();

        /*
         * 1. Initially empty.
         */
        if (map.size() != 0) {

            throw new IllegalStateException(
                    "Expected initial map size to be 0, got: " + map.size());
        }

        /*
         * 2. Store: pageId 5 -> offset 1024.
         */
        map.put(5, 1024L);

        /*
         * 3. Retrieve offset and verify it is 1024.
         */
        Long offset5 = map.get(5);

        if (offset5 == null || offset5 != 1024L) {

            throw new IllegalStateException(
                    "Expected offset 1024 for pageId 5, got: " + offset5);
        }

        /*
         * 4. contains(5) returns true.
         */
        if (!map.contains(5)) {

            throw new IllegalStateException(
                    "Expected map to contain pageId 5.");
        }

        /*
         * 5. Unknown page ID is not reported as present.
         */
        if (map.contains(999)) {

            throw new IllegalStateException(
                    "Map should not contain unknown pageId 999.");
        }

        if (map.get(999) != null) {

            throw new IllegalStateException(
                    "Expected null for unknown pageId 999, got: " + map.get(999));
        }

        /*
         * 6. Multiple mappings work:
         *    1 -> 0
         *    2 -> 128
         *    3 -> 256
         */
        map.put(1, 0L);
        map.put(2, 128L);
        map.put(3, 256L);

        if (map.get(1) == null || map.get(1) != 0L) {

            throw new IllegalStateException(
                    "Expected offset 0 for pageId 1, got: " + map.get(1));
        }

        if (map.get(2) == null || map.get(2) != 128L) {

            throw new IllegalStateException(
                    "Expected offset 128 for pageId 2, got: " + map.get(2));
        }

        if (map.get(3) == null || map.get(3) != 256L) {

            throw new IllegalStateException(
                    "Expected offset 256 for pageId 3, got: " + map.get(3));
        }

        /*
         * 7. size() correctly represents the number of unique page IDs (currently 4: 5, 1, 2, 3).
         */
        if (map.size() != 4) {

            throw new IllegalStateException(
                    "Expected map size 4, got: " + map.size());
        }

        /*
         * 8. Updating an existing page ID replaces its old offset:
         *    5 -> 1024 becomes 5 -> 2048.
         */
        map.put(5, 2048L);

        Long updatedOffset5 = map.get(5);

        if (updatedOffset5 == null || updatedOffset5 != 2048L) {

            throw new IllegalStateException(
                    "Expected updated offset 2048 for pageId 5, got: " + updatedOffset5);
        }

        // Size should still be 4 after updating an existing key
        if (map.size() != 4) {

            throw new IllegalStateException(
                    "Expected map size to remain 4 after update, got: " + map.size());
        }

        /*
         * 9. Integration check with PageFileManager:
         *    Page ID -> PageLocationMap -> file offset -> PageFileManager -> Page
         */
        File tempFile = null;

        try {

            tempFile = File.createTempFile("owldb_location_test_", ".data");
            tempFile.deleteOnExit();

            PageFileManager pfm = new PageFileManager(tempFile);
            PageLocationMap locationMap = new PageLocationMap();

            // Create and write page
            Page page = new Page(42, 3);
            Row row1 = new Row(List.of("101", "Order Alpha", "Complete"));
            Row row2 = new Row(List.of("102", "Order Beta", "Pending"));
            page.addRow(row1);
            page.addRow(row2);

            long writtenOffset = pfm.writePage(page);

            // Record in location map
            locationMap.put(page.getPageId(), writtenOffset);

            // Lookup offset from location map
            Long lookupOffset = locationMap.get(42);

            if (lookupOffset == null || lookupOffset != writtenOffset) {

                throw new IllegalStateException(
                        "Location map returned incorrect offset: " + lookupOffset);
            }

            // Read page back using retrieved offset
            Page retrievedPage = pfm.readPage(lookupOffset);

            if (retrievedPage == null) {

                throw new IllegalStateException(
                        "Retrieved page from PageFileManager must not be null.");
            }

            if (retrievedPage.getPageId() != 42) {

                throw new IllegalStateException(
                        "Expected retrieved pageId 42, got: " + retrievedPage.getPageId());
            }

            if (retrievedPage.getMaxRows() != 3) {

                throw new IllegalStateException(
                        "Expected retrieved maxRows 3, got: " + retrievedPage.getMaxRows());
            }

            if (retrievedPage.getRowCount() != 2) {

                throw new IllegalStateException(
                        "Expected row count 2, got: " + retrievedPage.getRowCount());
            }

            if (!retrievedPage.getRows().get(0).getValues().equals(row1.getValues())
                    || !retrievedPage.getRows().get(1).getValues().equals(row2.getValues())) {

                throw new IllegalStateException(
                        "Retrieved row values do not match original page.");
            }

        } catch (IOException e) {

            throw new RuntimeException("Integration test failed with IOException: " + e.getMessage(), e);

        } finally {

            if (tempFile != null && tempFile.exists()) {

                tempFile.delete();
            }
        }

        System.out.println("PageLocationMap regression tests passed successfully.");
    }
}
