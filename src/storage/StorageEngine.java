package storage;

import schema.Column;
import schema.Schema;

import java.io.File; //think it as pointer to a file on disk
import java.io.FileWriter; //used to write text to a file
import java.io.IOException; //used for file related exceptions

import table.Table;
import row.Row;

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
}