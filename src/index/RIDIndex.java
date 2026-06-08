package index;

import page.RID;

import java.util.HashMap;
import java.util.Map;

/*
 * PostgreSQL-style index.
 *
 * key -> RID
 */
public class RIDIndex {

    private Map<String, RID> entries;

    public RIDIndex() {

        entries =
                new HashMap<>();
    }

    /*
     * Add entry.
     */
    public void add(
            String key,
            RID rid) {

        entries.put(
                key,
                rid);
    }

    /*
     * Lookup RID.
     */
    public RID find(
            String key) {

        return entries.get(
                key);
    }
}