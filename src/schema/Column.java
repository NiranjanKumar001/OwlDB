package schema;

// This class represents a column in a database table, with a name and a data type.

public class Column {
    private String name;
    private String type;

    //constructor

    public Column(String name, String type) {
        this.name = name;
        this.type = type;
    }

    // Getters

    // Returns the name of the column.

    public String getName() {
        return name;
    }

    // Returns the data type of the column.

    public String getType() {
        return type;
    }
}