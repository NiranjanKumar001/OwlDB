package btree;

import page.RID;

public class BTree {

    private LeafNode root;

    public BTree() {

        root =
                new LeafNode();
    }

    /*
     * Insert key + RID.
     */
    public void insert(
            int key,
            RID rid) {

        int position = 0;

        while (position < root.getKeys().size()
                &&
                root.getKeys().get(position)
                        < key) {

            position++;
        }

        root.getKeys()
                .add(
                        position,
                        key);

        root.getRids()
                .add(
                        position,
                        rid);
    }

    public LeafNode getRoot() {

        return root;
    }
}