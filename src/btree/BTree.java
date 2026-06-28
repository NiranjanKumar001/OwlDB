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

        private void insertIntoInternal(
                        InternalNode node,
                        int key,
                        RID rid) {

                int childIndex = 0;

                while (childIndex < node.getKeys().size()
                                && key >= node.getKeys().get(childIndex)) {

                        childIndex++;
                }

                BTreeNode child = node.getChildren()
                                .get(childIndex);

                /*
                 * Reached leaf.
                 */
                if (child instanceof LeafNode) {

                        LeafNode target = (LeafNode) child;

                        insertIntoLeaf(
                                        target,
                                        key,
                                        rid);

                        if (isFull(target)) {

                                splitChildLeaf(
                                                node,
                                                target);
                        }

                        return;
                }

                /*
                 * Keep traversing.
                 */
                insertIntoInternal(
                                (InternalNode) child,
                                key,
                                rid);
        }

        /*
         * Check if leaf is full.
         */
        private boolean isFull(
                        LeafNode node) {

                return node.getKeys()
                                .size() > MAX_KEYS;
        }

        private boolean isInternalFull(
                        InternalNode node) {

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

                if (isInternalFull(parent)) {

                        root = splitInternalRoot(parent);

                        System.out.println(
                                        "\nROOT SPLIT!");
                }
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

        public void printTree() {

                printNode(root, 0);
        }

        private void printNode(
                        BTreeNode node,
                        int level) {

                String indent = "  ".repeat(level);

                if (node instanceof LeafNode) {

                        LeafNode leaf = (LeafNode) node;

                        System.out.println(
                                        indent + "Leaf: "
                                                        + leaf.getKeys());

                        return;
                }

                InternalNode internal = (InternalNode) node;

                System.out.println(
                                indent + "Internal: "
                                                + internal.getKeys());

                for (BTreeNode child : internal.getChildren()) {

                        printNode(
                                        child,
                                        level + 1);
                }
        }

        public RID search(
                        int key) {

                return searchNode(
                                root,
                                key);
        }

        private RID searchNode(
                        BTreeNode node,
                        int key) {

                /*
                 * Leaf reached.
                 */
                if (node instanceof LeafNode) {

                        LeafNode leaf = (LeafNode) node;

                        for (int i = 0; i < leaf.getKeys().size(); i++) {

                                if (leaf.getKeys()
                                                .get(i) == key) {

                                        return leaf.getRids()
                                                        .get(i);
                                }
                        }

                        return null;
                }

                /*
                 * Internal node.
                 */
                InternalNode internal = (InternalNode) node;

                int childIndex = 0;

                while (childIndex < internal.getKeys().size()
                                && key >= internal.getKeys().get(childIndex)) {

                        childIndex++;
                }

                return searchNode(
                                internal.getChildren()
                                                .get(childIndex),
                                key);
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
         * Split overflowing internal root.
         */
        private InternalNode splitInternalRoot(
                        InternalNode oldRoot) {

                InternalNode left = new InternalNode();

                InternalNode right = new InternalNode();

                int middle = oldRoot.getKeys()
                                .size() / 2;

                int promotedKey = oldRoot.getKeys()
                                .get(middle);

                /*
                 * Left keys.
                 */
                for (int i = 0; i < middle; i++) {

                        left.getKeys()
                                        .add(
                                                        oldRoot.getKeys()
                                                                        .get(i));
                }

                /*
                 * Right keys.
                 */
                for (int i = middle + 1; i < oldRoot.getKeys().size(); i++) {

                        right.getKeys()
                                        .add(
                                                        oldRoot.getKeys()
                                                                        .get(i));
                }

                /*
                 * Left children.
                 */
                for (int i = 0; i <= middle; i++) {

                        left.addChild(
                                        oldRoot.getChildren()
                                                        .get(i));
                }

                /*
                 * Right children.
                 */
                for (int i = middle + 1; i < oldRoot.getChildren().size(); i++) {

                        right.addChild(
                                        oldRoot.getChildren()
                                                        .get(i));
                }

                InternalNode newRoot = new InternalNode();

                newRoot.getKeys()
                                .add(
                                                promotedKey);

                newRoot.addChild(
                                left);

                newRoot.addChild(
                                right);

                return newRoot;
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