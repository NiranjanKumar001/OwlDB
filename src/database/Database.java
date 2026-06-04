package database;

import table.Table;
import row.Row;
import schema.Column;

import java.util.HashMap;
import java.util.Map;

import index.Index;

import java.util.List;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;

import storage.StorageEngine;

public class Database {

        /*
         * Stores all tables.
         *
         * Example:
         *
         * users -> Users Table
         * products -> Products Table
         */
        private Map<String, Table> tables;

        private StorageEngine storageEngine;

        /*
         * Constructor
         */
        public Database() {

                tables = new HashMap<>();
                storageEngine = new StorageEngine();
        }

        /*
         * Create a table inside database.
         */
        public void createTable(
                        Table table) {

                String tableName = table.getSchema()
                                .getTableName();

                tables.put(
                                tableName,
                                table);

                storageEngine.saveSchema(
                                table.getSchema());

                storageEngine.saveRows(
                                table);

                /*
                 * Save index metadata.
                 */
                storageEngine.saveIndexes(
                                table);

                System.out.println(
                                "Table created: "
                                                + tableName);
        }

        /*
         * Get a table by name.
         */
        public Table getTable(
                        String tableName) {

                return tables.get(
                                tableName);
        }

        /*
         * Insert a row into a table.
         */
        public void insert(
                        String tableName,
                        Row row) {

                Table table = tables.get(
                                tableName);

                if (table == null) {

                        System.out.println(
                                        "Table not found: "
                                                        + tableName);

                        return;
                }

                table.addRow(
                                row);
                storageEngine.saveRows(
                                table);

                System.out.println(
                                "Row inserted into "
                                                + tableName);
        }

        /*
         * SELECT * FROM table
         */
        public List<Row> selectAll(
                        String tableName) {

                Table table = tables.get(
                                tableName);

                if (table == null) {

                        System.out.println(
                                        "Table not found: "
                                                        + tableName);

                        return new ArrayList<>();
                }

                return table.getRows();
        }

        /*
         * SELECT *
         * FROM table
         * WHERE column = value
         */
        public List<Row> selectWhere(
                        String tableName,
                        String columnName,
                        String value) {

                Table table = tables.get(
                                tableName);

                if (table == null) {

                        System.out.println(
                                        "Table not found: "
                                                        + tableName);

                        return new ArrayList<>();
                }

                /*
                 * Use index if available.
                 */
                Index index = table.getIndexes()
                                .get(columnName);

                if (index != null) {

                        System.out.println(
                                        "Using index lookup.");

                        return index.find(
                                        value);
                }

                /*
                 * Normal scan.
                 */
                int columnIndex = getColumnIndex(
                                table,
                                columnName);

                if (columnIndex == -1) {

                        System.out.println(
                                        "Column not found: "
                                                        + columnName);

                        return new ArrayList<>();
                }

                List<Row> result = new ArrayList<>();

                for (Row row : table.getRows()) {

                        if (row.getValues()
                                        .get(columnIndex)
                                        .equals(value)) {

                                result.add(row);
                        }
                }

                return result;
        }

        /*
         * SELECT *
         * FROM table
         * WHERE column > value
         */
        public List<Row> selectGreaterThan(
                        String tableName,
                        String columnName,
                        String value) {

                Table table = tables.get(
                                tableName);

                if (table == null) {

                        System.out.println(
                                        "Table not found: "
                                                        + tableName);

                        return new ArrayList<>();
                }

                int columnIndex = getColumnIndex(
                                table,
                                columnName);

                if (columnIndex == -1) {

                        System.out.println(
                                        "Column not found: "
                                                        + columnName);

                        return new ArrayList<>();
                }

                List<Row> result = new ArrayList<>();

                int targetValue = Integer.parseInt(
                                value);

                for (Row row : table.getRows()) {

                        int rowValue = Integer.parseInt(
                                        row.getValues()
                                                        .get(columnIndex));

                        if (rowValue > targetValue) {

                                result.add(
                                                row);
                        }
                }

                return result;
        }

        /*
         * SELECT *
         * FROM table
         * WHERE column < value
         */
        public List<Row> selectLessThan(
                        String tableName,
                        String columnName,
                        String value) {

                Table table = tables.get(
                                tableName);

                if (table == null) {

                        System.out.println(
                                        "Table not found: "
                                                        + tableName);

                        return new ArrayList<>();
                }

                int columnIndex = getColumnIndex(
                                table,
                                columnName);

                if (columnIndex == -1) {

                        System.out.println(
                                        "Column not found: "
                                                        + columnName);

                        return new ArrayList<>();
                }

                List<Row> result = new ArrayList<>();

                int targetValue = Integer.parseInt(
                                value);

                for (Row row : table.getRows()) {

                        int rowValue = Integer.parseInt(
                                        row.getValues()
                                                        .get(columnIndex));

                        if (rowValue < targetValue) {

                                result.add(
                                                row);
                        }
                }

                return result;
        }

