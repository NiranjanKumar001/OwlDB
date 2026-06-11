package btree;

import page.RID;

public class BTree {

    private static final int MAX_KEYS = 3;

    /*
     * Root can now be:
     * LeafNode or InternalNode
     */
    private BTreeNode root;

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

        /*
         * For now we only support
         * inserting into a leaf root.
         */
        if (!(root instanceof LeafNode)) {

            System.out.println(
                    "Internal root insertion not implemented yet.");

            return;
        }

        LeafNode leaf =
                (LeafNode) root;

        int position = 0;

        while (position < leaf.getKeys().size()
                &&
                leaf.getKeys()
                        .get(position) < key) {

            position++;
        }

        leaf.getKeys()
                .add(
                        position,
                        key);

        leaf.getRids()
                .add(
                        position,
                        rid);

        /*
         * Split if full.
         */
        if (isFull(
                leaf)) {

            root =
                    splitRoot(
                            leaf);
        }
    }

    /*
     * Check if leaf is full.
     */
    private boolean isFull(
            LeafNode node) {

        return node.getKeys()
                .size() > MAX_KEYS;
    }

    /*
     * Split root leaf.
     */
    private InternalNode splitRoot(
            LeafNode oldRoot) {

        LeafNode left =
                new LeafNode();

        LeafNode right =
                new LeafNode();

        int middle =
                oldRoot.getKeys()
                        .size() / 2;

        /*
         * Left side
         */
        for (int i = 0;
             i < middle;
             i++) {

            left.getKeys()
                    .add(
                            oldRoot.getKeys()
                                    .get(i));

            left.getRids()
                    .add(
                            oldRoot.getRids()
                                    .get(i));
        }

        /*
         * Right side
         */
        for (int i = middle;
             i < oldRoot.getKeys()
                     .size();
             i++) {

            right.getKeys()
                    .add(
                            oldRoot.getKeys()
                                    .get(i));

            right.getRids()
                    .add(
                            oldRoot.getRids()
                                    .get(i));
        }

        InternalNode newRoot =
                new InternalNode();

        /*
         * Promote first key
         * from right node.
         */
        newRoot.getKeys()
                .add(
                        right.getKeys()
                                .get(0));

        newRoot.getChildren()
                .add(
                        left);

        newRoot.getChildren()
                .add(
                        right);

        return newRoot;
    }

    /*
     * Get root.
     */
    public BTreeNode getRoot() {

        return root;
    }

    /*
     * Print tree.
     */
    public void printTree() {

        if (root instanceof LeafNode) {

            LeafNode leaf =
                    (LeafNode) root;

            System.out.println(
                    leaf.getKeys());

            return;
        }

        InternalNode internal =
                (InternalNode) root;

        System.out.println(
                "\nRoot:");

        System.out.println(
                internal.getKeys());

        System.out.println(
                "\nLeft Child:");

        System.out.println(
                ((LeafNode)
                        internal.getChildren()
                                .get(0))
                        .getKeys());

        System.out.println(
                "\nRight Child:");

        System.out.println(
                ((LeafNode)
                        internal.getChildren()
                                .get(1))
                        .getKeys());
    }
}