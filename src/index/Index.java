package index;

import row.Row;

import java.util.HashMap;
import java.util.Map;

public class Index {

    private Map<String, Row> entries;

    public Index() {

        entries =
                new HashMap<>();
    }

    public void add(
            String key,
            Row row) {

        entries.put(
                key,
                row);
    }

    public Row find(
            String key) {

        return entries.get(
                key);
    }
}