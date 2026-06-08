package btree;

import java.util.ArrayList;
import java.util.List;

/*
 * Base B-Tree node.
 */
public class BTreeNode {

    protected List<Integer> keys;

    protected boolean leaf;

    public BTreeNode(
            boolean leaf) {

        this.leaf =
                leaf;

        this.keys =
                new ArrayList<>();
    }

    public List<Integer> getKeys() {

        return keys;
    }

    public boolean isLeaf() {

        return leaf;
    }
}