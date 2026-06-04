package query;

import database.Database;
import row.Row;

import java.util.List;
import java.util.Arrays;

public class QueryExecutor {

    private Database database;

    public QueryExecutor(
            Database database) {

        this.database = database;
    }

    /*
     * Print query result.
     */
    private void printRows(
            List<Row> rows) {

        System.out.println(
                "\nQuery Result:");

        for (Row row : rows) {

            System.out.println(
                    row.getValues());
        }
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
                    new Row(
                            Arrays.asList(
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
         * SELECT MAX(age) FROM users
         */
        if (sql.matches(
                "SELECT MAX\\(\\w+\\) FROM \\w+")) {

            String columnName = sql.substring(
                    sql.indexOf("(") + 1,
                    sql.indexOf(")"));

            String[] parts = sql.split(" ");

            String tableName = parts[3];

            int result = database.max(
                    tableName,
                    columnName);

            System.out.println(
                    "\nQuery Result:");

            System.out.println(
                    result);

            return;
        }

        /*
         * SELECT MIN(age) FROM users
         */
        if (sql.matches(
                "SELECT MIN\\(\\w+\\) FROM \\w+")) {

            String columnName = sql.substring(
                    sql.indexOf("(") + 1,
                    sql.indexOf(")"));

            String[] parts = sql.split(" ");

            String tableName = parts[3];

            int result = database.min(
                    tableName,
                    columnName);

            System.out.println(
                    "\nQuery Result:");

            System.out.println(
                    result);

            return;
        }

        /*
         * SELECT SUM(age) FROM users
         */
        if (sql.matches(
                "SELECT SUM\\(\\w+\\) FROM \\w+")) {

            String columnName = sql.substring(
                    sql.indexOf("(") + 1,
                    sql.indexOf(")"));

            String[] parts = sql.split(" ");

            String tableName = parts[3];

            int result = database.sum(
                    tableName,
                    columnName);

            System.out.println(
                    "\nQuery Result:");

            System.out.println(
                    result);

            return;
        }

        /*
         * SELECT AVG(age) FROM users
         */
        if (sql.matches(
                "SELECT AVG\\(\\w+\\) FROM \\w+")) {

            String columnName = sql.substring(
                    sql.indexOf("(") + 1,
                    sql.indexOf(")"));

            String[] parts = sql.split(" ");

            String tableName = parts[3];

            int result = database.avg(
                    tableName,
                    columnName);

            System.out.println(
                    "\nQuery Result:");

            System.out.println(
                    result);

            return;
        }

        /*
         * SELECT COUNT(*) FROM users
         */
        if (sql.matches(
                "SELECT COUNT\\(\\*\\) FROM \\w+")) {

            String[] parts = sql.split(" ");

            String tableName = parts[3];

            int count = database.count(
                    tableName);

            System.out.println(
                    "\nQuery Result:");

            System.out.println(
                    count);

            return;
        }

        /*
         * SELECT name,age FROM users
         */
        if (sql.matches(
                "SELECT \\w+(,\\w+)+ FROM \\w+")) {

            String[] parts = sql.split(" ");

            String[] columns = parts[1]
                    .split(",");

            List<String> columnNames = Arrays.asList(
                    columns);

            String tableName = parts[3];

            List<List<String>> rows = database.selectColumns(
                    tableName,
                    columnNames);

            printMultiColumnValues(
                    rows);

            return;
        }

        /*
         * SELECT DISTINCT age FROM users
         */
        if (sql.matches(
                "SELECT DISTINCT \\w+ FROM \\w+")) {

            String[] parts = sql.split(" ");

            String columnName = parts[2];

            String tableName = parts[4];

            List<String> values = database.selectDistinct(
                    tableName,
                    columnName);

            printValues(
                    values);

            return;
        }

        /*
         * SELECT name FROM users
         */
        if (sql.matches(
                "SELECT \\w+ FROM \\w+")) {

            String[] parts = sql.split(" ");

            String columnName = parts[1];

            String tableName = parts[3];

            List<String> values = database.selectColumn(
                    tableName,
                    columnName);

            printValues(
                    values);

            return;
        }

        /*
         * SELECT * FROM users ORDER BY age DESC
         */
        if (sql.matches(
                "SELECT \\* FROM \\w+ ORDER BY \\w+ DESC")) {

            String[] parts = sql.split(" ");

            String tableName = parts[3];

            String columnName = parts[6];

            List<Row> rows = database.orderBy(
                    tableName,
                    columnName,
                    false);

            printRows(
                    rows);

            return;
        }

        /*
         * SELECT * FROM users OFFSET 1 LIMIT 2
         */
        if (sql.matches(
                "SELECT \\* FROM \\w+ OFFSET \\d+ LIMIT \\d+")) {

            String[] parts = sql.split(" ");

            String tableName = parts[3];

            int offset = Integer.parseInt(
                    parts[5]);

            int limit = Integer.parseInt(
                    parts[7]);

            List<Row> rows = database.selectAll(
                    tableName);

            rows = database.offsetRows(
                    rows,
                    offset);

            rows = database.limitRows(
                    rows,
                    limit);

            printRows(
                    rows);

            return;
        }

        /*
         * SELECT * FROM users
         * WHERE age > 18
         * ORDER BY age
         * LIMIT 2
         */
        if (sql.matches(
                "SELECT \\* FROM \\w+ WHERE \\w+ [><=]+ \\w+ ORDER BY \\w+ LIMIT \\d+")) {

            String[] parts = sql.split(" ");

            String tableName = parts[3];

            String whereColumn = parts[5];

            String operator = parts[6];

            String whereValue = parts[7];

            String orderColumn = parts[10];

            int limit = Integer.parseInt(
                    parts[12]);

            List<Row> rows;

            switch (operator) {

                case ">":

                    rows = database.selectGreaterThan(
                            tableName,
                            whereColumn,
                            whereValue);
                    break;

                case "<":

                    rows = database.selectLessThan(
                            tableName,
                            whereColumn,
                            whereValue);
                    break;

                case "=":

                    rows = database.selectWhere(
                            tableName,
                            whereColumn,
                            whereValue);
                    break;

                default:

                    System.out.println(
                            "Unsupported operator.");

                    return;
            }

            rows = database.orderRows(
                    rows,
                    tableName,
                    orderColumn,
                    true);

            rows = database.limitRows(
                    rows,
                    limit);

            printRows(
                    rows);

            return;
        }

        /*
         * SELECT * FROM users LIMIT 2
         */
        if (sql.matches(
                "SELECT \\* FROM \\w+ LIMIT \\d+")) {

            String[] parts = sql.split(" ");

            String tableName = parts[3];

            int limit = Integer.parseInt(
                    parts[5]);

            List<Row> rows = database.limit(
                    tableName,
                    limit);

            printRows(
                    rows);

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

            printRows(
                    rows);

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

            printRows(
                    rows);

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

            printRows(
                    rows);

            return;
        }

        System.out.println(
                "Unsupported query.");
    }

    private void printValues(
            List<String> values) {

        System.out.println(
                "\nQuery Result:");

        for (String value : values) {

            System.out.println(
                    value);
        }
    }

    private void printMultiColumnValues(
            List<List<String>> rows) {

        System.out.println(
                "\nQuery Result:");

        for (List<String> row : rows) {

            System.out.println(
                    row);
        }
    }

}
