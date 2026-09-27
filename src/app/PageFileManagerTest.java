package app;

import page.Page;
import row.Row;
import storage.PageFileManager;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class PageFileManagerTest {

    public static void main(String[] args) {

        run();
    }

    public static void run() {

        System.out.println("Running PageFileManager regression tests...");

        File tempFile = null;

        try {

            tempFile = File.createTempFile("owldb_pages_test_", ".data");
            tempFile.deleteOnExit();

            PageFileManager pfm = new PageFileManager(tempFile);

            /*
             * 1. Create first page: ID 7, maxRows 3, with 2 rows.
             */
            Page page1 = new Page(7, 3);
            Row row1 = new Row(List.of("1", "Alice", "Admin"));
            Row row2 = new Row(List.of("2", "Bob", "User"));
            page1.addRow(row1);
            page1.addRow(row2);

            /*
             * 2. Write page1 and capture offset.
             */
            long offset1 = pfm.writePage(page1);

            if (offset1 != 0) {

                throw new IllegalStateException(
                        "Expected first page offset to be 0, got: " + offset1);
            }

            /*
             * 3. Read page1 back and verify fields.
             */
            Page loaded1 = pfm.readPage(offset1);

            if (loaded1 == null) {

                throw new IllegalStateException(
                        "Loaded page1 must not be null.");
            }

            if (loaded1.getPageId() != 7) {

                throw new IllegalStateException(
                        "Expected page ID 7, got: " + loaded1.getPageId());
            }

            if (loaded1.getMaxRows() != 3) {

                throw new IllegalStateException(
                        "Expected maxRows 3, got: " + loaded1.getMaxRows());
            }

            if (loaded1.getRowCount() != 2) {

                throw new IllegalStateException(
                        "Expected row count 2, got: " + loaded1.getRowCount());
            }

            if (!loaded1.getRows().get(0).getValues().equals(row1.getValues())) {

                throw new IllegalStateException(
                        "Row 1 mismatch. Expected: "
                                + row1.getValues()
                                + ", got: "
                                + loaded1.getRows().get(0).getValues());
            }

            if (!loaded1.getRows().get(1).getValues().equals(row2.getValues())) {

                throw new IllegalStateException(
                        "Row 2 mismatch. Expected: "
                                + row2.getValues()
                                + ", got: "
                                + loaded1.getRows().get(1).getValues());
            }

            /*
             * 4. Create and write second different page: ID 15, maxRows 5, with 3 rows.
             */
            Page page2 = new Page(15, 5);
            Row row3 = new Row(List.of("10", "Product A", "19.99"));
            Row row4 = new Row(List.of("20", "Product B", "49.99"));
            Row row5 = new Row(List.of("30", "Product C", "99.99"));
            page2.addRow(row3);
            page2.addRow(row4);
            page2.addRow(row5);

            long offset2 = pfm.writePage(page2);

            /*
             * 5. Verify different offset and file growth.
             */
            if (offset2 <= offset1) {

                throw new IllegalStateException(
                        "Expected offset2 (" + offset2 + ") to be greater than offset1 (" + offset1 + ")");
            }

            long totalFileSize = pfm.getFileSize();

            if (totalFileSize <= offset2) {

                throw new IllegalStateException(
                        "File size (" + totalFileSize + ") should be greater than offset2 (" + offset2 + ")");
            }

            /*
             * 6. Read BOTH pages back independently and verify neither was corrupted.
             */
            Page readPage2 = pfm.readPage(offset2);

            if (readPage2.getPageId() != 15) {

                throw new IllegalStateException(
                        "Expected page2 ID 15, got: " + readPage2.getPageId());
            }

            if (readPage2.getMaxRows() != 5) {

                throw new IllegalStateException(
                        "Expected page2 maxRows 5, got: " + readPage2.getMaxRows());
            }

            if (readPage2.getRowCount() != 3) {

                throw new IllegalStateException(
                        "Expected page2 row count 3, got: " + readPage2.getRowCount());
            }

            if (!readPage2.getRows().get(0).getValues().equals(row3.getValues())
                    || !readPage2.getRows().get(1).getValues().equals(row4.getValues())
                    || !readPage2.getRows().get(2).getValues().equals(row5.getValues())) {

                throw new IllegalStateException(
                        "Page 2 rows content corrupted.");
            }

            // Read page 1 again
            Page readPage1Again = pfm.readPage(offset1);

            if (readPage1Again.getPageId() != 7) {

                throw new IllegalStateException(
                        "Expected page1 ID 7, got: " + readPage1Again.getPageId());
            }

            if (readPage1Again.getMaxRows() != 3) {

                throw new IllegalStateException(
                        "Expected page1 maxRows 3, got: " + readPage1Again.getMaxRows());
            }

            if (readPage1Again.getRowCount() != 2) {

                throw new IllegalStateException(
                        "Expected page1 row count 2, got: " + readPage1Again.getRowCount());
            }

            if (!readPage1Again.getRows().get(0).getValues().equals(row1.getValues())
                    || !readPage1Again.getRows().get(1).getValues().equals(row2.getValues())) {

                throw new IllegalStateException(
                        "Page 1 rows corrupted after writing page 2.");
            }

            /*
             * 7. Test invalid offset error handling.
             */
            try {

                pfm.readPage(-1);
                throw new IllegalStateException("Expected IllegalArgumentException for negative offset.");

            } catch (IllegalArgumentException expected) {

                // Expected behavior
            }

            try {

                pfm.readPage(999999);
                throw new IllegalStateException("Expected IllegalArgumentException for out-of-bounds offset.");

            } catch (IllegalArgumentException expected) {

                // Expected behavior
            }

            System.out.println("PageFileManager regression tests passed successfully.");

        } catch (IOException e) {

            throw new RuntimeException("Test failed due to IOException: " + e.getMessage(), e);

        } finally {

            if (tempFile != null && tempFile.exists()) {

                tempFile.delete();
            }
        }
    }
}
