package app;

import database.Database;
import row.Row;
import schema.Column;
import schema.Schema;
import table.Table;

import java.util.ArrayList;
import java.util.List;

/*
 * Shared test data for OwlDB.
 */
public class TestData {

    public static Database createDatabase() {

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

        Schema schema =
                new Schema(
                        "users",
                        columns);

        Table table =
                new Table(
                        schema);

        Database db =
                new Database();

        db.createTable(
                table);

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

        return db;
    }
}