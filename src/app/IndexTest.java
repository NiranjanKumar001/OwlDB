package app;

import index.Index;
import row.Row;

import java.util.List;

public class IndexTest {

    public static void main(String[] args) {

        Index index =
                new Index();

        Row user1 =
                new Row(
                        List.of(
                                "1",
                                "Niranjan",
                                "23"
                        )
                );

        index.add(
                "1",
                user1);

        Row found =
                index.find(
                        "1");

        System.out.println(
                found.getValues());
    }
}