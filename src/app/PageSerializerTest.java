package app;

import page.Page;
import row.Row;
import storage.PageSerializer;

import java.util.List;

public class PageSerializerTest {

    public static void main(String[] args) {

        run();
    }

    public static void run() {

        System.out.println("Running PageSerializer regression tests...");

        /*
         * 1. Test page with custom maxRows (not 100) to verify metadata preservation.
         */
        int testPageId = 7;
        int testMaxRows = 3;

        Page page = new Page(testPageId, testMaxRows);

        Row row1 = new Row(List.of("1", "Alice", "100"));
        Row row2 = new Row(List.of("2", "Bob", "200"));

        page.addRow(row1);
        page.addRow(row2);

        String serialized = PageSerializer.serialize(page);

        // Verify serialized format starts with pageId|maxRows
        String[] lines = serialized.split("\n");

        if (!lines[0].equals("7|3")) {

            throw new IllegalStateException(
                    "Expected header '7|3', but got: " + lines[0]);
        }

        Page deserialized = PageSerializer.deserialize(serialized);

        if (deserialized == null) {

            throw new IllegalStateException(
                    "Deserialized page must not be null.");
        }

        if (deserialized.getPageId() != testPageId) {

            throw new IllegalStateException(
                    "Expected pageId " + testPageId + ", got: " + deserialized.getPageId());
        }

        if (deserialized.getMaxRows() != testMaxRows) {

            throw new IllegalStateException(
                    "Expected maxRows " + testMaxRows + ", got: " + deserialized.getMaxRows());
        }

        if (deserialized.getRowCount() != 2) {

            throw new IllegalStateException(
                    "Expected row count 2, got: " + deserialized.getRowCount());
        }

        if (!deserialized.getRows().get(0).getValues().equals(row1.getValues())) {

            throw new IllegalStateException(
                    "Row 1 values mismatch. Expected: "
                            + row1.getValues()
                            + ", got: "
                            + deserialized.getRows().get(0).getValues());
        }

        if (!deserialized.getRows().get(1).getValues().equals(row2.getValues())) {

            throw new IllegalStateException(
                    "Row 2 values mismatch. Expected: "
                            + row2.getValues()
                            + ", got: "
                            + deserialized.getRows().get(1).getValues());
        }

        // Verify behavior: page was not full, adding 1 more row fills it
        if (deserialized.isFull()) {

            throw new IllegalStateException(
                    "Deserialized page should not be full with 2 out of 3 rows.");
        }

        deserialized.addRow(new Row(List.of("3", "Charlie", "300")));

        if (!deserialized.isFull()) {

            throw new IllegalStateException(
                    "Deserialized page should be full with 3 out of 3 rows.");
        }

        /*
         * 2. Test multi-row page serialization round-trip with full capacity.
         */
        Page multiRowPage = new Page(42, 4);

        multiRowPage.addRow(new Row(List.of("10", "X")));
        multiRowPage.addRow(new Row(List.of("20", "Y")));
        multiRowPage.addRow(new Row(List.of("30", "Z")));
        multiRowPage.addRow(new Row(List.of("40", "W")));

        String multiSerialized = PageSerializer.serialize(multiRowPage);
        Page multiDeserialized = PageSerializer.deserialize(multiSerialized);

        if (multiDeserialized == null || multiDeserialized.getPageId() != 42 || multiDeserialized.getMaxRows() != 4) {

            throw new IllegalStateException(
                    "Multi-row page metadata preservation failed.");
        }

        if (multiDeserialized.getRowCount() != 4 || !multiDeserialized.isFull()) {

            throw new IllegalStateException(
                    "Multi-row page row count or fullness check failed.");
        }

        for (int i = 0; i < 4; i++) {

            if (!multiDeserialized.getRows().get(i).getValues().equals(multiRowPage.getRows().get(i).getValues())) {

                throw new IllegalStateException(
                        "Mismatch at row index " + i);
            }
        }

        /*
         * 3. Test backward compatibility with legacy single-value header (pageId only).
         */
        String legacyData = "15\n1,LegacyA\n2,LegacyB\n";
        Page legacyDeserialized = PageSerializer.deserialize(legacyData);

        if (legacyDeserialized == null || legacyDeserialized.getPageId() != 15) {

            throw new IllegalStateException(
                    "Legacy header deserialization failed.");
        }

        if (legacyDeserialized.getRowCount() != 2) {

            throw new IllegalStateException(
                    "Legacy header row count failed.");
        }

        System.out.println("PageSerializer regression tests passed successfully.");
    }
}
