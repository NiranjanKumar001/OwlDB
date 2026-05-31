package schema;

// Java doesn't know what List means by default.So we import it.

import java.util.List;

//

public class Schema {

    private String tableName;
    private List<Column> columns; 

    public Schema(String tableName, List<Column> columns) {
        this.tableName = tableName;
        this.columns = columns;
    }

    public String getTableName() {
        return tableName;
    }

    public List<Column> getColumns() {
        return columns;
    }
}