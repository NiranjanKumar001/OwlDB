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

        // Create columns
        List<Column> columns =
                new ArrayList<>();

        columns.add(
                new Column("id", "INT"));

        columns.add(
                new Column("name", "STRING"));

        columns.add(
                new Column("age", "INT"));

        // Create schema
        Schema usersSchema =
                new Schema(
                        "users",
                        columns);

        // Create table
        Table usersTable =
                new Table(
                        usersSchema);

        // Create database
        Database db =
                new Database();

        // Test createTable()
        db.createTable(
                usersTable);

        // Test insert()
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
        
        db.insert(
        "unknown",
        new Row(
                List.of("1")
        )
);

        // Test getTable()
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
        
    }
}