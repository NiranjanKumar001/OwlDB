package app;

import page.Page;
import storage.BufferPool;

public class BufferPoolTest {

    public static void main(String[] args) {

        run();
    }

    public static void run() {

        System.out.println("Running BufferPool regression tests...");

        /*
         * 1. Create BufferPool with capacity 2.
         * Verify getCapacity() == 2 and size() == 0.
         */
        BufferPool pool = new BufferPool(2);

        if (pool.getCapacity() != 2) {

            throw new IllegalStateException(
                    "Test 1 failed: Expected capacity 2, got: " + pool.getCapacity());
        }

        if (pool.size() != 0) {

            throw new IllegalStateException(
                    "Test 1 failed: Expected initial size 0, got: " + pool.size());
        }

        /*
         * 2. Create Page 1 and insert it.
         * Verify containsPage(1) == true, getPage(1) != null, and size() == 1.
         */
        Page page1 = new Page(1, 10);
        pool.putPage(page1);

        if (!pool.containsPage(1)) {

            throw new IllegalStateException(
                    "Test 2 failed: Expected pool to contain page 1.");
        }

        if (pool.getPage(1) == null || pool.getPage(1) != page1) {

            throw new IllegalStateException(
                    "Test 2 failed: getPage(1) did not return page 1.");
        }

        if (pool.size() != 1) {

            throw new IllegalStateException(
                    "Test 2 failed: Expected size 1, got: " + pool.size());
        }

        /*
         * 3. Create Page 2 and insert it.
         * Verify containsPage(2) == true and size() == 2.
         */
        Page page2 = new Page(2, 10);
        pool.putPage(page2);

        if (!pool.containsPage(2)) {

            throw new IllegalStateException(
                    "Test 3 failed: Expected pool to contain page 2.");
        }

        if (pool.size() != 2) {

            throw new IllegalStateException(
                    "Test 3 failed: Expected size 2, got: " + pool.size());
        }

        /*
         * 4. Replace Page 1.
         * Create another Page object with pageId 1 and insert it.
         * Verify size() == 2 and getPage(1) returns the replacement Page.
         */
        Page replacementPage1 = new Page(1, 20);
        pool.putPage(replacementPage1);

        if (pool.size() != 2) {

            throw new IllegalStateException(
                    "Test 4 failed: Pool size should remain 2 after replacing page 1, got: "
                            + pool.size());
        }

        if (pool.getPage(1) != replacementPage1) {

            throw new IllegalStateException(
                    "Test 4 failed: getPage(1) did not return the replacement page.");
        }

        if (pool.getPage(1).getMaxRows() != 20) {

            throw new IllegalStateException(
                    "Test 4 failed: Replacement page property mismatch.");
        }

        /*
         * 5. Try inserting Page 3.
         * Because the pool is full, verify that IllegalStateException is thrown.
         * Verify size() remains 2 and Page 1 and Page 2 still exist.
         */
        Page page3 = new Page(3, 10);
        boolean caughtOverflow = false;

        try {

            pool.putPage(page3);

        } catch (IllegalStateException expected) {

            caughtOverflow = true;
        }

        if (!caughtOverflow) {

            throw new IllegalStateException(
                    "Test 5 failed: Expected IllegalStateException when inserting into full pool.");
        }

        if (pool.size() != 2) {

            throw new IllegalStateException(
                    "Test 5 failed: Pool size changed after rejected insert, got: " + pool.size());
        }

        if (!pool.containsPage(1) || !pool.containsPage(2)) {

            throw new IllegalStateException(
                    "Test 5 failed: Existing pages (1 and 2) must remain after rejected insert.");
        }

        /*
         * 6. Test clear():
         * Verify size() == 0 and containsPage(1) == false.
         */
        pool.clear();

        if (pool.size() != 0) {

            throw new IllegalStateException(
                    "Test 6 failed: Expected size 0 after clear(), got: " + pool.size());
        }

        if (pool.containsPage(1) || pool.containsPage(2)) {

            throw new IllegalStateException(
                    "Test 6 failed: Pool should not contain any pages after clear().");
        }

        if (pool.getPage(1) != null) {

            throw new IllegalStateException(
                    "Test 6 failed: getPage(1) should return null after clear().");
        }

        /*
         * 7. Test invalid capacities:
         * new BufferPool(0) and new BufferPool(-1) must throw IllegalArgumentException.
         */
        boolean caughtZero = false;

        try {

            new BufferPool(0);

        } catch (IllegalArgumentException expected) {

            caughtZero = true;
        }

        if (!caughtZero) {

            throw new IllegalStateException(
                    "Test 7 failed: Expected IllegalArgumentException for capacity 0.");
        }

        boolean caughtNegative = false;

        try {

            new BufferPool(-1);

        } catch (IllegalArgumentException expected) {

            caughtNegative = true;
        }

        if (!caughtNegative) {

            throw new IllegalStateException(
                    "Test 7 failed: Expected IllegalArgumentException for capacity -1.");
        }

        /*
         * 8. Boundary test with a larger pool:
         * Create BufferPool with capacity 1000.
         * Insert exactly 1000 unique pages.
         * Verify size() == 1000.
         * Then attempt to insert page 1001 and verify IllegalStateException.
         * Verify size() remains 1000.
         */
        BufferPool largePool = new BufferPool(1000);

        for (int i = 1; i <= 1000; i++) {

            largePool.putPage(new Page(i, 5));
        }

        if (largePool.size() != 1000) {

            throw new IllegalStateException(
                    "Test 8 failed: Expected large pool size 1000, got: " + largePool.size());
        }

        boolean caughtLargeOverflow = false;

        try {

            largePool.putPage(new Page(1001, 5));

        } catch (IllegalStateException expected) {

            caughtLargeOverflow = true;
        }

        if (!caughtLargeOverflow) {

            throw new IllegalStateException(
                    "Test 8 failed: Expected IllegalStateException on 1001st page in capacity 1000 pool.");
        }

        if (largePool.size() != 1000) {

            throw new IllegalStateException(
                    "Test 8 failed: Large pool size changed after rejected insert, got: "
                            + largePool.size());
        }

        System.out.println("BufferPool regression tests passed successfully.");
    }
}
