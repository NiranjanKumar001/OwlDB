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
         * Foundation Test 1: Create BufferPool with capacity 2.
         * Verify getCapacity() == 2 and size() == 0.
         */
        BufferPool pool = new BufferPool(2);

        if (pool.getCapacity() != 2) {

            throw new IllegalStateException(
                    "Foundation 1 failed: Expected capacity 2, got: " + pool.getCapacity());
        }

        if (pool.size() != 0) {

            throw new IllegalStateException(
                    "Foundation 1 failed: Expected initial size 0, got: " + pool.size());
        }

        /*
         * Foundation Test 2: Insert Page 1.
         * Verify containsPage(1) == true, getPage(1) != null, and size() == 1.
         */
        Page page1 = new Page(1, 10);
        pool.putPage(page1);

        if (!pool.containsPage(1)) {

            throw new IllegalStateException(
                    "Foundation 2 failed: Expected pool to contain page 1.");
        }

        if (pool.getPage(1) == null || pool.getPage(1) != page1) {

            throw new IllegalStateException(
                    "Foundation 2 failed: getPage(1) did not return page 1.");
        }

        if (pool.size() != 1) {

            throw new IllegalStateException(
                    "Foundation 2 failed: Expected size 1, got: " + pool.size());
        }

        /*
         * Foundation Test 3: Insert Page 2.
         * Verify containsPage(2) == true and size() == 2.
         */
        Page page2 = new Page(2, 10);
        pool.putPage(page2);

        if (!pool.containsPage(2)) {

            throw new IllegalStateException(
                    "Foundation 3 failed: Expected pool to contain page 2.");
        }

        if (pool.size() != 2) {

            throw new IllegalStateException(
                    "Foundation 3 failed: Expected size 2, got: " + pool.size());
        }

        /*
         * Foundation Test 4: Replace Page 1.
         * Verify size remains 2 and getPage(1) returns the replacement page.
         */
        Page replacementPage1 = new Page(1, 20);
        pool.putPage(replacementPage1);

        if (pool.size() != 2) {

            throw new IllegalStateException(
                    "Foundation 4 failed: Pool size should remain 2 after replacing page 1, got: "
                            + pool.size());
        }

        if (pool.getPage(1) != replacementPage1) {

            throw new IllegalStateException(
                    "Foundation 4 failed: getPage(1) did not return the replacement page.");
        }

        if (pool.getPage(1).getMaxRows() != 20) {

            throw new IllegalStateException(
                    "Foundation 4 failed: Replacement page property mismatch.");
        }

        /*
         * Foundation Test 5: Invalid capacities.
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
                    "Foundation 5 failed: Expected IllegalArgumentException for capacity 0.");
        }

        boolean caughtNegative = false;

        try {

            new BufferPool(-1);

        } catch (IllegalArgumentException expected) {

            caughtNegative = true;
        }

        if (!caughtNegative) {

            throw new IllegalStateException(
                    "Foundation 5 failed: Expected IllegalArgumentException for capacity -1.");
        }

        /*
         * OWLET-066 TEST 1: Basic eviction.
         * capacity = 3, insert 1, 2, 3 -> size 3.
         * Insert 4 -> size 3, page 1 evicted, pages 2, 3, 4 present.
         */
        BufferPool poolTest1 = new BufferPool(3);
        poolTest1.putPage(new Page(1, 5));
        poolTest1.putPage(new Page(2, 5));
        poolTest1.putPage(new Page(3, 5));

        if (poolTest1.size() != 3) {

            throw new IllegalStateException(
                    "Test 1 failed: Expected size 3, got: " + poolTest1.size());
        }

        poolTest1.putPage(new Page(4, 5));

        if (poolTest1.size() != 3) {

            throw new IllegalStateException(
                    "Test 1 failed: Expected size 3 after eviction, got: " + poolTest1.size());
        }

        if (poolTest1.getPage(1) != null) {

            throw new IllegalStateException(
                    "Test 1 failed: Page 1 should have been evicted.");
        }

        if (poolTest1.getPage(2) == null || poolTest1.getPage(3) == null
                || poolTest1.getPage(4) == null) {

            throw new IllegalStateException(
                    "Test 1 failed: Pages 2, 3, and 4 must exist in pool.");
        }

        /*
         * OWLET-066 TEST 2: Access changes LRU order.
         * capacity = 3, insert 1, 2, 3.
         * Access getPage(1) -> recency becomes 2, 3, 1.
         * Insert 4 -> page 2 is evicted.
         */
        BufferPool poolTest2 = new BufferPool(3);
        poolTest2.putPage(new Page(1, 5));
        poolTest2.putPage(new Page(2, 5));
        poolTest2.putPage(new Page(3, 5));

        poolTest2.getPage(1);

        poolTest2.putPage(new Page(4, 5));

        if (poolTest2.getPage(1) == null) {

            throw new IllegalStateException(
                    "Test 2 failed: Page 1 was accessed and should not be evicted.");
        }

        if (poolTest2.getPage(2) != null) {

            throw new IllegalStateException(
                    "Test 2 failed: Page 2 was least recently used and should have been evicted.");
        }

        if (poolTest2.getPage(3) == null || poolTest2.getPage(4) == null) {

            throw new IllegalStateException(
                    "Test 2 failed: Pages 3 and 4 must exist in pool.");
        }

        /*
         * OWLET-066 TEST 3: Multiple accesses.
         * capacity = 3, insert 1, 2, 3.
         * Access getPage(1), getPage(2) -> recency becomes 3, 1, 2.
         * Insert 4 -> page 3 is evicted.
         */
        BufferPool poolTest3 = new BufferPool(3);
        poolTest3.putPage(new Page(1, 5));
        poolTest3.putPage(new Page(2, 5));
        poolTest3.putPage(new Page(3, 5));

        poolTest3.getPage(1);
        poolTest3.getPage(2);

        poolTest3.putPage(new Page(4, 5));

        if (poolTest3.getPage(3) != null) {

            throw new IllegalStateException(
                    "Test 3 failed: Page 3 was least recently used and should have been evicted.");
        }

        if (poolTest3.getPage(1) == null || poolTest3.getPage(2) == null
                || poolTest3.getPage(4) == null) {

            throw new IllegalStateException(
                    "Test 3 failed: Pages 1, 2, and 4 must exist in pool.");
        }

        /*
         * OWLET-066 TEST 4: Replacement.
         * capacity = 3, insert 1, 2, 3.
         * Replace page 2 with new Page object.
         * Verify size remains 3, getPage(2) returns replacement object,
         * and no other page was evicted.
         */
        BufferPool poolTest4 = new BufferPool(3);
        poolTest4.putPage(new Page(1, 5));
        poolTest4.putPage(new Page(2, 5));
        poolTest4.putPage(new Page(3, 5));

        Page newPage2 = new Page(2, 99);
        poolTest4.putPage(newPage2);

        if (poolTest4.size() != 3) {

            throw new IllegalStateException(
                    "Test 4 failed: Size should remain 3 after replacing page 2, got: "
                            + poolTest4.size());
        }

        if (poolTest4.getPage(2) != newPage2) {

            throw new IllegalStateException(
                    "Test 4 failed: getPage(2) did not return replacement page.");
        }

        if (poolTest4.getPage(1) == null || poolTest4.getPage(3) == null) {

            throw new IllegalStateException(
                    "Test 4 failed: Replacing existing page must not evict another page.");
        }

        /*
         * OWLET-066 TEST 5: Repeated eviction.
         * capacity = 2, insert 1, 2.
         * Insert 3 -> 1 evicted, pool has 2, 3.
         * Insert 4 -> 2 evicted, pool has 3, 4.
         * Insert 5 -> 3 evicted, pool has 4, 5.
         * Verify size is 2 after every insert and oldest pages removed in LRU order.
         */
        BufferPool poolTest5 = new BufferPool(2);
        poolTest5.putPage(new Page(1, 5));
        poolTest5.putPage(new Page(2, 5));

        poolTest5.putPage(new Page(3, 5));

        if (poolTest5.size() != 2 || poolTest5.containsPage(1) || !poolTest5.containsPage(2)
                || !poolTest5.containsPage(3)) {

            throw new IllegalStateException(
                    "Test 5 failed: After inserting 3, pool should contain 2 and 3.");
        }

        poolTest5.putPage(new Page(4, 5));

        if (poolTest5.size() != 2 || poolTest5.containsPage(2) || !poolTest5.containsPage(3)
                || !poolTest5.containsPage(4)) {

            throw new IllegalStateException(
                    "Test 5 failed: After inserting 4, pool should contain 3 and 4.");
        }

        poolTest5.putPage(new Page(5, 5));

        if (poolTest5.size() != 2 || poolTest5.containsPage(3) || !poolTest5.containsPage(4)
                || !poolTest5.containsPage(5)) {

            throw new IllegalStateException(
                    "Test 5 failed: After inserting 5, pool should contain 4 and 5.");
        }

        /*
         * OWLET-066 TEST 6: Clear.
         * Verify clear() completely empties the pool.
         */
        BufferPool poolTest6 = new BufferPool(3);
        poolTest6.putPage(new Page(1, 5));
        poolTest6.putPage(new Page(2, 5));
        poolTest6.clear();

        if (poolTest6.size() != 0) {

            throw new IllegalStateException(
                    "Test 6 failed: Expected size 0 after clear(), got: " + poolTest6.size());
        }

        if (poolTest6.containsPage(1) || poolTest6.containsPage(2)) {

            throw new IllegalStateException(
                    "Test 6 failed: Pool should not contain any pages after clear().");
        }

        /*
         * OWLET-066 TEST 7: Capacity 1.
         * capacity = 1. Insert 1, insert 2.
         * Verify size() == 1, page 1 is gone, page 2 exists.
         */
        BufferPool poolTest7 = new BufferPool(1);
        poolTest7.putPage(new Page(1, 5));

        if (poolTest7.size() != 1 || !poolTest7.containsPage(1)) {

            throw new IllegalStateException(
                    "Test 7 failed: Initial capacity 1 insert failed.");
        }

        poolTest7.putPage(new Page(2, 5));

        if (poolTest7.size() != 1) {

            throw new IllegalStateException(
                    "Test 7 failed: Size must remain 1, got: " + poolTest7.size());
        }

        if (poolTest7.containsPage(1)) {

            throw new IllegalStateException(
                    "Test 7 failed: Page 1 should be evicted in capacity 1 pool.");
        }

        if (!poolTest7.containsPage(2) || poolTest7.getPage(2) == null) {

            throw new IllegalStateException(
                    "Test 7 failed: Page 2 should exist in capacity 1 pool.");
        }

        /*
         * Boundary Test: Larger pool (1000 pages).
         * Insert 1000 pages -> size is 1000.
         * Insert page 1001 -> size remains 1000, page 1 evicted, page 1001 exists.
         */
        BufferPool largePool = new BufferPool(1000);

        for (int i = 1; i <= 1000; i++) {

            largePool.putPage(new Page(i, 5));
        }

        if (largePool.size() != 1000) {

            throw new IllegalStateException(
                    "Boundary Test failed: Expected large pool size 1000, got: " + largePool.size());
        }

        largePool.putPage(new Page(1001, 5));

        if (largePool.size() != 1000) {

            throw new IllegalStateException(
                    "Boundary Test failed: Large pool size should remain 1000 after eviction, got: "
                            + largePool.size());
        }

        if (largePool.containsPage(1)) {

            throw new IllegalStateException(
                    "Boundary Test failed: Page 1 should have been evicted from large pool.");
        }

        if (!largePool.containsPage(1001)) {

            throw new IllegalStateException(
                    "Boundary Test failed: Page 1001 should exist in large pool.");
        }

        System.out.println("BufferPool regression tests passed successfully.");
    }
}
