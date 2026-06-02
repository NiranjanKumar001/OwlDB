package app;

import database.Database;
import row.Row;

public class LoadAllTest {

    public static void main(String[] args) {

        Database db =
                new Database();

        db.loadAllTables();

        System.out.println(
                "\nUsers Table:"
        );

        for (Row row :
                db.selectAll(
                        "users")) {

            System.out.println(
                    row.getValues());
        }
    }
}