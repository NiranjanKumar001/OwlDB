package app;

import page.Page;
import row.Row;
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

        File tempFile = null;

        try {

            /*
             * 1. Create PageStorage using a temporary file.
             */
            tempFile = File.createTempFile("owldb_page_storage_test_", ".data");
            tempFile.deleteOnExit();

            PageStorage pageStorage = new PageStorage(tempFile);

            /*
             * 2 & 3. Create Page: pageId = 10, maxRows = 3 with rows.
             */
            Page page10 = new Page(10, 3);
            Row row1 = new Row(List.of("1", "Alpha", "100"));
            Row row2 = new Row(List.of("2", "Beta", "200"));
            page10.addRow(row1);
            page10.addRow(row2);

            /*
             * 4 & 5. Call savePage and verify valid offset.
             */
            long offset10 = pageStorage.savePage(page10);

            if (offset10 != 0) {

                throw new IllegalStateException(
                        "Expected first page offset to be 0, got: " + offset10);
            }

            /*
             * 6 & 7. Call loadPage(10) and verify metadata and rows.
             */
            Page loaded10 = pageStorage.loadPage(10);

            if (loaded10 == null) {

                throw new IllegalStateException(
                        "loadPage(10) returned null.");
            }

            if (loaded10.getPageId() != 10) {

                throw new IllegalStateException(
                        "Expected page ID 10, got: " + loaded10.getPageId());
            }

            if (loaded10.getMaxRows() != 3) {

                throw new IllegalStateException(
                        "Expected maxRows 3, got: " + loaded10.getMaxRows());
            }

            if (loaded10.getRowCount() != 2) {

                throw new IllegalStateException(
                        "Expected row count 2, got: " + loaded10.getRowCount());
            }

            if (!loaded10.getRows().get(0).getValues().equals(row1.getValues())
                    || !loaded10.getRows().get(1).getValues().equals(row2.getValues())) {

                throw new IllegalStateException(
                        "Loaded page 10 row values do not match original rows.");
            }

            /*
             * 8 & 9. Create another Page with different ID (20) and save it.
             */
            Page page20 = new Page(20, 4);
            Row row3 = new Row(List.of("10", "X", "One"));
            Row row4 = new Row(List.of("20", "Y", "Two"));
            Row row5 = new Row(List.of("30", "Z", "Three"));
            page20.addRow(row3);
            page20.addRow(row4);
            page20.addRow(row5);

            long offset20 = pageStorage.savePage(page20);

            if (offset20 <= offset10) {

                throw new IllegalStateException(
                        "Expected offset20 (" + offset20 + ") > offset10 (" + offset10 + ")");
            }

            /*
             * 10 & 11. Load both pages independently and verify neither was corrupted.
             */
            Page reloaded10 = pageStorage.loadPage(10);
            Page reloaded20 = pageStorage.loadPage(20);

            if (reloaded10 == null || reloaded10.getPageId() != 10 || reloaded10.getRowCount() != 2) {

                throw new IllegalStateException(
                        "Page 10 corrupted after saving Page 20.");
            }

            if (reloaded20 == null || reloaded20.getPageId() != 20 || reloaded20.getRowCount() != 3) {

                throw new IllegalStateException(
                        "Page 20 failed independent load verification.");
            }

            if (!reloaded10.getRows().get(0).getValues().equals(row1.getValues())
                    || !reloaded10.getRows().get(1).getValues().equals(row2.getValues())) {

                throw new IllegalStateException(
                        "Page 10 row data mismatch on independent reload.");
            }

            if (!reloaded20.getRows().get(0).getValues().equals(row3.getValues())
                    || !reloaded20.getRows().get(1).getValues().equals(row4.getValues())
                    || !reloaded20.getRows().get(2).getValues().equals(row5.getValues())) {

                throw new IllegalStateException(
                        "Page 20 row data mismatch on independent reload.");
            }

            /*
             * 12. Test an unknown page ID: loadPage(999) must return null.
             */
            Page unknownPage = pageStorage.loadPage(999);

            if (unknownPage != null) {

                throw new IllegalStateException(
                        "Expected null for unknown pageId 999, got: " + unknownPage);
            }

            /*
             * 13. Test saving the same page ID again with changed row data.
             *     Second save appends a new record and updates PageLocationMap to point
             *     to the newest offset.
             */
            Row row6 = new Row(List.of("3", "Gamma", "300"));
            page10.addRow(row6);

            long updatedOffset10 = pageStorage.savePage(page10);

            if (updatedOffset10 <= offset20) {

                throw new IllegalStateException(
                        "Updated page 10 should receive new appended offset > offset20.");
            }

            Long mapOffset10 = pageStorage.getPageLocationMap().get(10);

            if (mapOffset10 == null || mapOffset10 != updatedOffset10) {

                throw new IllegalStateException(
                        "PageLocationMap failed to update to newest offset.");
            }

            Page updatedLoaded10 = pageStorage.loadPage(10);

            if (updatedLoaded10 == null || updatedLoaded10.getRowCount() != 3) {

                throw new IllegalStateException(
                        "Expected newest version of page 10 with 3 rows.");
            }

            if (!updatedLoaded10.getRows().get(2).getValues().equals(row6.getValues())) {

                throw new IllegalStateException(
                        "Newest row value in updated page 10 is incorrect.");
            }

            // Verify Page 20 still loads correctly
            Page verifyPage20 = pageStorage.loadPage(20);

            if (verifyPage20 == null || verifyPage20.getRowCount() != 3) {

                throw new IllegalStateException(
                        "Page 20 affected by update to Page 10.");
            }

            System.out.println("PageStorage regression tests passed successfully.");

        } catch (IOException e) {

            throw new RuntimeException("PageStorageTest failed with IOException: " + e.getMessage(), e);

        } finally {

            if (tempFile != null && tempFile.exists()) {

                tempFile.delete();
            }
        }
    }
}
