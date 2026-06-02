package row;

import java.util.List;
import java.util.ArrayList;

public class Row {

    /*
     * Actual values stored in row.
     */
    private List<String> values;

    /*
     * Constructor
     */
    public Row(
            List<String> values) {

        /*
         * Create a mutable copy.
         *
         * Prevents problems when
         * List.of(...) is passed.
         */
        this.values =
                new ArrayList<>(
                        values
                );
    }

    /*
     * Get row values.
     */
    public List<String> getValues() {

        return values;
    }
}