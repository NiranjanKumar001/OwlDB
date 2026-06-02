package app;

import database.Database;
import row.Row;
import schema.Column;
import schema.Schema;
import table.Table;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        /*
         * Create columns
         */
        List<Column> columns =
                new ArrayList<>();

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
        Schema usersSchema =
                new Schema(
                        "users",
                        columns);

        /*
         * Create table
         */
        Table usersTable =
                new Table(
                        usersSchema);

        /*
         * Create database
         */
        Database db =
                new Database();

        /*
         * Test createTable()
         */
        db.createTable(
                usersTable);

        /*
         * Test insert()
         */
        db.insert(
                "users",
                new Row(
                        List.of(
                                "1",
                                "Niranjan",
                                "22"
                        )
                )
        );

        db.insert(
                "users",
                new Row(
                        List.of(
                                "2",
                                "Rahul",
                                "17"
                        )
                )
        );

        db.insert(
                "users",
                new Row(
                        List.of(
                                "3",
                                "Priya",
                                "20"
                        )
                )
        );

        /*
         * Invalid table test
         */
        db.insert(
                "unknown",
                new Row(
                        List.of("1")
                )
        );

        /*
         * Test getTable()
         */
        System.out.println(
                "\n===== GET TABLE =====");

        Table loadedTable =
                db.getTable(
                        "users");

        for (Row row :
                loadedTable.getRows()) {

            System.out.println(
                    row.getValues());
        }

        /*
         * Test selectAll()
         */
        System.out.println(
                "\n===== SELECT ALL =====");

        List<Row> rows =
                db.selectAll(
                        "users");

        for (Row row :
                rows) {

            System.out.println(
                    row.getValues());
        }

        /*
         * Test selectWhere()
         */
        System.out.println(
                "\n===== SELECT WHERE age = 22 =====");

        List<Row> result =
                db.selectWhere(
                        "users",
                        "age",
                        "22"
                );

        for (Row row :
                result) {

            System.out.println(
                    row.getValues());
        }

        /*
         * Test updateWhere()
         */
        System.out.println(
                "\n===== BEFORE UPDATE =====");

        for (Row row :
                db.selectAll(
                        "users")) {

            System.out.println(
                    row.getValues());
        }

        db.updateWhere(
                "users",
                "name",
                "Niranjan",
                "age",
                "23"
        );

        System.out.println(
                "\n===== AFTER UPDATE =====");

        for (Row row :
                db.selectAll(
                        "users")) {

            System.out.println(
                    row.getValues());
        }

        /*
         * Test deleteWhere()
         */
        System.out.println(
                "\n===== BEFORE DELETE =====");

        for (Row row :
                db.selectAll(
                        "users")) {

            System.out.println(
                    row.getValues());
        }

        db.deleteWhere(
                "users",
                "age",
                "17"
        );

        System.out.println(
                "\n===== AFTER DELETE =====");

        for (Row row :
                db.selectAll(
                        "users")) {

            System.out.println(
                    row.getValues());
        }
    }
}