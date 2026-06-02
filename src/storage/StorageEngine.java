package storage;

import schema.Column;
import schema.Schema;

import java.io.File; //think it as pointer to a file on disk
import java.io.FileWriter; //used to write text to a file
import java.io.IOException; //used for file related exceptions

import table.Table;
import row.Row;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

//this will Save data, Load data &Manage files

public class StorageEngine {

    /*
     * Save schema to disk.
     *
     * Example:
     *
     * users.schema
     *
     * id:INT
     * name:STRING
     * age:INT
     */
    public void saveSchema(
            Schema schema) {

        try {

            /*
             * Create filename
             */
            String fileName = "../schemas/"
                    + schema.getTableName()
                    + ".schema";

            /*
             * Create file object
             */
            File file = new File(fileName);

            /*
             * Writer used to write text
             */
            FileWriter writer = new FileWriter(file);

            /*
             * Write every column
             */
            for (Column column : schema.getColumns()) {

                writer.write(
                        column.getName()
                                + ":"
                                + column.getType()
                                + "\n");
            }

            writer.close();

            System.out.println(
                    "Schema saved successfully.");

        } catch (IOException e) {

            System.out.println(
                    "Error saving schema.");

            e.printStackTrace();
        }
    }

    /*
     * Save all rows of a table.
     *
     * Example:
     *
     * 1,Niranjan,22
     * 2,Rahul,17
     */
    public void saveRows(Table table) {

        try {

            String fileName = "../data/"
                    + table.getSchema().getTableName()
                    + ".data";

            File file = new File(fileName);

            FileWriter writer = new FileWriter(file);

            /*
             * Visit every row.
             */
            for (Row row : table.getRows()) {

                /*
                 * Get values from row.
                 */
                var values = row.getValues();

                /*
                 * Build:
                 *
                 * 1,Niranjan,22
                 */
                for (int i = 0; i < values.size(); i++) {

                    writer.write(
                            values.get(i)
                                    .toString());

                    /*
                     * Don't put comma
                     * after last value.
                     */
                    if (i != values.size() - 1) {

                        writer.write(",");
                    }
                }

                /*
                 * Move to next row.
                 */
                writer.write("\n");
            }

            writer.close();

            System.out.println(
                    "Rows saved successfully.");

        } catch (IOException e) {

            System.out.println(
                    "Error saving rows.");

            e.printStackTrace();
        }
    }

    public Schema loadSchema(
            String tableName) {

        List<Column> columns = new ArrayList<>();

        try {

            String fileName = "../schemas/"
                    + tableName
                    + ".schema";

            BufferedReader reader = new BufferedReader(
                    new FileReader(
                            fileName));

            String line;

            while ((line = reader.readLine()) != null) {

                String[] parts = line.split(":");

                Column column = new Column(
                        parts[0],
                        parts[1]);

                columns.add(column);
            }

            reader.close();

        } catch (IOException e) {

            e.printStackTrace();
        }

        System.out.println(
        "Schema loaded successfully.");

        return new Schema(
                tableName,
                columns);
    }

    /*
     * Load all rows from a table.
     *
     * Example:
     *
     * users.data
     *
     * 1,Niranjan,22
     * 2,Rahul,17
     */
    public List<Row> loadRows(String tableName) {

        // Store all loaded rows
        List<Row> rows = new ArrayList<>();

        try {

            // Build filename
            String fileName = "../data/"
                    + tableName
                    + ".data";

            // Open file for reading
            BufferedReader reader = new BufferedReader(
                    new FileReader(
                            fileName));

            String line;

            // Read every line
            while ((line = reader.readLine()) != null) {

                /*
                 * Example:
                 *
                 * 1,Niranjan,22
                 */
                String[] parts = line.split(",");

                /*
                 * Convert:
                 *
                 * ["1","Niranjan","22"]
                 *
                 * into Row object
                 */
                Row row = new Row(
                        List.of(parts));

                rows.add(row);
            }

            reader.close();

            System.out.println(
                    "Rows loaded successfully.");

        } catch (IOException e) {

            System.out.println(
                    "Error loading rows.");

            e.printStackTrace();
        }

        return rows;
    }

    public Table loadTable(
            String tableName) {

        // Load schema
        Schema schema = loadSchema(
                tableName);

        // Load rows
        List<Row> rows = loadRows(
                tableName);

        // Create table
        Table table = new Table(
                schema);

        // Add loaded rows
        for (Row row : rows) {

            table.addRow(
                    row);
        }

        System.out.println(
                "Table loaded successfully.");

        return table;
    }

}