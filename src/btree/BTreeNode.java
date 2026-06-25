package btree;

import java.util.ArrayList;
import java.util.List;

/*
 * Base class for every B-Tree node.
 */
public abstract class BTreeNode {

    /*
     * Sorted keys.
     */
    protected List<Integer> keys;

    /*
     * Parent node.
     */
    protected InternalNode parent;

    public BTreeNode() {

        keys =
                new ArrayList<>();
    }

    /*
     * Get keys.
     */
    public List<Integer> getKeys() {

        return keys;
    }

    /*
     * Parent.
     */
    public InternalNode getParent() {

        return parent;
    }

    public void setParent(
            InternalNode parent) {

        this.parent =
                parent;
    }

    /*
     * Leaf?
     */
    public abstract boolean isLeaf();
}