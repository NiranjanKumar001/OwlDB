package query;

import database.Database;
import row.Row;

import java.util.List;

public class QueryExecutor {

    private Database database;

    public QueryExecutor(
            Database database) {

        this.database = database;
    }

    /*
     * Execute SQL query.
     */
    public void execute(
            String sql) {

        sql = sql.trim();

        /*
         * INSERT INTO users VALUES (...)
         */
        if (sql.startsWith(
                "INSERT INTO")) {

            String[] parts = sql.split(" ", 4);

            String tableName = parts[2];

            String valuesPart = sql.substring(
                    sql.indexOf("(") + 1,
                    sql.lastIndexOf(")"));

            String[] values = valuesPart.split(",");

            database.insert(
                    tableName,
                    new row.Row(
                            java.util.Arrays.asList(
                                    values)));

            System.out.println(
                    "Insert successful.");

            return;
        }

        /*
         * DELETE FROM users WHERE id = 2
         */
        if (sql.startsWith(
                "DELETE FROM")) {

            String[] parts = sql.split(" ");

            String tableName = parts[2];

            String columnName = parts[4];

            String value = parts[6];

            database.deleteWhere(
                    tableName,
                    columnName,
                    value);

            System.out.println(
                    "Delete successful.");

            return;
        }

        /*
         * UPDATE users
         * SET age = 24
         * WHERE id = 1
         */
        if (sql.startsWith(
                "UPDATE")) {

            String[] parts = sql.split(" ");

            String tableName = parts[1];

            String updateColumn = parts[3];

            String newValue = parts[5];

            String whereColumn = parts[7];

            String whereValue = parts[9];

            database.updateWhere(
                    tableName,
                    whereColumn,
                    whereValue,
                    updateColumn,
                    newValue);

            System.out.println(
                    "Update successful.");

            return;
        }

        /*
         * SELECT * FROM users
         */
        if (sql.matches(
                "SELECT \\* FROM \\w+")) {

            String[] parts = sql.split(" ");

            String tableName = parts[3];

            List<Row> rows = database.selectAll(
                    tableName);

            System.out.println(
                    "\nQuery Result:");

            for (Row row : rows) {

                System.out.println(
                        row.getValues());
            }

            return;
        }

        /*
         * SELECT * FROM users ORDER BY age
         */
        if (sql.contains(
                "ORDER BY")) {

            String[] parts = sql.split(" ");

            String tableName = parts[3];

            String columnName = parts[6];

            List<Row> rows = database.orderBy(
                    tableName,
                    columnName);

            System.out.println(
                    "\nQuery Result:");

            for (Row row : rows) {

                System.out.println(
                        row.getValues());
            }

            return;
        }

        /*
         * SELECT with WHERE
         */
        if (sql.contains(
                "WHERE")) {

            String[] parts = sql.split(" ");

            String tableName = parts[3];

            String columnName = parts[5];

            String operator = parts[6];

            String value = parts[7];

            List<Row> rows;

            switch (operator) {

                case "=":

                    rows = database.selectWhere(
                            tableName,
                            columnName,
                            value);
                    break;

                case ">":

                    rows = database.selectGreaterThan(
                            tableName,
                            columnName,
                            value);
                    break;

                case "<":

                    rows = database.selectLessThan(
                            tableName,
                            columnName,
                            value);
                    break;

                case ">=":

                    rows = database.selectGreaterThanOrEqual(
                            tableName,
                            columnName,
                            value);
                    break;

                case "<=":

                    rows = database.selectLessThanOrEqual(
                            tableName,
                            columnName,
                            value);
                    break;

                default:

                    System.out.println(
                            "Unsupported operator.");

                    return;
            }

            System.out.println(
                    "\nQuery Result:");

            for (Row row : rows) {

                System.out.println(
                        row.getValues());
            }

            return;
        }

        System.out.println(
                "Unsupported query.");
    }
}