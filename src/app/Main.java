package app;

import schema.Column;
import schema.Schema;
import row.Row;
import table.Table;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        /*
         * STEP 1
         * Create columns for Users table
         */

        List<Column> columns = new ArrayList<>();

        columns.add(
                new Column("id", "INT")
        );

        columns.add(
                new Column("name", "STRING")
        );

        columns.add(
                new Column("age", "INT")
        );

        /*
         * STEP 2
         * Create schema
         *
         * users
         * ├── id INT
         * ├── name STRING
         * └── age INT
         */

        Schema usersSchema =
                new Schema(
                        "users",
                        columns
                );

        /*
         * STEP 3
         * Create table using schema
         */

        Table usersTable =
                new Table(usersSchema);

        /*
         * STEP 4
         * Add first row
         *
         * 1 | Niranjan | 22
         */

        usersTable.addRow(
                new Row(
                        List.of(
                                "1",
                                "Niranjan",
                                "22"
                        )
                )
        );

        /*
         * STEP 5
         * Add second row
         *
         * 2 | Rahul | 17
         */

        usersTable.addRow(
                new Row(
                        List.of(
                                "2",
                                "Rahul",
                                "17"
                        )
                )
        );

        /*
         * STEP 6
         * Print table name
         */

        System.out.println(
                "Table Name: "
                        + usersTable
                        .getSchema()
                        .getTableName()
        );

        /*
         * STEP 7
         * Print schema
         */

        System.out.println("\nSchema:");

        for (Column column :
                usersTable
                        .getSchema()
                        .getColumns()) {

            System.out.println(
                    column.getName()
                            + " "
                            + column.getType()
            );
        }

        /*
         * STEP 8
         * Print rows
         */

        System.out.println("\nRows:");

        for (Row row :
                usersTable.getRows()) {

            System.out.println(
                    row.getValues()
            );
        }
    }
}