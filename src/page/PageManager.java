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

        this.pageSize =
                pageSize;

        this.pages =
                new ArrayList<>();

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

        Page currentPage =
                pages.get(
                        pages.size() - 1);

        if (currentPage.isFull()) {

            Page newPage =
                    new Page(
                            pages.size(),
                            pageSize);

            pages.add(
                    newPage);

            currentPage =
                    newPage;
        }

        currentPage.addRow(
                row);
    }

    /*
     * Insert row and return RID.
     */
    public RID insertAndReturnRID(
            Row row) {

        Page currentPage =
                pages.get(
                        pages.size() - 1);

        if (currentPage.isFull()) {

            Page newPage =
                    new Page(
                            pages.size(),
                            pageSize);

            pages.add(
                    newPage);

            currentPage =
                    newPage;
        }

        int slotId =
                currentPage.getRows()
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
}