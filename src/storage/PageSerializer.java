package storage;

import page.Page;
import row.Row;

import java.util.Arrays;

public class PageSerializer {

    public static String serialize(Page page) {

        StringBuilder sb = new StringBuilder();

        sb.append(page.getPageId())
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

        String[] lines = data.split("\n");

        int pageId =
                Integer.parseInt(lines[0]);

        Page page =
                new Page(pageId, 100);

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