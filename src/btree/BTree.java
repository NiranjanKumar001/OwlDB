package btree;

import page.RID;

public class BTree {

        private static final int MAX_KEYS = 3;

        private LeafNode root;

        public BTree() {

                root = new LeafNode();
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
                                root.getKeys().get(position) < key) {

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
                if (isFull(
                                root)) {

                        InternalNode newRoot = splitRoot();

                        System.out.println(
                                        "\nRoot Split!");

                        System.out.println(
                                        "Promoted Key: "
                                                        + newRoot.getKeys());

                        System.out.println(
                                        "Left: "
                                                        + ((LeafNode) newRoot.getChildren()
                                                                        .get(0))
                                                                        .getKeys());

                        System.out.println(
                                        "Right: "
                                                        + ((LeafNode) newRoot.getChildren()
                                                                        .get(1))
                                                                        .getKeys());
                }
        }

        public LeafNode getRoot() {

                return root;
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
        private InternalNode splitRoot() {

                LeafNode oldRoot = root;

                LeafNode left = new LeafNode();

                LeafNode right = new LeafNode();

                int middle = oldRoot.getKeys()
                                .size() / 2;

                /*
                 * Left side
                 */
                for (int i = 0; i < middle; i++) {

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
                for (int i = middle; i < oldRoot.getKeys()
                                .size(); i++) {

                        right.getKeys()
                                        .add(
                                                        oldRoot.getKeys()
                                                                        .get(i));

                        right.getRids()
                                        .add(
                                                        oldRoot.getRids()
                                                                        .get(i));
                }

                InternalNode newRoot = new InternalNode();

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
}