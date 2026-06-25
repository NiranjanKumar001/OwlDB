package btree;

import java.util.ArrayList;
import java.util.List;

/*
 * Internal node of the B+Tree.
 *
 * Stores:
 *  - Separator keys
 *  - Child pointers
 */
public class InternalNode extends BTreeNode {

    /*
     * Child nodes.
     *
     * Children count is always
     * keys + 1.
     */
    private List<BTreeNode> children;

    public InternalNode() {

        super();

        children =
                new ArrayList<>();
    }

    /*
     * Get children.
     */
    public List<BTreeNode> getChildren() {

        return children;
    }

    /*
     * Add child.
     */
    public void addChild(
            BTreeNode child) {

        children.add(
                child);

        child.setParent(
                this);
    }

    /*
     * Insert child at position.
     */
    public void addChild(
            int index,
            BTreeNode child) {

        children.add(
                index,
                child);

        child.setParent(
                this);
    }

    /*
     * Remove child.
     */
    public void removeChild(
            BTreeNode child) {

        children.remove(
                child);
    }

    @Override
    public boolean isLeaf() {

        return false;
    }
}