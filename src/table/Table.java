package table;

import schema.Schema;
import row.Row;
import index.Index;

import java.util.ArrayList;
import java.util.List;

public class Table {

    /*
     * Blueprint of table.
     */
    private Schema schema;

    /*
     * Table rows.
     */
    private List<Row> rows;

    /*
     * Primary index.
     *
     * For now:
     * id -> Row
     */
    private Index index;

    /*
     * Constructor
     */
    public Table(
            Schema schema) {

        this.schema = schema;

        this.rows =
                new ArrayList<>();

        this.index =
                new Index();
    }

    /*
     * Get schema.
     */
    public Schema getSchema() {

        return schema;
    }

    /*
     * Get rows.
     */
    public List<Row> getRows() {

        return rows;
    }

    /*
     * Get index.
     */
    public Index getIndex() {

        return index;
    }

    /*
     * Add row.
     */
    public void addRow(
            Row row) {

        rows.add(row);

        /*
         * First column = id
         */
        String key =
                row.getValues()
                        .get(0);

        index.add(
                key,
                row);
    }
}