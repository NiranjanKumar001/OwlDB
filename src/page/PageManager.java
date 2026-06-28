package page;

import row.Row;

import java.util.ArrayList;
import java.util.List;

/*
 * Manages table pages.
 */
public class PageManager {

        private List<Page> pages;

        private int pageSize;

        public PageManager(
                        int pageSize) {

                this.pageSize = pageSize;

                this.pages = new ArrayList<>();

                /*
                 * Create first page.
                 */
                pages.add(
                                new Page(
                                                0,
                                                pageSize));
        }

        /*
         * Insert row.
         */
        public void insertRow(
                        Row row) {

                Page currentPage = pages.get(
                                pages.size() - 1);

                if (currentPage.isFull()) {

                        Page newPage = new Page(
                                        pages.size(),
                                        pageSize);

                        pages.add(
                                        newPage);

                        currentPage = newPage;
                }

                currentPage.addRow(
                                row);
        }

        /*
         * Insert row and return RID.
         */
        public RID insertAndReturnRID(
                        Row row) {

                Page currentPage = pages.get(
                                pages.size() - 1);

                if (currentPage.isFull()) {

                        Page newPage = new Page(
                                        pages.size(),
                                        pageSize);

                        pages.add(
                                        newPage);

                        currentPage = newPage;
                }

                int slotId = currentPage.getRows()
                                .size();

                currentPage.addRow(
                                row);

                return new RID(
                                currentPage.getPageId(),
                                slotId);
        }

        public List<Page> getPages() {

                return pages;
        }

        /*
         * Get row using RID.
         */
        public Row getRow(
                        RID rid) {

                int pageId = rid.getPageId();

                int slotId = rid.getSlotId();

                if (pageId >= pages.size()) {

                        return null;
                }

                Page page = pages.get(
                                pageId);

                if (slotId >= page.getRows()
                                .size()) {

                        return null;
                }

                return page.getRows()
                                .get(slotId);
        }

        public void printPages() {

                System.out.println("\nPage Layout:");

                for (Page page : pages) {

                        System.out.println(
                                        "Page "
                                                        + page.getPageId()
                                                        + " ("
                                                        + page.getRowCount()
                                                        + "/"
                                                        + page.getMaxRows()
                                                        + ")");

                        for (Row row : page.getRows()) {

                                System.out.println(
                                                "  " + row.getValues());
                        }
                }
        }

        public int getPageCount() {

                return pages.size();
        }

        public int getTotalRowCount() {

                int total = 0;

                for (Page page : pages) {

                        total += page.getRowCount();
                }

                return total;
        }
}