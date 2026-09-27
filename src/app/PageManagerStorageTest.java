package app;

import page.Page;
import page.PageManager;
import page.RID;
import row.Row;
import storage.PageFileManager;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class PageManagerStorageTest {

    public static void main(String[] args) {

        run();
    }

    public static void run() {

        System.out.println("Running PageManagerStorageTest regression tests...");

        File tempFile = null;

        try {

            /*
             * 1. Create a PageManager with pageSize = 2.
             */
            PageManager pm = new PageManager(2);

            /*
             * 2. Insert enough rows to produce multiple pages.
             *    Page 0: row1, row2
             *    Page 1: row3, row4
             *    Page 2: row5
             */
            Row row1 = new Row(List.of("1", "User1", "Active"));
            Row row2 = new Row(List.of("2", "User2", "Active"));
            Row row3 = new Row(List.of("3", "User3", "Pending"));
            Row row4 = new Row(List.of("4", "User4", "Suspended"));
            Row row5 = new Row(List.of("5", "User5", "Active"));

            RID rid1 = pm.insertAndReturnRID(row1);
            RID rid2 = pm.insertAndReturnRID(row2);
            RID rid3 = pm.insertAndReturnRID(row3);
            RID rid4 = pm.insertAndReturnRID(row4);
            RID rid5 = pm.insertAndReturnRID(row5);

            if (pm.getPageCount() != 3) {

                throw new IllegalStateException(
                        "Expected 3 pages in PageManager, got: " + pm.getPageCount());
            }

            /*
             * 3. Verify that newly created pages have NO disk offsets assigned automatically.
             *    Normal insert operations must perform NO disk I/O.
             */
            if (pm.getPageOffset(0) != null) {

                throw new IllegalStateException(
                        "Page 0 should not have an offset before persistence.");
            }

            if (pm.getPageOffset(1) != null) {

                throw new IllegalStateException(
                        "Page 1 should not have an offset before persistence.");
            }

            if (pm.getPageOffset(2) != null) {

                throw new IllegalStateException(
                        "Page 2 should not have an offset before persistence.");
            }

            if (pm.getPageLocationMap().size() != 0) {

                throw new IllegalStateException(
                        "PageLocationMap should be empty initially, size was: "
                                + pm.getPageLocationMap().size());
            }

            /*
             * 4. Create a PageFileManager with a temporary file.
             */
            tempFile = File.createTempFile("owldb_pm_storage_test_", ".data");
            tempFile.deleteOnExit();

            PageFileManager pfm = new PageFileManager(tempFile);

            /*
             * 5. Manually persist Page 0.
             */
            Page page0 = pm.getPage(0);
            long offset0 = pfm.writePage(page0);

            /*
             * 6. Record Page 0 location in PageManager.
             */
            pm.recordPageLocation(0, offset0);

            /*
             * 7. Verify PageManager reports the correct offset for Page 0,
             *    while Page 1 and Page 2 remain unpersisted.
             */
            Long retrievedOffset0 = pm.getPageOffset(0);

            if (retrievedOffset0 == null || retrievedOffset0 != offset0) {

                throw new IllegalStateException(
                        "Expected offset " + offset0 + " for page 0, got: " + retrievedOffset0);
            }

            if (pm.getPageOffset(1) != null) {

                throw new IllegalStateException(
                        "Page 1 offset should still be null.");
            }

            if (pm.getPageOffset(2) != null) {

                throw new IllegalStateException(
                        "Page 2 offset should still be null.");
            }

            /*
             * 8. Read Page 0 from disk using PageFileManager and the recorded offset.
             *    Verify page data integrity.
             */
            Page loadedPage0 = pfm.readPage(pm.getPageOffset(0));

            if (loadedPage0 == null || loadedPage0.getPageId() != 0) {

                throw new IllegalStateException(
                        "Failed to load Page 0 using recorded offset.");
            }

            if (loadedPage0.getMaxRows() != 2 || loadedPage0.getRowCount() != 2) {

                throw new IllegalStateException(
                        "Loaded Page 0 metadata mismatch.");
            }

            if (!loadedPage0.getRows().get(0).getValues().equals(row1.getValues())
                    || !loadedPage0.getRows().get(1).getValues().equals(row2.getValues())) {

                throw new IllegalStateException(
                        "Loaded Page 0 row values corrupted.");
            }

            /*
             * 9. Persist Page 1 and record its offset.
             */
            Page page1 = pm.getPage(1);
            long offset1 = pfm.writePage(page1);

            pm.recordPageLocation(1, offset1);

            /*
             * 10. Verify both page locations are independent and correct.
             */
            if (offset0 == offset1) {

                throw new IllegalStateException(
                        "Page 0 and Page 1 should receive different offsets.");
            }

            if (!pm.getPageOffset(0).equals(offset0)) {

                throw new IllegalStateException(
                        "Page 0 offset corrupted after recording Page 1.");
            }

            if (!pm.getPageOffset(1).equals(offset1)) {

                throw new IllegalStateException(
                        "Page 1 offset incorrect: " + pm.getPageOffset(1));
            }

            if (pm.getPageOffset(2) != null) {

                throw new IllegalStateException(
                        "Page 2 offset should still be null.");
            }

            if (pm.getPageLocationMap().size() != 2) {

                throw new IllegalStateException(
                        "Expected PageLocationMap size 2, got: "
                                + pm.getPageLocationMap().size());
            }

            /*
             * 11. Read Page 1 back and verify data.
             */
            Page loadedPage1 = pfm.readPage(pm.getPageOffset(1));

            if (loadedPage1.getPageId() != 1 || loadedPage1.getRowCount() != 2) {

                throw new IllegalStateException(
                        "Loaded Page 1 metadata mismatch.");
            }

            if (!loadedPage1.getRows().get(0).getValues().equals(row3.getValues())
                    || !loadedPage1.getRows().get(1).getValues().equals(row4.getValues())) {

                throw new IllegalStateException(
                        "Loaded Page 1 row values corrupted.");
            }

            /*
             * 12. Verify in-memory PageManager operations and RID lookups still work as before.
             */
            if (!pm.getRow(rid1).getValues().equals(row1.getValues())
                    || !pm.getRow(rid2).getValues().equals(row2.getValues())
                    || !pm.getRow(rid3).getValues().equals(row3.getValues())
                    || !pm.getRow(rid4).getValues().equals(row4.getValues())
                    || !pm.getRow(rid5).getValues().equals(row5.getValues())) {

                throw new IllegalStateException(
                        "PageManager in-memory RID lookups failed.");
            }

            System.out.println("PageManagerStorageTest regression tests passed successfully.");

        } catch (IOException e) {

            throw new RuntimeException("Test failed with IOException: " + e.getMessage(), e);

        } finally {

            if (tempFile != null && tempFile.exists()) {

                tempFile.delete();
            }
        }
    }
}
