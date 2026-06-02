package database;

import table.Table;
import row.Row;
import schema.Column;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class Database {

    /*
     * Stores all tables.
     *
     * Example:
     *
     * users    -> Users Table
     * products -> Products Table
     */
    private Map<String, Table> tables;

    /*
     * Constructor
     */
    public Database() {

        tables =
                new HashMap<>();
    }

    /*
     * Create a table inside database.
     */
    public void createTable(
            Table table) {

        String tableName =
                table.getSchema()
                        .getTableName();

        tables.put(
                tableName,
                table);

        System.out.println(
                "Table created: "
                        + tableName);
    }

    /*
     * Get a table by name.
     */
    public Table getTable(
            String tableName) {

        return tables.get(
                tableName);
    }

    /*
     * Insert a row into a table.
     */
    public void insert(
            String tableName,
            Row row) {

        Table table =
                tables.get(
                        tableName);

        if (table == null) {

            System.out.println(
                    "Table not found: "
                            + tableName);

            return;
        }

        table.addRow(
                row);

        System.out.println(
                "Row inserted into "
                        + tableName);
    }

    /*
     * SELECT * FROM table
     */
    public List<Row> selectAll(
            String tableName) {

        Table table =
                tables.get(
                        tableName);

        if (table == null) {

            System.out.println(
                    "Table not found: "
                            + tableName);

            return new ArrayList<>();
        }

        return table.getRows();
    }

    /*
     * SELECT *
     * FROM table
     * WHERE column = value
     */
    public List<Row> selectWhere(
            String tableName,
            String columnName,
            String value) {

        Table table =
                tables.get(
                        tableName);

        if (table == null) {

            System.out.println(
                    "Table not found: "
                            + tableName);

            return new ArrayList<>();
        }

        /*
         * Find column index.
         */
        int columnIndex = -1;

        List<Column> columns =
                table.getSchema()
                        .getColumns();

        for (int i = 0;
             i < columns.size();
             i++) {

            if (columns.get(i)
                    .getName()
                    .equals(columnName)) {

                columnIndex = i;
                break;
            }
        }

        /*
         * Column not found.
         */
        if (columnIndex == -1) {

            System.out.println(
                    "Column not found: "
                            + columnName);

            return new ArrayList<>();
        }

        /*
         * Store matching rows.
         */
        List<Row> result =
                new ArrayList<>();

        /*
         * Check every row.
         */
        for (Row row :
                table.getRows()) {

            String rowValue =
                    row.getValues()
                            .get(columnIndex);

            if (rowValue.equals(
                    value)) {

                result.add(
                        row);
            }
        }

        return result;
    }
}