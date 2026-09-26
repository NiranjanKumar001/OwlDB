package app;

import page.Page;
import page.PageManager;
import page.RID;
import row.Row;

import java.util.List;

public class PageStorageTest {

    public static void main(String[] args) {

        run();
    }

    public static void run() {

        System.out.println("Running PageManager regression tests...");

        /*
         * 1. Verify initial state: Page 0 exists.
         */
        PageManager pm = new PageManager(2);

        if (pm.getPageCount() != 1) {

            throw new IllegalStateException(
                    "Expected initial page count to be 1, got: "
                            + pm.getPageCount());
        }

        Page initialPage = pm.getPage(0);

        if (initialPage == null || initialPage.getPageId() != 0) {

            throw new IllegalStateException(
                    "Expected initial page with ID 0, but found: "
                            + initialPage);
        }

        if (pm.getPage(1) != null) {

            throw new IllegalStateException(
                    "Page 1 should not exist yet.");
        }

        /*
         * 2. Insert rows and verify page allocation IDs: 0, 1, 2.
         */
        Row row1 = new Row(List.of("1", "Alpha"));
        Row row2 = new Row(List.of("2", "Beta"));
        Row row3 = new Row(List.of("3", "Gamma"));
        Row row4 = new Row(List.of("4", "Delta"));
        Row row5 = new Row(List.of("5", "Epsilon"));

        RID rid1 = pm.insertAndReturnRID(row1);
        RID rid2 = pm.insertAndReturnRID(row2);

        if (rid1.getPageId() != 0 || rid1.getSlotId() != 0) {

            throw new IllegalStateException(
                    "Unexpected RID for row1: " + rid1);
        }

        if (rid2.getPageId() != 0 || rid2.getSlotId() != 1) {

            throw new IllegalStateException(
                    "Unexpected RID for row2: " + rid2);
        }

        // Inserting row3 must allocate page ID 1
        RID rid3 = pm.insertAndReturnRID(row3);

        if (rid3.getPageId() != 1 || rid3.getSlotId() != 0) {

            throw new IllegalStateException(
                    "Expected row3 on page 1 slot 0, got: " + rid3);
        }

        RID rid4 = pm.insertAndReturnRID(row4);

        if (rid4.getPageId() != 1 || rid4.getSlotId() != 1) {

            throw new IllegalStateException(
                    "Expected row4 on page 1 slot 1, got: " + rid4);
        }

        // Inserting row5 must allocate page ID 2
        RID rid5 = pm.insertAndReturnRID(row5);

        if (rid5.getPageId() != 2 || rid5.getSlotId() != 0) {

            throw new IllegalStateException(
                    "Expected row5 on page 2 slot 0, got: " + rid5);
        }

        /*
         * 3. Verify getPage() lookups.
         */
        Page page0 = pm.getPage(0);
        Page page1 = pm.getPage(1);
        Page page2 = pm.getPage(2);

        if (page0 == null || page0.getPageId() != 0) {

            throw new IllegalStateException(
                    "getPage(0) failed to return page with ID 0.");
        }

        if (page1 == null || page1.getPageId() != 1) {

            throw new IllegalStateException(
                    "getPage(1) failed to return page with ID 1.");
        }

        if (page2 == null || page2.getPageId() != 2) {

            throw new IllegalStateException(
                    "getPage(2) failed to return page with ID 2.");
        }

        if (pm.getPage(999) != null) {

            throw new IllegalStateException(
                    "getPage(999) should return null for nonexistent page.");
        }

        /*
         * 4. Verify RID lookups retrieve correct rows across all pages.
         */
        Row fetched1 = pm.getRow(rid1);
        Row fetched2 = pm.getRow(rid2);
        Row fetched3 = pm.getRow(rid3);
        Row fetched4 = pm.getRow(rid4);
        Row fetched5 = pm.getRow(rid5);

        if (!row1.getValues().equals(fetched1.getValues())) {

            throw new IllegalStateException(
                    "Row 1 mismatch: expected "
                            + row1.getValues()
                            + ", got "
                            + (fetched1 != null ? fetched1.getValues() : null));
        }

        if (!row3.getValues().equals(fetched3.getValues())) {

            throw new IllegalStateException(
                    "Row 3 mismatch: expected "
                            + row3.getValues()
                            + ", got "
                            + (fetched3 != null ? fetched3.getValues() : null));
        }

        if (!row5.getValues().equals(fetched5.getValues())) {

            throw new IllegalStateException(
                    "Row 5 mismatch: expected "
                            + row5.getValues()
                            + ", got "
                            + (fetched5 != null ? fetched5.getValues() : null));
        }

        // Boundary checks
        if (pm.getRow(new RID(999, 0)) != null) {

            throw new IllegalStateException(
                    "getRow with invalid page ID should return null.");
        }

        if (pm.getRow(new RID(0, 999)) != null) {

            throw new IllegalStateException(
                    "getRow with invalid slot ID should return null.");
        }

        /*
         * 5. Verify multiple page counts and totals.
         */
        if (pm.getPageCount() != 3) {

            throw new IllegalStateException(
                    "Expected total page count 3, got: "
                            + pm.getPageCount());
        }

        if (pm.getTotalRowCount() != 5) {

            throw new IllegalStateException(
                    "Expected total row count 5, got: "
                            + pm.getTotalRowCount());
        }

        System.out.println("PageManager regression tests passed successfully.");
    }
}
