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
import java.nio.file.Files;
import java.util.List;

public class PageLocationMapTest {

    public static void main(String[] args) {

        run();
    }

    public static void run() {

        System.out.println("Running PageLocationMap regression tests...");

        /*
         * Test 1 — basic mapping in memory:
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

        File tempDir = null;
        File tempMetaFile = null;
        File tempMalformedFile = null;
        File tempPageFile = null;
        File tempReadOnlyDir = null;

        try {

            /*
             * Test 2 — normal save in temporary directory:
             * 1. Create a temporary directory.
             * 2. Create a PageLocationMap.
             * 3. Add mappings.
             * 4. Save.
             * 5. Load using a NEW PageLocationMap.
             * 6. Verify all mappings.
             */
            tempDir = Files.createTempDirectory("owldb_meta_dir_").toFile();
            tempMetaFile = new File(tempDir, "page_locations.data");

            PageLocationMap saveMap = new PageLocationMap();
            saveMap.put(10, 1000L);
            saveMap.put(20, 2000L);
            saveMap.put(30, 3000L);

            saveMap.save(tempMetaFile);

            if (!tempMetaFile.exists() || tempMetaFile.length() == 0) {

                throw new IllegalStateException(
                        "Metadata file missing or empty after save.");
            }

            PageLocationMap loadMap = new PageLocationMap();
            loadMap.load(tempMetaFile);

            if (loadMap.size() != 3) {

                throw new IllegalStateException(
                        "Expected loaded map size 3, got: " + loadMap.size());
            }

            if (loadMap.get(10) != 1000L || loadMap.get(20) != 2000L || loadMap.get(30) != 3000L) {

                throw new IllegalStateException(
                        "Loaded map contains incorrect offset values.");
            }

            /*
             * Test 3 — replacement:
             * 1. Save: 1 -> 100, 2 -> 200
             * 2. Change: 2 -> 500
             * 3. Save again.
             * 4. Load using a NEW PageLocationMap.
             * 5. Verify: 1 -> 100, 2 -> 500
             */
            File replaceFile = new File(tempDir, "replace_test.data");
            PageLocationMap replaceMap = new PageLocationMap();
            replaceMap.put(1, 100L);
            replaceMap.put(2, 200L);
            replaceMap.save(replaceFile);

            replaceMap.put(2, 500L);
            replaceMap.save(replaceFile);

            PageLocationMap reloadedReplaceMap = new PageLocationMap();
            reloadedReplaceMap.load(replaceFile);

            if (reloadedReplaceMap.size() != 2) {

                throw new IllegalStateException(
                        "Expected replaced map size 2, got: " + reloadedReplaceMap.size());
            }

            if (reloadedReplaceMap.get(1) != 100L) {

                throw new IllegalStateException(
                        "Expected offset 100 for page 1, got: " + reloadedReplaceMap.get(1));
            }

            if (reloadedReplaceMap.get(2) != 500L) {

                throw new IllegalStateException(
                        "Expected offset 500 for page 2, got: " + reloadedReplaceMap.get(2));
            }

            /*
             * Test 4 — no temporary file after successful save:
             * After save(), verify that the temporary metadata file does not remain.
             */
            File expectedTempFile = new File(replaceFile.getPath() + ".tmp");

            if (expectedTempFile.exists()) {

                throw new IllegalStateException(
                        "Temporary metadata file was not removed after save: " + expectedTempFile);
            }

            File[] dirFiles = tempDir.listFiles();

            if (dirFiles != null) {

                for (File f : dirFiles) {

                    if (f.getName().endsWith(".tmp")) {

                        throw new IllegalStateException(
                                "Found leftover temporary file in metadata directory: " + f.getName());
                    }
                }
            }

            /*
             * Test 5 — old metadata remains valid until replacement:
             * 1. Save valid metadata in a directory.
             * 2. Make directory read-only so write fails before replacement.
             * 3. Verify previous metadata file can still be loaded intact.
             */
            tempReadOnlyDir = Files.createTempDirectory("owldb_meta_ro_dir_").toFile();
            File roMetaFile = new File(tempReadOnlyDir, "ro_metadata.data");

            PageLocationMap initialRoMap = new PageLocationMap();
            initialRoMap.put(1, 100L);
            initialRoMap.put(2, 200L);
            initialRoMap.save(roMetaFile);

            boolean madeReadOnly = tempReadOnlyDir.setWritable(false);

            if (madeReadOnly) {

                PageLocationMap failingUpdateMap = new PageLocationMap();
                failingUpdateMap.put(1, 999L);
                failingUpdateMap.put(2, 888L);

                boolean caughtException = false;

                try {

                    failingUpdateMap.save(roMetaFile);

                } catch (IOException expected) {

                    caughtException = true;
                }

                tempReadOnlyDir.setWritable(true);

                if (!caughtException) {

                    throw new IllegalStateException(
                            "Expected save() to fail on read-only directory.");
                }

                PageLocationMap intactMap = new PageLocationMap();
                intactMap.load(roMetaFile);

                if (intactMap.size() != 2) {

                    throw new IllegalStateException(
                            "Expected original metadata to remain intact with size 2, got: " + intactMap.size());
                }

                if (intactMap.get(1) != 100L || intactMap.get(2) != 200L) {

                    throw new IllegalStateException(
                            "Original metadata values were corrupted after failed save.");
                }
            }

            /*
             * Test 6 — missing file:
             * Load a nonexistent file and verify map remains empty.
             */
            File nonExistentFile = new File(tempDir, "non_existent_metadata_file.data");
            PageLocationMap mapEmpty = new PageLocationMap();
            mapEmpty.load(nonExistentFile);

            if (mapEmpty.size() != 0) {

                throw new IllegalStateException(
                        "Expected map to remain empty for non-existent file.");
            }

            /*
             * Test 7 — malformed metadata:
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
             * Test 8 — integration test with PageStorage:
             * PageStorage -> PageLocationMap -> save metadata
             * -> NEW PageLocationMap -> load metadata -> PageFileManager -> original Page
             */
            tempPageFile = File.createTempFile("owldb_storage_integration_", ".data");
            tempPageFile.deleteOnExit();

            PageFileManager pfm = new PageFileManager(tempPageFile);
            PageStorage storage = new PageStorage(pfm, tempMetaFile);

            Page originalPage = new Page(88, 3);
            Row row1 = new Row(List.of("1001", "ItemA", "10.50"));
            Row row2 = new Row(List.of("1002", "ItemB", "25.00"));
            originalPage.addRow(row1);
            originalPage.addRow(row2);

            storage.savePage(originalPage);

            // Save the PageStorage's PageLocationMap metadata
            storage.savePageLocations();

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

            if (tempMalformedFile != null && tempMalformedFile.exists()) {

                tempMalformedFile.delete();
            }

            if (tempPageFile != null && tempPageFile.exists()) {

                tempPageFile.delete();
            }

            if (tempReadOnlyDir != null && tempReadOnlyDir.exists()) {

                tempReadOnlyDir.setWritable(true);
                deleteDirectory(tempReadOnlyDir);
            }

            if (tempDir != null && tempDir.exists()) {

                deleteDirectory(tempDir);
            }
        }
    }

    private static void deleteDirectory(
            File directory) {

        if (directory != null && directory.exists()) {

            File[] files = directory.listFiles();

            if (files != null) {

                for (File file : files) {

                    if (file.isDirectory()) {

                        deleteDirectory(file);

                    } else {

                        file.delete();
                    }
                }
            }

            directory.delete();
        }
    }
}
