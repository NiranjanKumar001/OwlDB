package table;

import schema.Schema;
import row.Row;

import java.util.ArrayList;
import java.util.List;

/*
 * Represents a complete table.
 *
 * Example:
 *
 * Users Table
 *
 * Schema:
 * id INT
 * name STRING
 * age INT
 *
 * Rows:
 * [1, Niranjan, 22]
 * [2, Rahul, 17]
 */
public class Table {

    /*
     * Blueprint of the table.
     *
     * Example:
     * id INT
     * name STRING
     * age INT
     */
    private Schema schema;

    /*
     * All rows stored in this table.
     *
     * Example:
     * [1, Niranjan, 22]
     * [2, Rahul, 17]
     */
    private List<Row> rows;

    /*
     * Constructor
     *
     * When a new table is created,
     * it receives a schema.
     *
     * Initially there are no rows.
     */
    public Table(Schema schema) {

        this.schema = schema;

        // Start with empty rows
        this.rows = new ArrayList<>();
    }

    /*
     * Returns table schema.
     */
    public Schema getSchema() {
        return schema;
    }

    /*
     * Returns all rows.
     */
    public List<Row> getRows() {
        return rows;
    }

    /*
     * Adds a row to the table.
     */
    public void addRow(Row row) {
        rows.add(row);
    }
}