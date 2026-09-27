package storage;

import page.Page;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;

/*
 * Manages appending and reading pages to/from a single data file.
 *
 * Each record is stored using length-prefixed formatting:
 *   [4-byte int: length of serialized page bytes]
 *   [serialized page bytes in UTF-8]
 */
public class PageFileManager {

    private static final String DEFAULT_FILE_PATH = "pages/pages.data";

    private File file;

    public PageFileManager() {

        this(new File(DEFAULT_FILE_PATH));
    }

    public PageFileManager(String filePath) {

        this(new File(filePath));
    }

    public PageFileManager(File file) {

        this.file = file;
    }

    /*
     * Append a page to the file and return its starting offset.
     */
    public long writePage(Page page) throws IOException {

        if (page == null) {

            throw new IllegalArgumentException(
                    "Page cannot be null.");
        }

        String serialized = PageSerializer.serialize(page);

        byte[] bytes = serialized.getBytes(StandardCharsets.UTF_8);

        if (file.getParentFile() != null && !file.getParentFile().exists()) {

            file.getParentFile().mkdirs();
        }

        try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {

            long offset = raf.length();

            raf.seek(offset);

            raf.writeInt(bytes.length);

            raf.write(bytes);

            return offset;
        }
    }

    /*
     * Read a page starting at the specified byte offset.
     */
    public Page readPage(long offset) throws IOException {

        if (!file.exists()) {

            throw new FileNotFoundException(
                    "Page file not found: " + file.getPath());
        }

        if (offset < 0 || offset >= file.length()) {

            throw new IllegalArgumentException(
                    "Invalid offset: " + offset + " (file length: " + file.length() + ")");
        }

        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {

            raf.seek(offset);

            int length = raf.readInt();

            if (length < 0) {

                throw new IOException(
                        "Corrupted page record: negative length " + length);
            }

            byte[] bytes = new byte[length];

            raf.readFully(bytes);

            String data = new String(bytes, StandardCharsets.UTF_8);

            Page page = PageSerializer.deserialize(data);

            if (page == null) {

                throw new IOException(
                        "Failed to deserialize page at offset " + offset);
            }

            return page;
        }
    }

    public File getFile() {

        return file;
    }

    public long getFileSize() {

        return file.exists() ? file.length() : 0;
    }
}