        /*
         * SELECT *
         * FROM table
         * WHERE column >= value
         */
        public List<Row> selectGreaterThanOrEqual(
                        String tableName,
                        String columnName,
                        String value) {

                Table table = tables.get(
                                tableName);

                if (table == null) {

                        System.out.println(
                                        "Table not found: "
                                                        + tableName);

                        return new ArrayList<>();
                }

                int columnIndex = getColumnIndex(
                                table,
                                columnName);

                if (columnIndex == -1) {

                        System.out.println(
                                        "Column not found: "
                                                        + columnName);

                        return new ArrayList<>();
                }

                List<Row> result = new ArrayList<>();

                int targetValue = Integer.parseInt(
                                value);

                for (Row row : table.getRows()) {

                        int rowValue = Integer.parseInt(
                                        row.getValues()
                                                        .get(columnIndex));

                        if (rowValue >= targetValue) {

                                result.add(
                                                row);
                        }
                }

                return result;
        }

        /*
         * SELECT *
         * FROM table
         * WHERE column <= value
         */
        public List<Row> selectLessThanOrEqual(
                        String tableName,
                        String columnName,
                        String value) {

                Table table = tables.get(
                                tableName);

                if (table == null) {

                        System.out.println(
                                        "Table not found: "
                                                        + tableName);

                        return new ArrayList<>();
                }

                int columnIndex = getColumnIndex(
                                table,
                                columnName);

                if (columnIndex == -1) {

                        System.out.println(
                                        "Column not found: "
                                                        + columnName);

                        return new ArrayList<>();
                }

                List<Row> result = new ArrayList<>();

                int targetValue = Integer.parseInt(
                                value);

                for (Row row : table.getRows()) {

                        int rowValue = Integer.parseInt(
                                        row.getValues()
                                                        .get(columnIndex));

                        if (rowValue <= targetValue) {

                                result.add(
                                                row);
                        }
                }

                return result;
        }

        /*
         * ORDER BY column ASC
         */
        public List<Row> orderBy(
                        String tableName,
                        String columnName) {

                Table table = tables.get(
                                tableName);

                if (table == null) {

                        System.out.println(
                                        "Table not found: "
                                                        + tableName);

                        return new ArrayList<>();
                }

                int columnIndex = getColumnIndex(
                                table,
                                columnName);

                if (columnIndex == -1) {

                        System.out.println(
                                        "Column not found: "
                                                        + columnName);

                        return new ArrayList<>();
                }

                List<Row> result = new ArrayList<>(
                                table.getRows());

                result.sort(
                                Comparator.comparingInt(
                                                row -> Integer.parseInt(
                                                                row.getValues()
                                                                                .get(columnIndex))));

                return result;
        }

        /*
         * DELETE
         * FROM table
         * WHERE column = value
         */
        public void deleteWhere(
                        String tableName,
                        String columnName,
                        String value) {

                Table table = tables.get(
                                tableName);

                if (table == null) {

                        System.out.println(
                                        "Table not found: "
                                                        + tableName);

                        return;
                }

                int columnIndex = getColumnIndex(
                                table,
                                columnName);

                if (columnIndex == -1) {

                        System.out.println(
                                        "Column not found: "
                                                        + columnName);

                        return;
                }

                int finalColumnIndex = columnIndex;

                table.getRows().removeIf(
                                row -> row.getValues()
                                                .get(finalColumnIndex)
                                                .equals(value));
                table.rebuildIndexes();
                storageEngine.saveRows(
                                table);

                storageEngine.saveIndexes(
                                table);

                System.out.println(
                                "Rows deleted successfully.");
        }

        /*
         * UPDATE table
         * SET updateColumn = newValue
         * WHERE whereColumn = whereValue
         */
        public void updateWhere(
                        String tableName,
                        String whereColumn,
                        String whereValue,
                        String updateColumn,
                        String newValue) {

                Table table = tables.get(
                                tableName);

                if (table == null) {

                        System.out.println(
                                        "Table not found: "
                                                        + tableName);

                        return;
                }

                int whereColumnIndex = getColumnIndex(
                                table,
                                whereColumn);

                int updateColumnIndex = getColumnIndex(
                                table,
                                updateColumn);

                if (whereColumnIndex == -1) {

                        System.out.println(
                                        "Column not found: "
                                                        + whereColumn);

                        return;
                }

                if (updateColumnIndex == -1) {

                        System.out.println(
                                        "Column not found: "
                                                        + updateColumn);

                        return;
                }

                int updatedRows = 0;

                for (Row row : table.getRows()) {

                        if (row.getValues()
                                        .get(whereColumnIndex)
                                        .equals(whereValue)) {

                                row.getValues()
                                                .set(
                                                                updateColumnIndex,
                                                                newValue);

                                updatedRows++;
                        }
                }
                table.rebuildIndexes();
                storageEngine.saveRows(
                                table);

                storageEngine.saveIndexes(
                                table);

                System.out.println(
                                updatedRows
                                                + " row(s) updated.");
        }

