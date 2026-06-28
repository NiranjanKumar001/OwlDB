package app;

import page.Page;
import page.PageManager;
import row.Row;
import storage.PageSerializer;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        BTreeTest.run();

        System.out.println("\nOWLET-055 Serialization Test:");

        PageManager pm = new PageManager(2);

        pm.insertRow(
                new Row(
                        List.of("1", "A", "10")));

        pm.insertRow(
                new Row(
                        List.of("2", "B", "20")));

        Page page =
                pm.getPages().get(0);

        String data =
                PageSerializer.serialize(page);

        System.out.println(
                "\nSerialized Page:");

        System.out.println(data);

        Page loaded =
                PageSerializer.deserialize(data);

        System.out.println(
                "Deserialized Rows:");

        for (Row row : loaded.getRows()) {

            System.out.println(
                    row.getValues());
        }
    }
}