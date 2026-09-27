package app;

import page.Page;
import row.Row;
import storage.PageFileManager;
import storage.PageLocationMap;
import storage.PageStorage;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class PageStorageTest {

    public static void main(String[] args) {

        run();
    }

    public static void run() {

        System.out.println("Running PageStorage regression tests...");

        File tempPageFile = null;
        File tempLocationFile = null;
        File tempFreshPageFile = null;
        File tempMissingMetaFile = null;

        try {

            /*
             * Test 1 — fresh storage:
             * 1. Create temporary page data file.
             * 2. Create temporary metadata file.
             * 3. Create PageStorage.
             * 4. Verify loading an unknown page behaves correctly.
             */
            tempPageFile = File.createTempFile("owldb_storage_test_", ".data");
            tempPageFile.deleteOnExit();

            tempLocationFile = File.createTempFile("owldb_meta_test_", ".data");
            tempLocationFile.deleteOnExit();
            tempLocationFile.delete();

            PageStorage storage1 = new PageStorage(tempPageFile, tempLocationFile);

            if (storage1.getPageLocationMap().size() != 0) {

                throw new IllegalStateException(
                        "Test 1 failed: Expected initial map size 0, got: "
                                + storage1.getPageLocationMap().size());
            }

            Page unknownPage = storage1.loadPage(999);

            if (unknownPage != null) {

                throw new IllegalStateException(
                        "Test 1 failed: Expected null for unknown page ID 999.");
            }

            /*
             * Test 2 — initial persistence:
             * 1. Create Page 10.
             * 2. Save it.
             * 3. Save page-location metadata.
             */
            Page page10 = new Page(10, 3);
            Row row1 = new Row(List.of("1", "Alpha", "100"));
            Row row2 = new Row(List.of("2", "Beta", "200"));
            page10.addRow(row1);
            page10.addRow(row2);

            long offset10 = storage1.savePage(page10);

            if (offset10 != 0) {

                throw new IllegalStateException(
                        "Test 2 failed: Expected first page offset 0, got: " + offset10);
            }

            storage1.savePageLocations();

            if (!tempLocationFile.exists()) {

                throw new IllegalStateException(
                        "Test 2 failed: Metadata file was not created by savePageLocations().");
            }

            /*
             * Test 3 — simulated restart:
             * 1. Destroy/release the first PageStorage object.
             * 2. Create a NEW PageLocationMap.
             * 3. Create a NEW PageStorage using the SAME page data file and metadata file.
             * 4. Do NOT manually insert the old page location into the new map.
             * 5. Call:
             *        loadPage(10)
             * 6. Verify the page is successfully loaded.
             */
            storage1 = null;

            PageLocationMap newMap = new PageLocationMap();
            PageFileManager restartPfm = new PageFileManager(tempPageFile);
            PageStorage storage2 = new PageStorage(restartPfm, newMap, tempLocationFile);

            if (storage2.getPageLocationMap().size() != 1) {

                throw new IllegalStateException(
                        "Test 3 failed: Expected map size 1 after restart, got: "
                                + storage2.getPageLocationMap().size());
            }

            Page loaded10 = storage2.loadPage(10);

            if (loaded10 == null) {

                throw new IllegalStateException(
                        "Test 3 failed: loadPage(10) returned null after restart.");
            }

            if (loaded10.getPageId() != 10) {

                throw new IllegalStateException(
                        "Test 3 failed: Expected pageId 10, got: " + loaded10.getPageId());
            }

            if (loaded10.getMaxRows() != 3) {

                throw new IllegalStateException(
                        "Test 3 failed: Expected maxRows 3, got: " + loaded10.getMaxRows());
            }

            if (loaded10.getRowCount() != 2) {

                throw new IllegalStateException(
                        "Test 3 failed: Expected rowCount 2, got: " + loaded10.getRowCount());
            }

            if (!loaded10.getRows().get(0).getValues().equals(row1.getValues())
                    || !loaded10.getRows().get(1).getValues().equals(row2.getValues())) {

                throw new IllegalStateException(
                        "Test 3 failed: Loaded page 10 row values do not match original rows.");
            }

            /*
             * Test 4 — add another page after restart:
             * 1. Save Page 20 using the second PageStorage.
             * 2. Save metadata.
             * 3. Create a THIRD PageStorage instance.
             * 4. Verify both Page 10 and Page 20 can be loaded.
             */
            Page page20 = new Page(20, 5);
            Row row3 = new Row(List.of("10", "X", "One"));
            Row row4 = new Row(List.of("20", "Y", "Two"));
            Row row5 = new Row(List.of("30", "Z", "Three"));
            page20.addRow(row3);
            page20.addRow(row4);
            page20.addRow(row5);

            long offset20 = storage2.savePage(page20);

            if (offset20 <= offset10) {

                throw new IllegalStateException(
                        "Test 4 failed: Expected offset20 > offset10.");
            }

            storage2.savePageLocations();

            PageStorage storage3 = new PageStorage(tempPageFile, tempLocationFile);

            if (storage3.getPageLocationMap().size() != 2) {

                throw new IllegalStateException(
                        "Test 4 failed: Expected map size 2 in storage3, got: "
                                + storage3.getPageLocationMap().size());
            }

            Page thirdLoaded10 = storage3.loadPage(10);
            Page thirdLoaded20 = storage3.loadPage(20);

            if (thirdLoaded10 == null || thirdLoaded10.getPageId() != 10 || thirdLoaded10.getRowCount() != 2) {

                throw new IllegalStateException(
                        "Test 4 failed: Page 10 failed to load from storage3.");
            }

            if (thirdLoaded20 == null || thirdLoaded20.getPageId() != 20 || thirdLoaded20.getRowCount() != 3) {

                throw new IllegalStateException(
                        "Test 4 failed: Page 20 failed to load from storage3.");
            }

            if (!thirdLoaded10.getRows().get(0).getValues().equals(row1.getValues())
                    || !thirdLoaded10.getRows().get(1).getValues().equals(row2.getValues())) {

                throw new IllegalStateException(
                        "Test 4 failed: Page 10 row content mismatch in storage3.");
            }

            if (!thirdLoaded20.getRows().get(0).getValues().equals(row3.getValues())
                    || !thirdLoaded20.getRows().get(1).getValues().equals(row4.getValues())
                    || !thirdLoaded20.getRows().get(2).getValues().equals(row5.getValues())) {

                throw new IllegalStateException(
                        "Test 4 failed: Page 20 row content mismatch in storage3.");
            }

            /*
             * Test 5 — missing metadata:
             * 1. Create a fresh temporary location.
             * 2. Create PageStorage without metadata.
             * 3. Verify initialization succeeds.
             * 4. Verify no pages are falsely reported as existing.
             */
            tempFreshPageFile = File.createTempFile("owldb_fresh_page_", ".data");
            tempFreshPageFile.deleteOnExit();

            tempMissingMetaFile = new File(
                    tempFreshPageFile.getParentFile(),
                    "owldb_missing_meta_" + System.currentTimeMillis() + ".data");
            if (tempMissingMetaFile.exists()) {

                tempMissingMetaFile.delete();
            }

            PageStorage freshStorage = new PageStorage(tempFreshPageFile, tempMissingMetaFile);

            if (freshStorage.getPageLocationMap().size() != 0) {

                throw new IllegalStateException(
                        "Test 5 failed: Fresh storage with missing metadata should have empty map.");
            }

            if (freshStorage.loadPage(0) != null || freshStorage.loadPage(10) != null) {

                throw new IllegalStateException(
                        "Test 5 failed: Non-existent pages falsely reported as existing.");
            }

            /*
             * Test 6 — page update/overwrite persistence across restart:
             * Append new row to Page 10, save, save metadata, restart and verify newest version.
             */
            Row row6 = new Row(List.of("3", "Gamma", "300"));
            page10.addRow(row6);

            long updatedOffset10 = storage3.savePage(page10);

            if (updatedOffset10 <= offset20) {

                throw new IllegalStateException(
                        "Test 6 failed: Updated page 10 offset should be greater than offset20.");
            }

            storage3.savePageLocations();

            PageStorage storage4 = new PageStorage(tempPageFile, tempLocationFile);
            Page updatedLoaded10 = storage4.loadPage(10);

            if (updatedLoaded10 == null || updatedLoaded10.getRowCount() != 3) {

                throw new IllegalStateException(
                        "Test 6 failed: Expected updated page 10 with 3 rows after restart.");
            }

            if (!updatedLoaded10.getRows().get(2).getValues().equals(row6.getValues())) {

                throw new IllegalStateException(
                        "Test 6 failed: Updated row content mismatch after restart.");
            }

            System.out.println("PageStorage regression tests passed successfully.");

        } catch (IOException e) {

            throw new RuntimeException("PageStorageTest failed with IOException: " + e.getMessage(), e);

        } finally {

            if (tempPageFile != null && tempPageFile.exists()) {

                tempPageFile.delete();
            }

            if (tempLocationFile != null && tempLocationFile.exists()) {

                tempLocationFile.delete();
            }

            if (tempFreshPageFile != null && tempFreshPageFile.exists()) {

                tempFreshPageFile.delete();
            }

            if (tempMissingMetaFile != null && tempMissingMetaFile.exists()) {

                tempMissingMetaFile.delete();
            }
        }
    }
}
