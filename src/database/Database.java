package database;

import table.Table;

import java.util.HashMap;
import java.util.Map;

public class Database {

    /*
     * Stores all tables.
     *
     * Example:
     *
     * users -> Users Table
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

    public void createTable(Table table) {

    String tableName =
            table.getSchema()
                 .getTableName();

    tables.put(
            tableName,
            table
    );

    System.out.println(
            "Table created: "
            + tableName
    );
}
}