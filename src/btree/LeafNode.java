package btree;

import page.RID;

import java.util.ArrayList;
import java.util.List;

/*
 * Leaf node.
 *
 * Stores keys + RIDs.
 */
public class LeafNode
        extends BTreeNode {

    private List<RID> rids;

    public LeafNode() {

        super(true);

        this.rids =
                new ArrayList<>();
    }

    public List<RID> getRids() {

        return rids;
    }
}