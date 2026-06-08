package index;

import page.RID;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class RIDTreeIndex {

    /*
     * key -> RID list
     */
    private TreeMap<Integer, List<RID>> entries;

    public RIDTreeIndex() {

        entries =
                new TreeMap<>();
    }

    /*
     * Add entry.
     */
    public void add(
            int key,
            RID rid) {

        entries.computeIfAbsent(
                key,
                k -> new ArrayList<>());

        entries.get(
                key)
                .add(rid);
    }

    /*
     * Exact lookup.
     */
    public List<RID> find(
            int key) {

        return entries.getOrDefault(
                key,
                new ArrayList<>());
    }

    /*
     * Greater than.
     */
    public List<RID> findGreaterThan(
            int key) {

        List<RID> result =
                new ArrayList<>();

        entries.tailMap(
                key + 1)
                .values()
                .forEach(
                        result::addAll);

        return result;
    }

    /*
     * Less than.
     */
    public List<RID> findLessThan(
            int key) {

        List<RID> result =
                new ArrayList<>();

        entries.headMap(
                key)
                .values()
                .forEach(
                        result::addAll);

        return result;
    }
}