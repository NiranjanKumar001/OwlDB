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
         * Create table in database
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
         * Test invalid table
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
        Table loadedTable =
                db.getTable(
                        "users"
                );

        System.out.println(
                "\nRows After Insert:"
        );

        for (Row row :
                loadedTable.getRows()) {

            System.out.println(
                    row.getValues()
            );
        }

        /*
         * Test selectAll()
         */
        System.out.println(
                "\nSelect All:"
        );

        List<Row> rows =
                db.selectAll(
                        "users"
                );

        for (Row row : rows) {

            System.out.println(
                    row.getValues()
            );
        }

        /*
         * Test selectWhere()
         */
        System.out.println(
                "\nUsers With Age 22:"
        );

        List<Row> ageResult =
                db.selectWhere(
                        "users",
                        "age",
                        "22"
                );

        for (Row row :
                ageResult) {

            System.out.println(
                    row.getValues()
            );
        }

        /*
         * Test deleteWhere()
         */
        System.out.println(
                "\nBefore Delete:"
        );

        for (Row row :
                db.selectAll(
                        "users"
                )) {

            System.out.println(
                    row.getValues()
            );
        }

        db.deleteWhere(
                "users",
                "age",
                "17"
        );

        System.out.println(
                "\nAfter Delete:"
        );

        for (Row row :
                db.selectAll(
                        "users"
                )) {

            System.out.println(
                    row.getValues()
            );
        }
    }
}