package storage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
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
     * Save mappings to a metadata file using a temporary file and atomic replacement.
     */
    public void save(
            String filePath) throws IOException {

        if (filePath == null) {

            throw new IllegalArgumentException(
                    "File path cannot be null.");
        }

        save(new File(filePath));
    }

    public void save(
            File file) throws IOException {

        if (file == null) {

            throw new IllegalArgumentException(
                    "File cannot be null.");
        }

        File parentDir = file.getParentFile();

        if (parentDir != null && !parentDir.exists()) {

            parentDir.mkdirs();
        }

        File tempFile = new File(file.getPath() + ".tmp");
        Path tempPath = tempFile.toPath();
        Path targetPath = file.toPath();

        boolean writeSuccessful = false;

        try {

            try (BufferedWriter writer = Files.newBufferedWriter(
                    tempPath,
                    StandardCharsets.UTF_8)) {

                for (Map.Entry<Integer, Long> entry : locations.entrySet()) {

                    writer.write(entry.getKey() + "|" + entry.getValue());
                    writer.newLine();
                }
            }

            try {

                Files.move(
                        tempPath,
                        targetPath,
                        StandardCopyOption.ATOMIC_MOVE,
                        StandardCopyOption.REPLACE_EXISTING);

            } catch (AtomicMoveNotSupportedException e) {

                Files.move(
                        tempPath,
                        targetPath,
                        StandardCopyOption.REPLACE_EXISTING);
            }

            writeSuccessful = true;

        } finally {

            if (!writeSuccessful && tempFile.exists()) {

                tempFile.delete();
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
