package app;

import database.Database;
import index.RIDIndex;
import index.RIDTreeIndex;
import page.PageManager;
import page.RID;
import query.QueryExecutor;
import row.Row;
import schema.Column;
import schema.Schema;
import table.Table;
import storage.StorageEngine;

import java.util.ArrayList;
import java.util.List;

import btree.LeafNode;
import btree.BTree;
import btree.InternalNode;

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
                db.insert(
                                "users",
                                new Row(
                                                List.of(
                                                                "4",
                                                                "Amit",
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
                executor.execute(
                                "SELECT DISTINCT age FROM users");
                executor.execute(
                                "SELECT * FROM users OFFSET 1 LIMIT 2");
                System.out.println(
                                "\nRID Test:");

                PageManager pm = new PageManager(
                                2);

                RID rid1 = pm.insertAndReturnRID(
                                new Row(
                                                List.of(
                                                                "1",
                                                                "A",
                                                                "10")));

                RID rid2 = pm.insertAndReturnRID(
                                new Row(
                                                List.of(
                                                                "2",
                                                                "B",
                                                                "20")));

                RID rid3 = pm.insertAndReturnRID(
                                new Row(
                                                List.of(
                                                                "3",
                                                                "C",
                                                                "30")));

                System.out.println(
                                rid1);

                System.out.println(
                                rid2);

                System.out.println(
                                rid3);

                System.out.println(
                                "\nPages:");

                for (page.Page page : pm.getPages()) {

                        System.out.println(
                                        "Page "
                                                        + page.getPageId()
                                                        + " -> "
                                                        + page.getRows()
                                                                        .size()
                                                        + " rows");
                }

                System.out.println(
                                "\nRID Index Test:");

                RIDIndex ridIndex = new RIDIndex();

                ridIndex.add(
                                "1",
                                rid1);

                ridIndex.add(
                                "2",
                                rid2);

                ridIndex.add(
                                "3",
                                rid3);

                System.out.println(
                                ridIndex.find(
                                                "1"));

                System.out.println(
                                ridIndex.find(
                                                "2"));

                System.out.println(
                                ridIndex.find(
                                                "3"));
                System.out.println(
                                "\nRID Lookup Test:");

                Row foundRow = pm.getRow(
                                rid2);

                System.out.println(
                                foundRow.getValues());
                System.out.println(
                                "\nRID Tree Index Test:");

                RIDTreeIndex treeIndex = new RIDTreeIndex();

                treeIndex.add(
                                10,
                                rid1);

                treeIndex.add(
                                20,
                                rid2);

                treeIndex.add(
                                30,
                                rid3);

                System.out.println(
                                "Age > 15");

                for (RID rid : treeIndex.findGreaterThan(
                                15)) {

                        System.out.println(
                                        pm.getRow(
                                                        rid)
                                                        .getValues());
                }

                System.out.println(
                                "\nB-Tree Node Test:");

                LeafNode leaf = new LeafNode();

                leaf.getKeys()
                                .add(10);

                leaf.getKeys()
                                .add(20);

                System.out.println(
                                leaf.getKeys());

                InternalNode root = new InternalNode();

                root.getKeys()
                                .add(15);

                root.getChildren()
                                .add(leaf);

                System.out.println(
                                root.getKeys());

                System.out.println(
                                root.isLeaf());

                System.out.println(
                                leaf.isLeaf());
                System.out.println(
                                "\nB-Tree Split Test:");

                BTree tree = new BTree();

                tree.insert(
                                10,
                                rid1);

                tree.insert(
                                20,
                                rid2);

                tree.insert(
                                30,
                                rid3);

                tree.insert(
                                40,
                                rid1);
        }
}