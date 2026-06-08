package btree;

import java.util.ArrayList;
import java.util.List;

/*
 * Internal node.
 */
public class InternalNode
        extends BTreeNode {

    private List<BTreeNode> children;

    public InternalNode() {

        super(false);

        children =
                new ArrayList<>();
    }

    public List<BTreeNode> getChildren() {

        return children;
    }
}