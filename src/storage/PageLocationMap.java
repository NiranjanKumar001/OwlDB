package storage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/*
 * Maps logical page IDs to physical file offsets.
 *
 * For example:
 *   pageId 0 -> offset 0
 *   pageId 1 -> offset 128
 *
 * Can be persisted to a single metadata file using line-based formatting:
 *   pageId|offset
 */
public class PageLocationMap {

    private Map<Integer, Long> locations;

    public PageLocationMap() {

        locations = new HashMap<>();
    }

    /*
     * Store or update the offset for a page ID.
     */
    public void put(
            int pageId,
            long offset) {

        locations.put(
                pageId,
                offset);
    }

    /*
     * Get the offset for a page ID.
     * Returns null if page ID is not found.
     */
    public Long get(
            int pageId) {

        return locations.get(
                pageId);
    }

    /*
     * Check if a page ID exists in the map.
     */
    public boolean contains(
            int pageId) {

        return locations.containsKey(
                pageId);
    }

    /*
     * Number of mapped pages.
     */
    public int size() {

        return locations.size();
    }

    /*
     * Save mappings to a metadata file.
     */
    public void save(
            String filePath) throws IOException {

        save(new File(filePath));
    }

    public void save(
            File file) throws IOException {

        if (file.getParentFile() != null && !file.getParentFile().exists()) {

            file.getParentFile().mkdirs();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {

            for (Map.Entry<Integer, Long> entry : locations.entrySet()) {

                writer.write(entry.getKey() + "|" + entry.getValue());
                writer.newLine();
            }
        }
    }

    /*
     * Load mappings from a metadata file.
     * If the file does not exist, leaves the map empty.
     */
    public void load(
            String filePath) throws IOException {

        load(new File(filePath));
    }

    public void load(
            File file) throws IOException {

        locations.clear();

        if (!file.exists()) {

            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

            String line;

            while ((line = reader.readLine()) != null) {

                if (line.trim().isEmpty()) {

                    continue;
                }

                String[] parts = line.split("\\|");

                if (parts.length != 2) {

                    throw new IOException(
                            "Malformed metadata line: " + line);
                }

                try {

                    int pageId = Integer.parseInt(parts[0]);
                    long offset = Long.parseLong(parts[1]);

                    locations.put(pageId, offset);

                } catch (NumberFormatException e) {

                    throw new IOException(
                            "Invalid numeric value in metadata line: " + line,
                            e);
                }
            }
        }
    }
}
