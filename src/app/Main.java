package app;

import schema.Column;
import schema.Schema;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        List<Column> columns =
                new ArrayList<>();

        columns.add(
                new Column("id", "INT"));

        columns.add(
                new Column("name", "STRING"));

        columns.add(
                new Column("age", "INT"));

        Schema usersSchema =
                new Schema(
                        "users",
                        columns);

        System.out.println(
                usersSchema.getTableName());

    }
}