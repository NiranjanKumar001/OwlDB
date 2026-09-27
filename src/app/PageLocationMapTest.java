package app;

import page.Page;
import row.Row;
import storage.PageFileManager;
import storage.PageLocationMap;
import storage.PageStorage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class PageLocationMapTest {

    public static void main(String[] args) {

        run();
    }

    public static void run() {

        System.out.println("Running PageLocationMap regression tests...");

        /*
         * Test 1 — basic mapping:
         * 1 -> 0
         * 2 -> 128
         * 3 -> 256
         * Verify get(), contains(), and size().
         */
        PageLocationMap map1 = new PageLocationMap();

        if (map1.size() != 0) {

            throw new IllegalStateException(
                    "Expected initial map size 0, got: " + map1.size());
        }

        map1.put(1, 0L);
        map1.put(2, 128L);
        map1.put(3, 256L);

        if (!map1.contains(1) || !map1.contains(2) || !map1.contains(3)) {

            throw new IllegalStateException(
                    "contains() check failed for inserted keys.");
        }

        if (map1.contains(999)) {

            throw new IllegalStateException(
                    "contains(999) should be false.");
        }

        if (map1.get(1) != 0L || map1.get(2) != 128L || map1.get(3) != 256L) {

            throw new IllegalStateException(
                    "get() returned incorrect offset values.");
        }

        if (map1.size() != 3) {

            throw new IllegalStateException(
                    "Expected map size 3, got: " + map1.size());
        }

        File tempMetaFile = null;
        File tempMalformedFile = null;
        File tempPageFile = null;

        try {

            tempMetaFile = File.createTempFile("owldb_meta_test_", ".data");
            tempMetaFile.deleteOnExit();

            /*
             * Test 2 — save:
             * Save map1 to temporary metadata file.
             */
            map1.save(tempMetaFile);

            if (tempMetaFile.length() == 0) {

                throw new IllegalStateException(
                        "Metadata file was empty after save.");
            }

            /*
             * Test 3 — load:
             * Create a NEW PageLocationMap instance and load the file.
             */
            PageLocationMap map2 = new PageLocationMap();
            map2.load(tempMetaFile);

            if (map2.size() != 3) {

                throw new IllegalStateException(
                        "Expected loaded map size 3, got: " + map2.size());
            }

            if (map2.get(1) != 0L || map2.get(2) != 128L || map2.get(3) != 256L) {

                throw new IllegalStateException(
                        "Loaded map contains incorrect offset values.");
            }

            /*
             * Test 4 — update:
             * Change 2 -> 128 to 2 -> 512, save again, load into another new map.
             */
            map1.put(2, 512L);
            map1.save(tempMetaFile);

            PageLocationMap map3 = new PageLocationMap();
            map3.load(tempMetaFile);

            if (map3.size() != 3) {

                throw new IllegalStateException(
                        "Expected updated map size 3, got: " + map3.size());
            }

            if (map3.get(2) != 512L) {

                throw new IllegalStateException(
                        "Expected updated offset 512 for page 2, got: " + map3.get(2));
            }

            /*
             * Test 5 — missing file:
             * Load a nonexistent file and verify map remains empty.
             */
            File nonExistentFile = new File("non_existent_metadata_file_test.tmp");
            PageLocationMap mapEmpty = new PageLocationMap();
            mapEmpty.load(nonExistentFile);

            if (mapEmpty.size() != 0) {

                throw new IllegalStateException(
                        "Expected map to remain empty for non-existent file.");
            }

            /*
             * Test 6 — malformed metadata:
             * Create a file with an invalid line and verify load fails clearly.
             */
            tempMalformedFile = File.createTempFile("owldb_malformed_test_", ".data");
            tempMalformedFile.deleteOnExit();

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempMalformedFile))) {

                writer.write("1|100");
                writer.newLine();
                writer.write("INVALID_LINE");
                writer.newLine();
            }

            PageLocationMap mapMalformed = new PageLocationMap();
            boolean caughtMalformed = false;

            try {

                mapMalformed.load(tempMalformedFile);

            } catch (IOException expected) {

                caughtMalformed = true;
            }

            if (!caughtMalformed) {

                throw new IllegalStateException(
                        "Expected load() to throw IOException on malformed metadata line.");
            }

            /*
             * Integration Test:
             * PageStorage -> PageLocationMap -> save metadata
             * -> NEW PageLocationMap -> load metadata -> PageFileManager -> original Page
             */
            tempPageFile = File.createTempFile("owldb_storage_integration_", ".data");
            tempPageFile.deleteOnExit();

            PageFileManager pfm = new PageFileManager(tempPageFile);
            PageStorage storage = new PageStorage(pfm);

            Page originalPage = new Page(88, 3);
            Row row1 = new Row(List.of("1001", "ItemA", "10.50"));
            Row row2 = new Row(List.of("1002", "ItemB", "25.00"));
            originalPage.addRow(row1);
            originalPage.addRow(row2);

            storage.savePage(originalPage);

            // Save the PageStorage's PageLocationMap metadata
            storage.getPageLocationMap().save(tempMetaFile);

            // Create a completely new PageLocationMap and load metadata
            PageLocationMap restoredMap = new PageLocationMap();
            restoredMap.load(tempMetaFile);

            Long restoredOffset = restoredMap.get(88);

            if (restoredOffset == null) {

                throw new IllegalStateException(
                        "Restored PageLocationMap missing offset for page 88.");
            }

            // Read the page directly using PageFileManager with restored offset
            Page retrievedPage = pfm.readPage(restoredOffset);

            if (retrievedPage == null || retrievedPage.getPageId() != 88) {

                throw new IllegalStateException(
                        "Failed to retrieve original page using persisted metadata.");
            }

            if (retrievedPage.getMaxRows() != 3 || retrievedPage.getRowCount() != 2) {

                throw new IllegalStateException(
                        "Retrieved page metadata mismatch.");
            }

            if (!retrievedPage.getRows().get(0).getValues().equals(row1.getValues())
                    || !retrievedPage.getRows().get(1).getValues().equals(row2.getValues())) {

                throw new IllegalStateException(
                        "Retrieved page row values mismatch.");
            }

            System.out.println("PageLocationMap regression tests passed successfully.");

        } catch (IOException e) {

            throw new RuntimeException("PageLocationMapTest failed: " + e.getMessage(), e);

        } finally {

            if (tempMetaFile != null && tempMetaFile.exists()) {

                tempMetaFile.delete();
            }

            if (tempMalformedFile != null && tempMalformedFile.exists()) {

                tempMalformedFile.delete();
            }

            if (tempPageFile != null && tempPageFile.exists()) {

                tempPageFile.delete();
            }
        }
    }
}