        /*
         * Find column index.
         *
         * Example:
         *
         * id -> 0
         * name -> 1
         * age -> 2
         */
        private int getColumnIndex(
                        Table table,
                        String columnName) {

                List<Column> columns = table.getSchema()
                                .getColumns();

                for (int i = 0; i < columns.size(); i++) {

                        if (columns.get(i)
                                        .getName()
                                        .equals(columnName)) {

                                return i;
                        }
                }

                return -1;
        }

        /*
         * Load table from disk
         */
        public void loadTable(
                        String tableName) {

                Table table = storageEngine.loadTable(
                                tableName);

                if (table == null) {

                        System.out.println(
                                        "Failed to load table: "
                                                        + tableName);

                        return;
                }

                tables.put(
                                tableName,
                                table);

                System.out.println(
                                "Table loaded into database: "
                                                + tableName);
        }

        /*
         * Load all tables from schemas folder.
         */
        public void loadAllTables() {

                File schemaFolder = new File(
                                "../schemas");

                File[] files = schemaFolder.listFiles();

                if (files == null) {

                        System.out.println(
                                        "No schema files found.");

                        return;
                }

                for (File file : files) {

                        String fileName = file.getName();

                        if (!fileName.endsWith(
                                        ".schema")) {

                                continue;
                        }

                        String tableName = fileName.replace(
                                        ".schema",
                                        "");

                        loadTable(
                                        tableName);
                }

                System.out.println(
                                "All tables loaded.");
        }

        /*
         * Count rows in a table.
         */
        public int count(
                        String tableName) {

                Table table = tables.get(
                                tableName);

                if (table == null) {

                        System.out.println(
                                        "Table not found: "
                                                        + tableName);

                        return 0;
                }

                return table.getRows()
                                .size();
        }

        /*
         * Find maximum value in a column.
         */
        public int max(
                        String tableName,
                        String columnName) {

                Table table = tables.get(
                                tableName);

                if (table == null) {

                        System.out.println(
                                        "Table not found: "
                                                        + tableName);

                        return 0;
                }

                int columnIndex = getColumnIndex(
                                table,
                                columnName);

                if (columnIndex == -1) {

                        System.out.println(
                                        "Column not found: "
                                                        + columnName);

                        return 0;
                }

                int max = Integer.MIN_VALUE;

                for (Row row : table.getRows()) {

                        int value = Integer.parseInt(
                                        row.getValues()
                                                        .get(columnIndex));

                        if (value > max) {

                                max = value;
                        }
                }

                return max;
        }

        /*
         * Find minimum value in a column.
         */
        public int min(
                        String tableName,
                        String columnName) {

                Table table = tables.get(
                                tableName);

                if (table == null) {

                        System.out.println(
                                        "Table not found: "
                                                        + tableName);

                        return 0;
                }

                int columnIndex = getColumnIndex(
                                table,
                                columnName);

                if (columnIndex == -1) {

                        System.out.println(
                                        "Column not found: "
                                                        + columnName);

                        return 0;
                }

                int min = Integer.MAX_VALUE;

                for (Row row : table.getRows()) {

                        int value = Integer.parseInt(
                                        row.getValues()
                                                        .get(columnIndex));

                        if (value < min) {

                                min = value;
                        }
                }

                return min;
        }

        /*
         * Find sum of a column.
         */
        public int sum(
                        String tableName,
                        String columnName) {

                Table table = tables.get(
                                tableName);

                if (table == null) {

                        System.out.println(
                                        "Table not found: "
                                                        + tableName);

                        return 0;
                }

                int columnIndex = getColumnIndex(
                                table,
                                columnName);

                if (columnIndex == -1) {

                        System.out.println(
                                        "Column not found: "
                                                        + columnName);

                        return 0;
                }

                int sum = 0;

                for (Row row : table.getRows()) {

                        sum += Integer.parseInt(
                                        row.getValues()
                                                        .get(columnIndex));
                }

                return sum;
        }

        /*
         * Find average of a column.
         */
        public int avg(
                        String tableName,
                        String columnName) {

                Table table = tables.get(
                                tableName);

                if (table == null) {

                        System.out.println(
                                        "Table not found: "
                                                        + tableName);

                        return 0;
                }

                if (table.getRows()
                                .isEmpty()) {

                        return 0;
                }

                return sum(
                                tableName,
                                columnName)
                                / table.getRows()
                                                .size();
        }

        /*
         * Select a single column.
         */
        public List<String> selectColumn(
                        String tableName,
                        String columnName) {

                Table table = tables.get(
                                tableName);

                if (table == null) {

                        System.out.println(
                                        "Table not found: "
                                                        + tableName);

                        return new ArrayList<>();
                }

                int columnIndex = getColumnIndex(
                                table,
                                columnName);

                if (columnIndex == -1) {

                        System.out.println(
                                        "Column not found: "
                                                        + columnName);

                        return new ArrayList<>();
                }

                List<String> result = new ArrayList<>();

                for (Row row : table.getRows()) {

                        result.add(
                                        row.getValues()
                                                        .get(columnIndex));
                }

                return result;
        }

}