package table;

import schema.Schema;
import row.Row;
import index.Index;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

public class Table {

    /*
     * Blueprint of table.
     */
    private Schema schema;

    /*
     * Table rows.
     */
    private List<Row> rows;

    /*
     * All indexes.
     *
     * Example:
     * id    -> Index
     * name  -> Index
     * age   -> Index
     */
    private Map<String, Index> indexes;

    /*
     * Constructor
     */
    public Table(
            Schema schema) {

        this.schema = schema;

        this.rows =
                new ArrayList<>();

        /*
         * Keeps insertion order.
         */
        this.indexes =
                new LinkedHashMap<>();
    }

    /*
     * Get schema.
     */
    public Schema getSchema() {

        return schema;
    }

    /*
     * Get rows.
     */
    public List<Row> getRows() {

        return rows;
    }

    /*
     * Get indexes.
     */
    public Map<String, Index> getIndexes() {

        return indexes;
    }

    /*
     * Add row.
     */
    public void addRow(
            Row row) {

        rows.add(row);

        /*
         * Update all existing indexes.
         */
        for (String columnName :
                indexes.keySet()) {

            int columnIndex = -1;

            for (int i = 0;
                 i < schema.getColumns().size();
                 i++) {

                if (schema.getColumns()
                        .get(i)
                        .getName()
                        .equals(columnName)) {

                    columnIndex = i;
                    break;
                }
            }

            if (columnIndex != -1) {

                String key =
                        row.getValues()
                                .get(columnIndex);

                indexes.get(columnName)
                        .add(
                                key,
                                row);
            }
        }
    }

    /*
     * Create index on a column.
     */
    public void createIndex(
            String columnName) {

        int columnIndex = -1;

        for (int i = 0;
             i < schema.getColumns().size();
             i++) {

            if (schema.getColumns()
                    .get(i)
                    .getName()
                    .equals(columnName)) {

                columnIndex = i;
                break;
            }
        }

        if (columnIndex == -1) {

            System.out.println(
                    "Column not found: "
                            + columnName);

            return;
        }

        Index index =
                new Index();

        for (Row row : rows) {

            String key =
                    row.getValues()
                            .get(columnIndex);

            index.add(
                    key,
                    row);
        }

        indexes.put(
                columnName,
                index);

        System.out.println(
                "Index created on "
                        + columnName);
    }
}