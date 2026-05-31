package row;

import java.util.List;

public class Row {

    // Actual values stored in row
    private List<String> values;

    public Row(List<String> values) {
        this.values = values;
    }

    public List<String> getValues() {
        return values;
    }
}