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
         * Add rows
         */
        usersTable.addRow(
                new Row(
                        List.of(
                                "1",
                                "Niranjan",
                                "22")));

        usersTable.addRow(
                new Row(
                        List.of(
                                "2",
                                "Rahul",
                                "17")));

        /*
         * Create database
         */
        Database db =
                new Database();

        /*
         * Add table to database
         */
        db.createTable(
                usersTable);

        System.out.println(
                "\nDatabase test completed.");
    }
}