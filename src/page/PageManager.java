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

        pages.add(
                new Page(
                        0,
                        pageSize));
    }

    /*
     * Insert row into pages.
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

    public List<Page> getPages() {

        return pages;
    }
}