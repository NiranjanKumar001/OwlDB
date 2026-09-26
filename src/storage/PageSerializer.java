package storage;

import page.Page;
import row.Row;

import java.util.Arrays;

public class PageSerializer {

    public static String serialize(Page page) {

        StringBuilder sb = new StringBuilder();

        sb.append(page.getPageId())
                .append("|")
                .append(page.getMaxRows())
                .append("\n");

        for (Row row : page.getRows()) {

            sb.append(
                    String.join(
                            ",",
                            row.getValues()))
                    .append("\n");
        }

        return sb.toString();
    }

    public static Page deserialize(String data) {

        if (data == null || data.isBlank()) {
            return null;
        }

        String[] lines = data.split("\n");

        String[] header = lines[0].split("\\|");

        int pageId = Integer.parseInt(header[0]);

        int maxRows = 100;

        if (header.length > 1) {
            maxRows = Integer.parseInt(header[1]);
        }

        Page page = new Page(pageId, maxRows);

        for (int i = 1; i < lines.length; i++) {

            if (lines[i].isBlank()) {
                continue;
            }

            String[] values =
                    lines[i].split(",");

            Row row =
                    new Row(
                            Arrays.asList(values)
                    );

            page.addRow(row);
        }

        return page;
    }
}