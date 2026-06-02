package index;

import row.Row;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class Index {

    /*
     * Example:
     *
     * "Niranjan"
     *      ↓
     * [Row1, Row2]
     *
     * "Rahul"
     *      ↓
     * [Row3]
     */
    private Map<String, List<Row>> entries;

    public Index() {

        entries =
                new HashMap<>();
    }

    /*
     * Add row to index.
     */
    public void add(
            String key,
            Row row) {

        entries.putIfAbsent(
                key,
                new ArrayList<>());

        entries.get(key)
                .add(row);
    }

    /*
     * Find rows by key.
     */
    public List<Row> find(
            String key) {

        return entries.getOrDefault(
                key,
                new ArrayList<>());
    }
}