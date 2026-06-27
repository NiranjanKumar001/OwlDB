package btree;

import page.RID;

import java.util.ArrayList;
import java.util.List;

public class BTree {

        private static final int MAX_KEYS = 3;

        /*
         * Root can now be:
         * LeafNode or InternalNode
         */
        private BTreeNode root;

        public BTree() {

                root = new LeafNode();
        }

        /*
         * Insert key + RID.
         */
        public void insert(
                        int key,
                        RID rid) {

                /*
                 * Root is still a leaf.
                 */
                if (root instanceof LeafNode) {

                        LeafNode leaf = (LeafNode) root;

                        insertIntoLeaf(
                                        leaf,
                                        key,
                                        rid);

                        /*
                         * Split if full.
                         */
                        if (isFull(
                                        leaf)) {

                                root = splitRoot(
                                                leaf);

                        }

                        return;
                }

                /*
                 * Root became internal.
                 */
                insertIntoInternal(
                                (InternalNode) root,
                                key,
                                rid);
        }

        /*
         * Insert into a leaf.
         */
        private void insertIntoLeaf(
                        LeafNode leaf,
                        int key,
                        RID rid) {

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
        }

        /*
         * Navigate from internal root.
         */
        private void insertIntoInternal(
                        InternalNode node,
                        int key,
                        RID rid) {

                int promotedKey = node.getKeys()
                                .get(0);

                LeafNode target;

                if (key < promotedKey) {

                        target = (LeafNode) node.getChildren()
                                        .get(0);

                } else {

                        target = (LeafNode) node.getChildren()
                                        .get(1);
                }

                insertIntoLeaf(
                                target,
                                key,
                                rid);

                /*
                 * Detect overflow.
                 */
                if (isFull(
                                target)) {

                        splitChildLeaf(
                                        node,
                                        target);
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

                LeafNode left = new LeafNode();

                LeafNode right = new LeafNode();

                int middle = oldRoot.getKeys()
                                .size() / 2;

                /*
                 * Left side.
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
                 * Right side.
                 */
                for (int i = middle; i < oldRoot.getKeys().size(); i++) {

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

        /*
         * Split child leaf.
         */
        private void splitChildLeaf(
                        InternalNode parent,
                        LeafNode child) {

                LeafNode right = new LeafNode();

                int middle = child.getKeys()
                                .size() / 2;

                while (child.getKeys()
                                .size() > middle) {

                        right.getKeys()
                                        .add(
                                                        child.getKeys()
                                                                        .remove(
                                                                                        middle));

                        right.getRids()
                                        .add(
                                                        child.getRids()
                                                                        .remove(
                                                                                        middle));
                }

                int promotedKey = right.getKeys()
                                .get(0);

                parent.getKeys()
                                .add(
                                                promotedKey);

                parent.getKeys()
                                .sort(
                                                Integer::compareTo);

                int childIndex = parent.getChildren()
                                .indexOf(
                                                child);

                parent.addChild(
                                childIndex + 1,
                                right);

                System.out.println(
                                "\nChild Split!");

                System.out.println(
                                "Promoted: "
                                                + promotedKey);
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

                        LeafNode leaf = (LeafNode) root;

                        System.out.println(
                                        leaf.getKeys());

                        return;
                }

                InternalNode internal = (InternalNode) root;

                System.out.println(
                                "\nRoot:");

                System.out.println(
                                internal.getKeys());

                System.out.println(
                                "\nLeft Child:");

                System.out.println(
                                ((LeafNode) internal.getChildren()
                                                .get(0))
                                                .getKeys());

                System.out.println(
                                "\nRight Child:");

                System.out.println(
                                ((LeafNode) internal.getChildren()
                                                .get(1))
                                                .getKeys());
        }

        /*
         * Search for a key.
         */
        public RID search(
                        int key) {

                if (root instanceof LeafNode) {

                        LeafNode leaf = (LeafNode) root;

                        for (int i = 0; i < leaf.getKeys().size(); i++) {

                                if (leaf.getKeys()
                                                .get(i) == key) {

                                        return leaf.getRids()
                                                        .get(i);
                                }
                        }

                        return null;
                }

                InternalNode internal = (InternalNode) root;

                int promotedKey = internal.getKeys()
                                .get(0);

                LeafNode target;

                if (key < promotedKey) {

                        target = (LeafNode) internal.getChildren()
                                        .get(0);

                } else {

                        target = (LeafNode) internal.getChildren()
                                        .get(1);
                }

                for (int i = 0; i < target.getKeys()
                                .size(); i++) {

                        if (target.getKeys()
                                        .get(i) == key) {

                                return target.getRids()
                                                .get(i);
                        }
                }

                return null;
        }

        /*
         * Search for keys in a range.
         */
        public List<RID> searchRange(
                        int minKey,
                        int maxKey) {

                List<RID> result = new ArrayList<>();

                /*
                 * Tree has only one leaf.
                 */
                if (root instanceof LeafNode) {

                        LeafNode leaf = (LeafNode) root;

                        collectRange(
                                        leaf,
                                        minKey,
                                        maxKey,
                                        result);

                        return result;
                }

                /*
                 * Tree has an internal root.
                 */
                InternalNode internal = (InternalNode) root;

                for (BTreeNode child : internal.getChildren()) {

                        LeafNode leaf = (LeafNode) child;

                        collectRange(
                                        leaf,
                                        minKey,
                                        maxKey,
                                        result);
                }

                return result;
        }

        /*
         * Collect matching RIDs from one leaf.
         */
        private void collectRange(
                        LeafNode leaf,
                        int minKey,
                        int maxKey,
                        List<RID> result) {

                for (int i = 0; i < leaf.getKeys().size(); i++) {

                        int key = leaf.getKeys()
                                        .get(i);

                        if (key >= minKey &&
                                        key <= maxKey) {

                                result.add(
                                                leaf.getRids()
                                                                .get(i));
                        }
                }
        }
}