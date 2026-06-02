package database;

import table.Table;
import row.Row;

import java.util.HashMap;
import java.util.Map;

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
}