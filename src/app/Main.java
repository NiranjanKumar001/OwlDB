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
         * Show all rows
         */
        System.out.println(
                "\nAll Rows:");

        for (Row row :
                db.selectAll(
                        "users")) {

            System.out.println(
                    row.getValues());
        }

        /*
         * Test id index
         */
        System.out.println(
                "\nSelect By ID:");

        List<Row> idResult =
                db.selectWhere(
                        "users",
                        "id",
                        "3");

        for (Row row :
                idResult) {

            System.out.println(
                    row.getValues());
        }

        /*
         * Test name index
         */
        System.out.println(
                "\nSelect By Name:");

        List<Row> nameResult =
                db.selectWhere(
                        "users",
                        "name",
                        "Niranjan");

        for (Row row :
                nameResult) {

            System.out.println(
                    row.getValues());
        }
    }
}