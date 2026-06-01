package storage;

import schema.Column;
import schema.Schema;

import java.io.File; //think it as pointer to a file on disk
import java.io.FileWriter; //used to write text to a file
import java.io.IOException; //used for file related exceptions

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
}