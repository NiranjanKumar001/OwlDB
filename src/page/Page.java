package page;

import row.Row;

import java.util.ArrayList;
import java.util.List;

/*
 * Represents a disk page.
 *
 * PostgreSQL uses 8KB pages.
 *
 * For OwlDB we start simple:
 * fixed number of rows.
 */
public class Page {

    private int pageId;

    private List<Row> rows;

    private int maxRows;

    public Page(
            int pageId,
            int maxRows) {

        this.pageId = pageId;

        this.maxRows = maxRows;

        this.rows = new ArrayList<>();
    }

    public int getPageId() {

        return pageId;
    }

    public List<Row> getRows() {

        return rows;
    }

    /*
     * Check if page is full.
     */
    public boolean isFull() {

        return rows.size() >= maxRows;
    }

    /*
     * Insert row.
     */
    public void addRow(
            Row row) {

        rows.add(
                row);
    }

    public int getRowCount() {

        return rows.size();
    }

    public int getMaxRows() {

        return maxRows;
    }
}