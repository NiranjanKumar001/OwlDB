package app;

import page.Page;
import row.Row;
import storage.BufferPool;
import storage.PageStorage;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class BufferPoolTest {

    public static void main(String[] args) {

        try {

            run();

        } catch (IOException e) {

            throw new RuntimeException("BufferPoolTest failed with IOException: " + e.getMessage(), e);
        }
    }

    public static void run() throws IOException {

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

        /*
         * ==================================================
         * OWLET-067 Storage Integration Tests
         * ==================================================
         */
        File tempPageFile = null;
        File tempMetaFile = null;
        File lifecyclePageFile = null;
        File lifecycleMetaFile = null;

        try {

            tempPageFile = File.createTempFile("owldb_bp_storage_", ".data");
            tempPageFile.deleteOnExit();

            tempMetaFile = File.createTempFile("owldb_bp_meta_", ".data");
            tempMetaFile.deleteOnExit();
            tempMetaFile.delete();

            CountingPageStorage countingStorage = new CountingPageStorage(tempPageFile, tempMetaFile);

            // Persist pages 1, 2, 3, 4, 5 to disk storage
            Page p1 = new Page(1, 3);
            p1.addRow(new Row(List.of("1", "Alice", "100")));
            countingStorage.savePage(p1);

            Page p2 = new Page(2, 3);
            p2.addRow(new Row(List.of("2", "Bob", "200")));
            countingStorage.savePage(p2);

            Page p3 = new Page(3, 3);
            p3.addRow(new Row(List.of("3", "Charlie", "300")));
            countingStorage.savePage(p3);

            Page p4 = new Page(4, 3);
            p4.addRow(new Row(List.of("4", "David", "400")));
            countingStorage.savePage(p4);

            Page p5 = new Page(5, 3);
            p5.addRow(new Row(List.of("5", "Eve", "500")));
            countingStorage.savePage(p5);

            countingStorage.savePageLocations();

            /*
             * Storage Test 1 & 2: Cache miss loads page from storage and inserts into BufferPool.
             */
            BufferPool storagePool = new BufferPool(3);

            if (storagePool.containsPage(1)) {

                throw new IllegalStateException(
                        "Storage Test 1 failed: Initial pool should not contain page 1.");
            }

            int initialLoads = countingStorage.loadCount;
            Page loadedP1 = storagePool.getPage(1, countingStorage);

            if (loadedP1 == null || loadedP1.getPageId() != 1) {

                throw new IllegalStateException(
                        "Storage Test 1 failed: Failed to load page 1 from storage.");
            }

            if (countingStorage.loadCount != initialLoads + 1) {

                throw new IllegalStateException(
                        "Storage Test 1 failed: Expected disk load on cache miss.");
            }

            if (!storagePool.containsPage(1)) {

                throw new IllegalStateException(
                        "Storage Test 2 failed: Loaded page 1 was not inserted into BufferPool.");
            }

            if (storagePool.size() != 1) {

                throw new IllegalStateException(
                        "Storage Test 2 failed: Expected pool size 1, got: " + storagePool.size());
            }

            if (!loadedP1.getRows().get(0).getValues().equals(p1.getRows().get(0).getValues())) {

                throw new IllegalStateException(
                        "Storage Test 2 failed: Loaded row content does not match persisted page.");
            }

            /*
             * Storage Test 3: Cache hit returns cached page without secondary disk load.
             */
            int loadsBeforeHit = countingStorage.loadCount;
            Page hitP1 = storagePool.getPage(1, countingStorage);

            if (hitP1 != loadedP1) {

                throw new IllegalStateException(
                        "Storage Test 3 failed: Cache hit should return identical cached Page instance.");
            }

            if (countingStorage.loadCount != loadsBeforeHit) {

                throw new IllegalStateException(
                        "Storage Test 3 failed: Cache hit must NOT cause another disk read.");
            }

            /*
             * Storage Test 4: Missing disk page handled safely.
             */
            Page missing = storagePool.getPage(999, countingStorage);

            if (missing != null) {

                throw new IllegalStateException(
                        "Storage Test 4 failed: Non-existent page should return null.");
            }

            if (storagePool.containsPage(999)) {

                throw new IllegalStateException(
                        "Storage Test 4 failed: Missing page must not be stored in BufferPool.");
            }

            if (storagePool.size() != 1) {

                throw new IllegalStateException(
                        "Storage Test 4 failed: Pool size should remain 1, got: " + storagePool.size());
            }

            /*
             * Storage Test 5: Disk-loaded pages participate in LRU eviction.
             * Pool capacity = 2.
             * Load 1 -> cached: [1].
             * Load 2 -> cached: [1, 2].
             * Access 1 again -> cache hit, LRU recency becomes: [2, 1].
             * Load 3 -> cache miss, loads 3.
             * Page 2 is least recently used, so page 2 must be evicted!
             */
            BufferPool lruStoragePool = new BufferPool(2);

            lruStoragePool.getPage(1, countingStorage);
            lruStoragePool.getPage(2, countingStorage);

            if (lruStoragePool.size() != 2) {

                throw new IllegalStateException(
                        "Storage Test 5 failed: Expected size 2, got: " + lruStoragePool.size());
            }

            // Access page 1 to make it most recently used
            lruStoragePool.getPage(1, countingStorage);

            // Load page 3 from disk
            Page lruP3 = lruStoragePool.getPage(3, countingStorage);

            if (lruP3 == null || lruP3.getPageId() != 3) {

                throw new IllegalStateException(
                        "Storage Test 5 failed: Page 3 failed to load.");
            }

            if (lruStoragePool.size() != 2) {

                throw new IllegalStateException(
                        "Storage Test 5 failed: Pool size must remain 2, got: " + lruStoragePool.size());
            }

            if (!lruStoragePool.containsPage(1)) {

                throw new IllegalStateException(
                        "Storage Test 5 failed: Page 1 was recently accessed and should NOT be evicted.");
            }

            if (lruStoragePool.containsPage(2)) {

                throw new IllegalStateException(
                        "Storage Test 5 failed: Page 2 was least recently used and MUST be evicted.");
            }

            if (!lruStoragePool.containsPage(3)) {

                throw new IllegalStateException(
                        "Storage Test 5 failed: Page 3 must exist in pool.");
            }

            /*
             * Storage Test 6: BufferPool capacity remains bounded with multiple disk loads.
             * Pool capacity = 2. Load 1, 2, 3, 4, 5 in sequence.
             * Size must remain 2 at every step once filled.
             */
            BufferPool boundedPool = new BufferPool(2);

            for (int i = 1; i <= 5; i++) {

                boundedPool.getPage(i, countingStorage);

                int expectedSize = Math.min(i, 2);

                if (boundedPool.size() != expectedSize) {

                    throw new IllegalStateException(
                            "Storage Test 6 failed: Expected pool size " + expectedSize + " at step " + i
                                    + ", got: " + boundedPool.size());
                }
            }

            if (!boundedPool.containsPage(4) || !boundedPool.containsPage(5)) {

                throw new IllegalStateException(
                        "Storage Test 6 failed: Pages 4 and 5 must be in pool after loading 1..5.");
            }

            if (boundedPool.containsPage(1) || boundedPool.containsPage(2) || boundedPool.containsPage(3)) {

                throw new IllegalStateException(
                        "Storage Test 6 failed: Pages 1, 2, and 3 should have been evicted.");
            }

            /*
             * Storage Test 7: Pre-configured PageStorage in BufferPool constructor.
             */
            BufferPool preconfiguredPool = new BufferPool(2, countingStorage);

            if (preconfiguredPool.getPageStorage() != countingStorage) {

                throw new IllegalStateException(
                        "Storage Test 7 failed: Configured PageStorage mismatch.");
            }

            Page preP1 = preconfiguredPool.getPageFromStorage(1);

            if (preP1 == null || preP1.getPageId() != 1) {

                throw new IllegalStateException(
                        "Storage Test 7 failed: getPageFromStorage(1) failed.");
            }

            if (!preconfiguredPool.containsPage(1)) {

                throw new IllegalStateException(
                        "Storage Test 7 failed: Page 1 was not cached by getPageFromStorage.");
            }

            /*
             * ==================================================
             * OWLET-068 Dirty Page Tracking & Write-Back Tests
             * ==================================================
             */

            /*
             * Dirty Test 1: New pages are clean.
             */
            BufferPool dirtyTestPool = new BufferPool(3, countingStorage);
            Page d1 = dirtyTestPool.getPage(1, countingStorage);

            if (dirtyTestPool.isDirty(1)) {

                throw new IllegalStateException(
                        "Dirty Test 1 failed: Newly loaded page must be clean.");
            }

            Page freshPage = new Page(100, 5);
            dirtyTestPool.putPage(freshPage);

            if (dirtyTestPool.isDirty(100)) {

                throw new IllegalStateException(
                        "Dirty Test 1 failed: Newly inserted in-memory page must be clean.");
            }

            /*
             * Dirty Test 2: Mark dirty.
             */
            dirtyTestPool.markDirty(1);

            if (!dirtyTestPool.isDirty(1)) {

                throw new IllegalStateException(
                        "Dirty Test 2 failed: Expected page 1 to be marked dirty.");
            }

            /*
             * Dirty Test 3: Missing page cannot become dirty.
             */
            dirtyTestPool.markDirty(999);

            if (dirtyTestPool.isDirty(999)) {

                throw new IllegalStateException(
                        "Dirty Test 3 failed: Non-cached page 999 must not be marked dirty.");
            }

            /*
             * Dirty Test 4: Flush dirty page.
             * Modify page 1, mark dirty, flushPage, verify clean and modification persisted.
             */
            d1.addRow(new Row(List.of("101", "AliceUpdated", "150")));
            dirtyTestPool.markDirty(1);

            int savesBeforeFlush = countingStorage.saveCount;
            dirtyTestPool.flushPage(1);

            if (countingStorage.saveCount != savesBeforeFlush + 1) {

                throw new IllegalStateException(
                        "Dirty Test 4 failed: flushPage(1) did not invoke storage.savePage.");
            }

            if (dirtyTestPool.isDirty(1)) {

                throw new IllegalStateException(
                        "Dirty Test 4 failed: Page 1 should be clean after successful flushPage.");
            }

            // Verify modification on disk
            Page reloadedFromDisk1 = countingStorage.loadPage(1);

            if (reloadedFromDisk1 == null || reloadedFromDisk1.getRowCount() != 2) {

                throw new IllegalStateException(
                        "Dirty Test 4 failed: Modification not found on disk after flushPage.");
            }

            if (!reloadedFromDisk1.getRows().get(1).getValues().equals(List.of("101", "AliceUpdated", "150"))) {

                throw new IllegalStateException(
                        "Dirty Test 4 failed: Row content mismatch on disk reload.");
            }

            /*
             * Dirty Test 5: Clean page does not require write.
             */
            int savesBeforeCleanFlush = countingStorage.saveCount;
            dirtyTestPool.flushPage(1);

            if (countingStorage.saveCount != savesBeforeCleanFlush) {

                throw new IllegalStateException(
                        "Dirty Test 5 failed: Flushing a clean page must not cause disk writes.");
            }

            if (dirtyTestPool.isDirty(1)) {

                throw new IllegalStateException(
                        "Dirty Test 5 failed: Clean page should remain clean.");
            }

            /*
             * Dirty Test 6: Dirty page eviction writes back to disk.
             * Capacity = 2.
             * Load 1, 2. Modify 1, mark dirty.
             * Page 1 is LRU victim (loaded before 2).
             * Load 3 -> page 1 is written to disk, evicted, page 3 inserted.
             */
            BufferPool evictPool = new BufferPool(2, countingStorage);
            Page ep1 = evictPool.getPage(1, countingStorage);
            Page ep2 = evictPool.getPage(2, countingStorage);

            ep1.addRow(new Row(List.of("102", "AliceThirdRow", "175")));
            evictPool.markDirty(1);

            int savesBeforeEviction = countingStorage.saveCount;

            // Loading page 3 forces eviction of LRU page 1
            Page ep3 = evictPool.getPage(3, countingStorage);

            if (countingStorage.saveCount != savesBeforeEviction + 1) {

                throw new IllegalStateException(
                        "Dirty Test 6 failed: Evicting dirty page 1 should write it to disk.");
            }

            if (evictPool.containsPage(1)) {

                throw new IllegalStateException(
                        "Dirty Test 6 failed: Page 1 should be evicted from BufferPool.");
            }

            if (evictPool.isDirty(1)) {

                throw new IllegalStateException(
                        "Dirty Test 6 failed: Evicted page 1 must not remain in dirtyPages.");
            }

            if (!evictPool.containsPage(2) || !evictPool.containsPage(3)) {

                throw new IllegalStateException(
                        "Dirty Test 6 failed: Pool should contain pages 2 and 3.");
            }

            // Verify page 1 on disk has the 3 rows
            Page diskEp1 = countingStorage.loadPage(1);

            if (diskEp1 == null || diskEp1.getRowCount() != 3) {

                throw new IllegalStateException(
                        "Dirty Test 6 failed: Disk did not persist modification of evicted page 1.");
            }

            /*
             * Dirty Test 7: Clean page eviction does not write to disk.
             * Capacity = 2. Pool currently has 2 (clean) and 3 (clean).
             * LRU is page 2.
             * Load page 4.
             */
            int savesBeforeCleanEvict = countingStorage.saveCount;
            Page ep4 = evictPool.getPage(4, countingStorage);

            if (countingStorage.saveCount != savesBeforeCleanEvict) {

                throw new IllegalStateException(
                        "Dirty Test 7 failed: Evicting clean page 2 must not cause disk writes.");
            }

            if (evictPool.containsPage(2)) {

                throw new IllegalStateException(
                        "Dirty Test 7 failed: Page 2 should be evicted.");
            }

            if (!evictPool.containsPage(3) || !evictPool.containsPage(4)) {

                throw new IllegalStateException(
                        "Dirty Test 7 failed: Pool should contain pages 3 and 4.");
            }

            /*
             * Dirty Test 8: Flush all.
             * Load multiple pages, modify multiple, mark dirty, flushAll().
             * Verify all become clean and disk reflects modifications.
             */
            BufferPool flushAllPool = new BufferPool(5, countingStorage);
            Page fa1 = flushAllPool.getPage(1, countingStorage);
            Page fa2 = flushAllPool.getPage(2, countingStorage);
            Page fa3 = flushAllPool.getPage(3, countingStorage);

            fa2.addRow(new Row(List.of("201", "BobUpdated", "250")));
            flushAllPool.markDirty(2);

            fa3.addRow(new Row(List.of("301", "CharlieUpdated", "350")));
            flushAllPool.markDirty(3);

            if (!flushAllPool.isDirty(2) || !flushAllPool.isDirty(3) || flushAllPool.isDirty(1)) {

                throw new IllegalStateException(
                        "Dirty Test 8 failed: Initial dirty states before flushAll are incorrect.");
            }

            flushAllPool.flushAll();

            if (flushAllPool.isDirty(1) || flushAllPool.isDirty(2) || flushAllPool.isDirty(3)) {

                throw new IllegalStateException(
                        "Dirty Test 8 failed: All pages should be clean after flushAll().");
            }

            Page diskFa2 = countingStorage.loadPage(2);
            Page diskFa3 = countingStorage.loadPage(3);

            if (diskFa2.getRowCount() != 2 || diskFa3.getRowCount() != 2) {

                throw new IllegalStateException(
                        "Dirty Test 8 failed: Modifications were not persisted by flushAll().");
            }

            /*
             * Dirty Test 9: Write failure preserves dirty page in cache and prevents eviction.
             * Pool capacity = 2.
             * Contains: Page 1 (DIRTY), Page 2 (CLEAN).
             * LRU victim is Page 1.
             * Set failSave = true.
             * Try to insert Page 3.
             * Writing Page 1 fails -> IOException thrown.
             * Page 1 remains cached, remains dirty. Page 3 is NOT inserted.
             */
            BufferPool failPool = new BufferPool(2, countingStorage);
            Page fp1 = failPool.getPage(1, countingStorage);
            Page fp2 = failPool.getPage(2, countingStorage);

            fp1.addRow(new Row(List.of("103", "AliceFailedRow", "999")));
            failPool.markDirty(1);

            countingStorage.failSave = true;

            boolean caughtWriteFailure = false;

            try {

                failPool.getPage(3, countingStorage);

            } catch (IOException expected) {

                caughtWriteFailure = true;
            }

            countingStorage.failSave = false;

            if (!caughtWriteFailure) {

                throw new IllegalStateException(
                        "Dirty Test 9 failed: Expected IOException when disk write fails during eviction.");
            }

            if (!failPool.containsPage(1)) {

                throw new IllegalStateException(
                        "Dirty Test 9 failed: Dirty page 1 must NOT be evicted on write failure.");
            }

            if (!failPool.isDirty(1)) {

                throw new IllegalStateException(
                        "Dirty Test 9 failed: Dirty page 1 must remain DIRTY on write failure.");
            }

            if (failPool.containsPage(3)) {

                throw new IllegalStateException(
                        "Dirty Test 9 failed: Page 3 must NOT be inserted when eviction fails.");
            }

            if (failPool.size() != 2) {

                throw new IllegalStateException(
                        "Dirty Test 9 failed: Pool size should remain 2, got: " + failPool.size());
            }

            // Now with failSave = false, flushPage succeeds and clean eviction works
            failPool.flushPage(1);

            if (failPool.isDirty(1)) {

                throw new IllegalStateException(
                        "Dirty Test 9 failed: Page 1 should be clean after successful flush.");
            }

            failPool.getPage(3, countingStorage);

            if (!failPool.containsPage(3) || failPool.containsPage(1)) {

                throw new IllegalStateException(
                        "Dirty Test 9 failed: Clean eviction should succeed once flushed.");
            }

            /*
             * ==================================================
             * OWLET-069 Page Pinning and Unpinning Tests
             * ==================================================
             */

            /*
             * Pin Test 1: Basic pinning.
             * Capacity 2. Insert page 1.
             * Verify getPinCount(1) == 0.
             * Call pinPage(1).
             * Verify getPinCount(1) == 1 and isPinned(1) == true.
             */
            BufferPool pinPool1 = new BufferPool(2);
            pinPool1.putPage(new Page(1, 5));

            if (pinPool1.getPinCount(1) != 0 || pinPool1.isPinned(1)) {

                throw new IllegalStateException(
                        "Pin Test 1 failed: Initial pin count must be 0.");
            }

            pinPool1.pinPage(1);

            if (pinPool1.getPinCount(1) != 1 || !pinPool1.isPinned(1)) {

                throw new IllegalStateException(
                        "Pin Test 1 failed: Pin count must be 1 after pinPage(1).");
            }

            /*
             * Pin Test 2: Multiple pins and unpins.
             * pinPage(1) again -> pin count 2.
             * unpinPage(1) -> pin count 1.
             * unpinPage(1) -> pin count 0, isPinned false.
             */
            pinPool1.pinPage(1);

            if (pinPool1.getPinCount(1) != 2 || !pinPool1.isPinned(1)) {

                throw new IllegalStateException(
                        "Pin Test 2 failed: Pin count must be 2 after second pinPage.");
            }

            pinPool1.unpinPage(1);

            if (pinPool1.getPinCount(1) != 1 || !pinPool1.isPinned(1)) {

                throw new IllegalStateException(
                        "Pin Test 2 failed: Pin count must be 1 after first unpinPage.");
            }

            pinPool1.unpinPage(1);

            if (pinPool1.getPinCount(1) != 0 || pinPool1.isPinned(1)) {

                throw new IllegalStateException(
                        "Pin Test 2 failed: Pin count must be 0 after second unpinPage.");
            }

            /*
             * Pin Test 3: Pinned page cannot be evicted.
             * capacity = 2. Insert 1, 2.
             * Pin page 1. Page 1 is LRU victim (inserted first).
             * Insert page 3.
             * Page 1 remains, page 2 is evicted, page 3 exists.
             */
            BufferPool pinPool3 = new BufferPool(2);
            pinPool3.putPage(new Page(1, 5));
            pinPool3.putPage(new Page(2, 5));

            pinPool3.pinPage(1);

            pinPool3.putPage(new Page(3, 5));

            if (!pinPool3.containsPage(1)) {

                throw new IllegalStateException(
                        "Pin Test 3 failed: Pinned page 1 must NOT be evicted.");
            }

            if (pinPool3.containsPage(2)) {

                throw new IllegalStateException(
                        "Pin Test 3 failed: Unpinned page 2 should have been evicted.");
            }

            if (!pinPool3.containsPage(3)) {

                throw new IllegalStateException(
                        "Pin Test 3 failed: Page 3 must be in buffer pool.");
            }

            if (pinPool3.size() != 2) {

                throw new IllegalStateException(
                        "Pin Test 3 failed: Pool size must remain 2, got: " + pinPool3.size());
            }

            /*
             * Pin Test 4: Unpinned page can be evicted.
             * capacity = 2. Insert 1, 2. Pin 1.
             * Insert 3 -> page 2 is evicted.
             * Then unpin 1.
             * Verify page 1 and page 3 remain.
             * Insert 4 -> page 1 (now unpinned and LRU) is evicted!
             */
            BufferPool pinPool4 = new BufferPool(2);
            pinPool4.putPage(new Page(1, 5));
            pinPool4.putPage(new Page(2, 5));

            pinPool4.pinPage(1);
            pinPool4.putPage(new Page(3, 5));

            pinPool4.unpinPage(1);

            if (!pinPool4.containsPage(1) || !pinPool4.containsPage(3)) {

                throw new IllegalStateException(
                        "Pin Test 4 failed: Pages 1 and 3 should remain after unpinning.");
            }

            pinPool4.putPage(new Page(4, 5));

            if (pinPool4.containsPage(1)) {

                throw new IllegalStateException(
                        "Pin Test 4 failed: Unpinned page 1 should be evicted as LRU victim.");
            }

            if (!pinPool4.containsPage(3) || !pinPool4.containsPage(4)) {

                throw new IllegalStateException(
                        "Pin Test 4 failed: Pages 3 and 4 should be in pool.");
            }

            /*
             * Pin Test 5: All pages pinned.
             * capacity = 2. Insert 1, 2. Pin 1, 2.
             * Try inserting page 3 -> IllegalStateException.
             * Verify size == 2, page 1 exists, page 2 exists, page 3 does not exist.
             */
            BufferPool pinPool5 = new BufferPool(2);
            pinPool5.putPage(new Page(1, 5));
            pinPool5.putPage(new Page(2, 5));

            pinPool5.pinPage(1);
            pinPool5.pinPage(2);

            boolean caughtAllPinned = false;

            try {

                pinPool5.putPage(new Page(3, 5));

            } catch (IllegalStateException expected) {

                caughtAllPinned = true;
            }

            if (!caughtAllPinned) {

                throw new IllegalStateException(
                        "Pin Test 5 failed: Expected IllegalStateException when all pages are pinned.");
            }

            if (pinPool5.size() != 2) {

                throw new IllegalStateException(
                        "Pin Test 5 failed: Pool size should remain 2, got: " + pinPool5.size());
            }

            if (!pinPool5.containsPage(1) || !pinPool5.containsPage(2)) {

                throw new IllegalStateException(
                        "Pin Test 5 failed: Pinned pages 1 and 2 must remain in pool.");
            }

            if (pinPool5.containsPage(3)) {

                throw new IllegalStateException(
                        "Pin Test 5 failed: Page 3 must NOT be inserted when all pages are pinned.");
            }

            /*
             * Pin Test 6: Dirty + pinned.
             * capacity = 2. Load page 1, page 2.
             * Modify page 1, mark dirty. Pin page 1.
             * Load page 3.
             * Verify page 1 remains in cache and is dirty.
             * Page 2 is evicted. Page 3 exists.
             */
            BufferPool pinPool6 = new BufferPool(2, countingStorage);
            Page pp1 = pinPool6.getPage(1, countingStorage);
            Page pp2 = pinPool6.getPage(2, countingStorage);

            pp1.addRow(new Row(List.of("104", "AlicePinnedRow", "888")));
            pinPool6.markDirty(1);
            pinPool6.pinPage(1);

            pinPool6.getPage(3, countingStorage);

            if (!pinPool6.containsPage(1)) {

                throw new IllegalStateException(
                        "Pin Test 6 failed: Dirty pinned page 1 must NOT be evicted.");
            }

            if (!pinPool6.isDirty(1)) {

                throw new IllegalStateException(
                        "Pin Test 6 failed: Dirty pinned page 1 must remain dirty.");
            }

            if (pinPool6.containsPage(2)) {

                throw new IllegalStateException(
                        "Pin Test 6 failed: Clean unpinned page 2 should have been evicted.");
            }

            if (!pinPool6.containsPage(3)) {

                throw new IllegalStateException(
                        "Pin Test 6 failed: Page 3 must be in pool.");
            }

            /*
             * Pin Test 7: Dirty + unpinned.
             * Unpin page 1, cause eviction by loading page 4.
             * Page 1 is safely written to disk, evicted, modification survives on disk.
             */
            pinPool6.unpinPage(1);

            int savesBeforePinEvict = countingStorage.saveCount;
            pinPool6.getPage(4, countingStorage);

            if (countingStorage.saveCount != savesBeforePinEvict + 1) {

                throw new IllegalStateException(
                        "Pin Test 7 failed: Evicting unpinned dirty page 1 should write it to disk.");
            }

            if (pinPool6.containsPage(1)) {

                throw new IllegalStateException(
                        "Pin Test 7 failed: Unpinned dirty page 1 should be evicted.");
            }

            Page diskPp1 = countingStorage.loadPage(1);

            if (diskPp1 == null || !diskPp1.getRows().get(diskPp1.getRowCount() - 1).getValues().equals(
                    List.of("104", "AlicePinnedRow", "888"))) {

                throw new IllegalStateException(
                        "Pin Test 7 failed: Modification of unpinned dirty page 1 was not persisted.");
            }

            /*
             * Pin Test 8: Invalid unpin.
             * Insert page 1. Pin count is 0. Call unpinPage(1).
             * Expected: clear failure (IllegalStateException).
             * Pin count must remain 0, never negative.
             */
            BufferPool pinPool8 = new BufferPool(2);
            pinPool8.putPage(new Page(1, 5));

            boolean caughtInvalidUnpin = false;

            try {

                pinPool8.unpinPage(1);

            } catch (IllegalStateException expected) {

                caughtInvalidUnpin = true;
            }

            if (!caughtInvalidUnpin) {

                throw new IllegalStateException(
                        "Pin Test 8 failed: Expected IllegalStateException when unpinning page with pin count 0.");
            }

            if (pinPool8.getPinCount(1) != 0) {

                throw new IllegalStateException(
                        "Pin Test 8 failed: Pin count must remain 0, got: " + pinPool8.getPinCount(1));
            }

            /*
             * Pin Test 9: Missing page.
             * pinPage(999) and unpinPage(999) on uncached page must not create fake pin entries.
             */
            BufferPool pinPool9 = new BufferPool(2);
            pinPool9.pinPage(999);

            if (pinPool9.getPinCount(999) != 0 || pinPool9.isPinned(999)) {

                throw new IllegalStateException(
                        "Pin Test 9 failed: pinPage(999) on missing page must not create pin entry.");
            }

            pinPool9.unpinPage(999);

            if (pinPool9.getPinCount(999) != 0 || pinPool9.isPinned(999)) {

                throw new IllegalStateException(
                        "Pin Test 9 failed: unpinPage(999) on missing page must not create pin entry.");
            }

            /*
             * Pin Test 10: Capacity 1.
             * capacity = 1. Insert page 1. Pin page 1.
             * Attempt to insert page 2 -> IllegalStateException.
             * Unpin page 1.
             * Insert page 2 -> page 1 is evicted, page 2 exists, size is 1.
             */
            BufferPool pinPool10 = new BufferPool(1);
            pinPool10.putPage(new Page(1, 5));
            pinPool10.pinPage(1);

            boolean caughtCap1 = false;

            try {

                pinPool10.putPage(new Page(2, 5));

            } catch (IllegalStateException expected) {

                caughtCap1 = true;
            }

            if (!caughtCap1) {

                throw new IllegalStateException(
                        "Pin Test 10 failed: Expected failure when inserting into capacity 1 with pinned page.");
            }

            if (pinPool10.size() != 1 || !pinPool10.containsPage(1) || pinPool10.containsPage(2)) {

                throw new IllegalStateException(
                        "Pin Test 10 failed: Page 1 must remain when capacity 1 insert fails.");
            }

            pinPool10.unpinPage(1);
            pinPool10.putPage(new Page(2, 5));

            if (pinPool10.size() != 1 || pinPool10.containsPage(1) || !pinPool10.containsPage(2)) {

                throw new IllegalStateException(
                        "Pin Test 10 failed: Page 1 should be evicted after unpinning in capacity 1 pool.");
            }

            /*
             * Pin Test 11: Clear removes pin metadata.
             */
            BufferPool pinPool11 = new BufferPool(2);
            pinPool11.putPage(new Page(1, 5));
            pinPool11.pinPage(1);
            pinPool11.clear();

            if (pinPool11.getPinCount(1) != 0 || pinPool11.isPinned(1)) {

                throw new IllegalStateException(
                        "Pin Test 11 failed: clear() must remove all pin metadata.");
            }

            /*
             * ==================================================
             * OWLET-070 Safe Page Lifecycle API Tests
             * ==================================================
             */

            /*
             * Lifecycle Test 1: getPageAndPin, LRU eviction protection, and releasePage.
             * 1. Create a BufferPool with capacity = 2.
             * 2. Load page 1.
             * 3. Call getPageAndPin(1).
             * 4. Verify getPinCount(1) == 1.
             * 5. Make page 1 the LRU candidate by accessing/loading page 2.
             * 6. Fill the BufferPool (contains 1 and 2).
             * 7. Cause an eviction (put page 3).
             * 8. Verify page 1 is NOT evicted (unpinned page 2 was evicted instead).
             * 9. Call releasePage(1).
             * 10. Verify getPinCount(1) == 0.
             * 11. Cause another eviction (put page 4).
             * 12. Verify page 1 can now be evicted according to LRU.
             */
            BufferPool lcPool1 = new BufferPool(2);
            lcPool1.putPage(new Page(1, 5));
            Page lcPage1 = lcPool1.getPageAndPin(1);

            if (lcPage1 == null || lcPage1.getPageId() != 1) {

                throw new IllegalStateException(
                        "Lifecycle Test 1 failed: getPageAndPin(1) should return page 1.");
            }

            if (lcPool1.getPinCount(1) != 1 || !lcPool1.isPinned(1)) {

                throw new IllegalStateException(
                        "Lifecycle Test 1 failed: getPinCount(1) must be 1 after getPageAndPin(1).");
            }

            // Put page 2 so pool is full and page 1 is the oldest / LRU candidate
            lcPool1.putPage(new Page(2, 5));

            // Cause eviction by putting page 3
            lcPool1.putPage(new Page(3, 5));

            // Page 1 is pinned, so page 2 must be evicted instead
            if (!lcPool1.containsPage(1)) {

                throw new IllegalStateException(
                        "Lifecycle Test 1 failed: Pinned page 1 must NOT be evicted.");
            }

            if (lcPool1.containsPage(2)) {

                throw new IllegalStateException(
                        "Lifecycle Test 1 failed: Unpinned page 2 should have been evicted.");
            }

            if (!lcPool1.containsPage(3)) {

                throw new IllegalStateException(
                        "Lifecycle Test 1 failed: Page 3 must be present in pool.");
            }

            // Release page 1
            lcPool1.releasePage(1);

            if (lcPool1.getPinCount(1) != 0 || lcPool1.isPinned(1)) {

                throw new IllegalStateException(
                        "Lifecycle Test 1 failed: getPinCount(1) must be 0 after releasePage(1).");
            }

            // Cause another eviction by putting page 4. Page 1 is still older than page 3 and now unpinned!
            lcPool1.putPage(new Page(4, 5));

            if (lcPool1.containsPage(1)) {

                throw new IllegalStateException(
                        "Lifecycle Test 1 failed: Page 1 should now be evicted after being released.");
            }

            if (!lcPool1.containsPage(3) || !lcPool1.containsPage(4)) {

                throw new IllegalStateException(
                        "Lifecycle Test 1 failed: Pages 3 and 4 should be retained in the pool.");
            }

            /*
             * Lifecycle Test 2: Dirty Page Lifecycle.
             * getPageAndPin(1) -> modify page -> markDirty(1) -> releasePage(1)
             * Then force eviction.
             * Expected:
             * - page 1 was not evicted while pinned
             * - after release, page 1 becomes eligible
             * - when eventually evicted, existing OWLET-068 write-back occurs
             * - modification survives on disk
             */
            lifecyclePageFile = File.createTempFile("owldb_bp_lc_page_", ".data");
            lifecyclePageFile.deleteOnExit();

            lifecycleMetaFile = File.createTempFile("owldb_bp_lc_meta_", ".data");
            lifecycleMetaFile.deleteOnExit();
            lifecycleMetaFile.delete();

            CountingPageStorage lcStorage = new CountingPageStorage(lifecyclePageFile, lifecycleMetaFile);

            Page initP1 = new Page(1, 5);
            initP1.addRow(new Row(List.of("1", "Original", "100")));
            lcStorage.savePage(initP1);

            Page initP2 = new Page(2, 5);
            initP2.addRow(new Row(List.of("2", "Second", "200")));
            lcStorage.savePage(initP2);

            Page initP3 = new Page(3, 5);
            initP3.addRow(new Row(List.of("3", "Third", "300")));
            lcStorage.savePage(initP3);

            lcStorage.savePageLocations();

            BufferPool lcDirtyPool = new BufferPool(2, lcStorage);

            // getPageAndPin(1) loads page 1 and pins it
            Page dp1 = lcDirtyPool.getPageAndPin(1);
            if (dp1 == null || lcDirtyPool.getPinCount(1) != 1) {

                throw new IllegalStateException(
                        "Lifecycle Test 2 failed: Page 1 must be loaded and pinned.");
            }

            // Modify page and mark dirty
            dp1.addRow(new Row(List.of("10", "Modified", "999")));
            lcDirtyPool.markDirty(1);

            if (!lcDirtyPool.isDirty(1)) {

                throw new IllegalStateException(
                        "Lifecycle Test 2 failed: Page 1 must be marked dirty.");
            }

            // Load page 2 (pool now full with 1 and 2, page 1 is LRU)
            lcDirtyPool.getPageFromStorage(2);

            // Try to evict by loading page 3 while page 1 is still pinned
            lcDirtyPool.getPageFromStorage(3);

            // Page 1 is pinned and dirty; page 2 is clean and unpinned, so page 2 was evicted!
            if (!lcDirtyPool.containsPage(1)) {

                throw new IllegalStateException(
                        "Lifecycle Test 2 failed: Pinned dirty page 1 must NOT be evicted.");
            }

            // Release page 1
            lcDirtyPool.releasePage(1);

            if (lcDirtyPool.getPinCount(1) != 0) {

                throw new IllegalStateException(
                        "Lifecycle Test 2 failed: Pin count must be 0 after release.");
            }

            // Page 1 must STILL be dirty and cached (releasePage does not auto-flush)
            if (!lcDirtyPool.isDirty(1)) {

                throw new IllegalStateException(
                        "Lifecycle Test 2 failed: releasePage must not clear dirty flag or force flush.");
            }

            // Now load page 2 back. Since pool has 1 (older, unpinned) and 3 (newer), page 1 is evicted!
            int lcSavesBeforeEviction = lcStorage.saveCount;
            lcDirtyPool.getPageFromStorage(2);

            // Page 1 should now be evicted
            if (lcDirtyPool.containsPage(1)) {

                throw new IllegalStateException(
                        "Lifecycle Test 2 failed: Page 1 should be evicted once released and LRU victim.");
            }

            // Eviction of dirty page 1 must trigger write-back to storage
            if (lcStorage.saveCount <= lcSavesBeforeEviction) {

                throw new IllegalStateException(
                        "Lifecycle Test 2 failed: Evicting dirty page 1 must trigger write-back to storage.");
            }

            // Verify modification survived by reading from storage
            Page verifiedP1 = lcStorage.loadPage(1);

            if (verifiedP1 == null || verifiedP1.getRows().size() != 2) {

                throw new IllegalStateException(
                        "Lifecycle Test 2 failed: Modified rows did not survive on disk after write-back.");
            }

            if (!"Modified".equals(verifiedP1.getRows().get(1).getValues().get(1))) {

                throw new IllegalStateException(
                        "Lifecycle Test 2 failed: Modified row content does not match expected value.");
            }

            /*
             * Lifecycle Test 3: Pinning + Cache Miss.
             * Empty BufferPool configured with PageStorage.
             * Call getPageAndPin(1).
             * Expected: disk -> load page -> insert into BufferPool -> pin page -> return page.
             * Verify containsPage(1) == true and getPinCount(1) == 1.
             */
            BufferPool lcMissPool = new BufferPool(3, lcStorage);

            if (lcMissPool.containsPage(1)) {

                throw new IllegalStateException(
                        "Lifecycle Test 3 failed: Pool should initially be empty.");
            }

            Page missP1 = lcMissPool.getPageAndPin(1);

            if (missP1 == null || missP1.getPageId() != 1) {

                throw new IllegalStateException(
                        "Lifecycle Test 3 failed: getPageAndPin(1) should load and return page 1.");
            }

            if (!lcMissPool.containsPage(1)) {

                throw new IllegalStateException(
                        "Lifecycle Test 3 failed: Page 1 must be inserted into BufferPool on cache miss.");
            }

            if (lcMissPool.getPinCount(1) != 1 || !lcMissPool.isPinned(1)) {

                throw new IllegalStateException(
                        "Lifecycle Test 3 failed: Page 1 must have pin count 1 after getPageAndPin.");
            }

            /*
             * Lifecycle Test 4: Cache Hit.
             * Page 1 is already cached in lcMissPool.
             * Calling getPageAndPin(1) must:
             * - return cached page
             * - increment pin count
             * - update normal cache access behavior
             * - NOT reload page from disk
             */
            int lcLoadsBeforeHit = lcStorage.loadCount;
            Page lcHitP1 = lcMissPool.getPageAndPin(1);

            if (lcHitP1 != missP1) {

                throw new IllegalStateException(
                        "Lifecycle Test 4 failed: Cache hit should return identical cached Page reference.");
            }

            if (lcStorage.loadCount != lcLoadsBeforeHit) {

                throw new IllegalStateException(
                        "Lifecycle Test 4 failed: Cache hit must NOT reload page from disk.");
            }

            if (lcMissPool.getPinCount(1) != 2) {

                throw new IllegalStateException(
                        "Lifecycle Test 4 failed: Pin count must increment to 2 on second getPageAndPin.");
            }

            // Release both pins
            lcMissPool.releasePage(1);
            if (lcMissPool.getPinCount(1) != 1) {

                throw new IllegalStateException(
                        "Lifecycle Test 4 failed: Pin count must be 1 after first release.");
            }

            lcMissPool.releasePage(1);
            if (lcMissPool.getPinCount(1) != 0 || lcMissPool.isPinned(1)) {

                throw new IllegalStateException(
                        "Lifecycle Test 4 failed: Pin count must be 0 after second release.");
            }

            /*
             * Lifecycle Test 5: Missing Page.
             * Calling getPageAndPin(nonExistingPage):
             * - returns null
             * - does not create a page
             * - does not create pin metadata
             * - does not increase BufferPool size
             * Calling releasePage(nonExistingPage):
             * - handled safely and cleanly without exception
             */
            int sizeBeforeMissing = lcMissPool.size();
            Page missingPage = lcMissPool.getPageAndPin(9999);

            if (missingPage != null) {

                throw new IllegalStateException(
                        "Lifecycle Test 5 failed: Missing page must return null.");
            }

            if (lcMissPool.containsPage(9999)) {

                throw new IllegalStateException(
                        "Lifecycle Test 5 failed: Missing page must not be stored in pool.");
            }

            if (lcMissPool.getPinCount(9999) != 0 || lcMissPool.isPinned(9999)) {

                throw new IllegalStateException(
                        "Lifecycle Test 5 failed: Missing page must not have pin metadata.");
            }

            if (lcMissPool.size() != sizeBeforeMissing) {

                throw new IllegalStateException(
                        "Lifecycle Test 5 failed: BufferPool size must not increase for missing page.");
            }

            // Releasing non-existing page should be safe and no-op
            lcMissPool.releasePage(9999);

            if (lcMissPool.getPinCount(9999) != 0) {

                throw new IllegalStateException(
                        "Lifecycle Test 5 failed: Non-existing page pin count must remain 0.");
            }

            /*
             * Lifecycle Test 6: Double Release.
             * getPageAndPin(1) -> pin count = 1
             * releasePage(1)   -> pin count = 0
             * releasePage(1)   -> throws IllegalStateException, pin count remains 0.
             */
            lcMissPool.getPageAndPin(1);
            if (lcMissPool.getPinCount(1) != 1) {

                throw new IllegalStateException(
                        "Lifecycle Test 6 failed: Pin count must be 1.");
            }

            lcMissPool.releasePage(1);
            if (lcMissPool.getPinCount(1) != 0) {

                throw new IllegalStateException(
                        "Lifecycle Test 6 failed: Pin count must be 0 after release.");
            }

            boolean doubleReleaseFailed = false;

            try {

                lcMissPool.releasePage(1);

            } catch (IllegalStateException e) {

                doubleReleaseFailed = true;
            }

            if (!doubleReleaseFailed) {

                throw new IllegalStateException(
                        "Lifecycle Test 6 failed: Double release must throw IllegalStateException.");
            }

            if (lcMissPool.getPinCount(1) != 0) {

                throw new IllegalStateException(
                        "Lifecycle Test 6 failed: Pin count must not become negative.");
            }

            /*
             * Lifecycle Test 7: putPage overwrite protection for pinned pages.
             * A page that is currently pinned cannot be replaced via putPage.
             * Once released (pin count 0), replacement is permitted.
             */
            BufferPool putSafetyPool = new BufferPool(2);
            putSafetyPool.putPage(new Page(1, 5));
            putSafetyPool.getPageAndPin(1);

            boolean overwriteBlocked = false;

            try {

                putSafetyPool.putPage(new Page(1, 10));

            } catch (IllegalStateException e) {

                overwriteBlocked = true;
            }

            if (!overwriteBlocked) {

                throw new IllegalStateException(
                        "Lifecycle Test 7 failed: putPage must not overwrite a pinned page.");
            }

            // Release page 1, then putPage should succeed
            putSafetyPool.releasePage(1);
            Page newPage1 = new Page(1, 10);
            putSafetyPool.putPage(newPage1);

            if (putSafetyPool.getPage(1) != newPage1 || putSafetyPool.getPage(1).getMaxRows() != 10) {

                throw new IllegalStateException(
                        "Lifecycle Test 7 failed: putPage should succeed once page is released.");
            }

            /*
             * Lifecycle Test 8: getPageAndPin(pageId, storage) with explicit storage parameter.
             */
            BufferPool explicitStoragePool = new BufferPool(2);
            Page expP2 = explicitStoragePool.getPageAndPin(2, lcStorage);

            if (expP2 == null || expP2.getPageId() != 2) {

                throw new IllegalStateException(
                        "Lifecycle Test 8 failed: getPageAndPin(2, storage) should return page 2.");
            }

            if (explicitStoragePool.getPinCount(2) != 1) {

                throw new IllegalStateException(
                        "Lifecycle Test 8 failed: Pin count must be 1.");
            }

            explicitStoragePool.releasePage(2);

            if (explicitStoragePool.getPinCount(2) != 0) {

                throw new IllegalStateException(
                        "Lifecycle Test 8 failed: Pin count must be 0 after release.");
            }

            System.out.println("BufferPool regression tests passed successfully.");

        } finally {

            if (tempPageFile != null && tempPageFile.exists()) {

                tempPageFile.delete();
            }

            if (tempMetaFile != null && tempMetaFile.exists()) {

                tempMetaFile.delete();
            }

            if (lifecyclePageFile != null && lifecyclePageFile.exists()) {

                lifecyclePageFile.delete();
            }

            if (lifecycleMetaFile != null && lifecycleMetaFile.exists()) {

                lifecycleMetaFile.delete();
            }
        }
    }

    /*
     * Test-only PageStorage extension to count reads/writes and simulate write failure.
     */
    static class CountingPageStorage extends PageStorage {

        int loadCount;

        int saveCount;

        boolean failSave;

        CountingPageStorage(
                File pageFile,
                File locationFile) throws IOException {

            super(pageFile, locationFile);
            this.loadCount = 0;
            this.saveCount = 0;
            this.failSave = false;
        }

        @Override
        public Page loadPage(
                int pageId) throws IOException {

            loadCount++;
            return super.loadPage(pageId);
        }

        @Override
        public long savePage(
                Page page) throws IOException {

            if (failSave) {

                throw new IOException("Simulated disk write failure for testing.");
            }

            saveCount++;
            return super.savePage(page);
        }
    }
}
