package btree;

import page.RID;

import java.util.ArrayList;
import java.util.List;

/*
 * Leaf node of the B+Tree.
 *
 * Stores:
 *  - Keys
 *  - Record IDs (RIDs)
 *  - Pointer to next leaf
 */
public class LeafNode extends BTreeNode {

    /*
     * Record IDs.
     */
    private List<RID> rids;

    /*
     * Next leaf.
     *
     * Used for range scan.
     */
    private LeafNode next;

    public LeafNode() {

        super();

        rids =
                new ArrayList<>();

        next =
                null;
    }

    /*
     * Get RIDs.
     */
    public List<RID> getRids() {

        return rids;
    }

    /*
     * Next leaf.
     */
    public LeafNode getNext() {

        return next;
    }

    public void setNext(
            LeafNode next) {

        this.next =
                next;
    }

    @Override
    public boolean isLeaf() {

        return true;
    }
}