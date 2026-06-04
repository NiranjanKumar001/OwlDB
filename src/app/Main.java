package app;

import database.Database;
import query.QueryExecutor;
import row.Row;
import schema.Column;
import schema.Schema;
import table.Table;
import storage.StorageEngine;

import java.util.ArrayList;
import java.util.List;

public class Main {

        public static void main(String[] args) {

                /*
                 * Create columns
                 */
                List<Column> columns = new ArrayList<>();

                columns.add(
                                new Column(
                                                "id",
                                                "INT"));

                columns.add(
                                new Column(
                                                "name",
                                                "STRING"));

                columns.add(
                                new Column(
                                                "age",
                                                "INT"));

                /*
                 * Create schema
                 */
                Schema usersSchema = new Schema(
                                "users",
                                columns);

                /*
                 * Create table
                 */
                Table usersTable = new Table(
                                usersSchema);

                /*
                 * Create database
                 */
                Database db = new Database();

                /*
                 * Create table
                 */
                db.createTable(
                                usersTable);

                /*
                 * Insert rows
                 */
                db.insert(
                                "users",
                                new Row(
                                                List.of(
                                                                "1",
                                                                "Niranjan",
                                                                "23")));

                db.insert(
                                "users",
                                new Row(
                                                List.of(
                                                                "2",
                                                                "Rahul",
                                                                "17")));

                db.insert(
                                "users",
                                new Row(
                                                List.of(
                                                                "3",
                                                                "Priya",
                                                                "20")));

                /*
                 * Create indexes
                 */
                usersTable.createIndex(
                                "id");

                usersTable.createIndex(
                                "name");

                /*
                 * Save indexes to disk
                 */
                StorageEngine storageEngine = new StorageEngine();

                storageEngine.saveIndexes(
                                usersTable);

                /*
                 * Show all rows
                 */
                System.out.println(
                                "\nAll Rows:");

                for (Row row : db.selectAll(
                                "users")) {

                        System.out.println(
                                        row.getValues());
                }

                /*
                 * Test ID index
                 */
                System.out.println(
                                "\nSelect By ID:");

                List<Row> idResult = db.selectWhere(
                                "users",
                                "id",
                                "3");

                for (Row row : idResult) {

                        System.out.println(
                                        row.getValues());
                }

                /*
                 * Test Name index
                 */
                System.out.println(
                                "\nSelect By Name:");

                List<Row> nameResult = db.selectWhere(
                                "users",
                                "name",
                                "Niranjan");

                for (Row row : nameResult) {

                        System.out.println(
                                        row.getValues());
                }

                /*
                 * Verify indexes
                 */
                System.out.println(
                                "\nIndexes:");

                System.out.println(
                                usersTable.getIndexes()
                                                .keySet());

                System.out.println(
                                "\nUsers With Age > 18:");

                List<Row> greaterThanRows = db.selectGreaterThan(
                                "users",
                                "age",
                                "18");

                for (Row row : greaterThanRows) {

                        System.out.println(
                                        row.getValues());
                }

                System.out.println(
                                "\nUsers With Age < 21:");

                List<Row> lessThanRows = db.selectLessThan(
                                "users",
                                "age",
                                "21");

                for (Row row : lessThanRows) {

                        System.out.println(
                                        row.getValues());
                }

                System.out.println(
                                "\nUsers With Age >= 20:");

                List<Row> greaterEqualRows = db.selectGreaterThanOrEqual(
                                "users",
                                "age",
                                "20");

                for (Row row : greaterEqualRows) {

                        System.out.println(
                                        row.getValues());
                }

                System.out.println(
                                "\nUsers With Age <= 20:");

                List<Row> lessEqualRows = db.selectLessThanOrEqual(
                                "users",
                                "age",
                                "20");

                for (Row row : lessEqualRows) {

                        System.out.println(
                                        row.getValues());
                }
                System.out.println(
                                "\nOrder By Age:");

                List<Row> sortedRows = db.orderBy(
                                "users",
                                "age");

                for (Row row : sortedRows) {

                        System.out.println(
                                        row.getValues());
                }
                QueryExecutor executor = new QueryExecutor(
                                db);

                executor.execute(
                                "SELECT MIN(age) FROM users");

                executor.execute(
                                "SELECT SUM(age) FROM users");

                executor.execute(
                                "SELECT AVG(age) FROM users");
                executor.execute(
                                "SELECT id,name FROM users");

                executor.execute(
                                "SELECT name,age FROM users");
                executor.execute(
                                "SELECT * FROM users LIMIT 2");
                executor.execute(
                                "SELECT * FROM users WHERE age > 18 ORDER BY age LIMIT 2");
                executor.execute(
        "SELECT * FROM users ORDER BY age DESC");
                executor.execute(
        "SELECT * FROM users WHERE age > 18 ORDER BY age LIMIT 2");
        }
}