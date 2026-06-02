package app;

import database.Database;
import row.Row;

public class LoadTest {

    public static void main(String[] args) {

        Database db =
                new Database();

        db.loadTable(
                "users");

        System.out.println(
                "\nLoaded Rows:"
        );

        for (Row row :
                db.selectAll(
                        "users")) {

            System.out.println(
                    row.getValues()
            );
        }
    }
}