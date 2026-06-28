package app;

import btree.BTree;
import page.PageManager;
import page.RID;
import row.Row;

import java.util.List;

public class BTreeTest {

        public static void run() {

                System.out.println(
                                "\nB-Tree Test:");

                PageManager pm = new PageManager(2);

                RID rid1 = pm.insertAndReturnRID(
                                new Row(
                                                List.of(
                                                                "1",
                                                                "A",
                                                                "10")));

                RID rid2 = pm.insertAndReturnRID(
                                new Row(
                                                List.of(
                                                                "2",
                                                                "B",
                                                                "20")));

                RID rid3 = pm.insertAndReturnRID(
                                new Row(
                                                List.of(
                                                                "3",
                                                                "C",
                                                                "30")));

                RID rid4 = pm.insertAndReturnRID(
                                new Row(
                                                List.of(
                                                                "4",
                                                                "D",
                                                                "40")));

                RID rid5 = pm.insertAndReturnRID(
                                new Row(
                                                List.of(
                                                                "5",
                                                                "E",
                                                                "50")));

                BTree tree = new BTree();

                tree.insert(10, rid1);
                tree.insert(20, rid2);
                tree.insert(30, rid3);
                tree.insert(40, rid4);
                tree.insert(50, rid5);
                tree.insert(60, rid5);
                tree.insert(70, rid5);
                tree.insert(80, rid5);
                tree.insert(90, rid5);
                tree.insert(100, rid5);
                tree.insert(110, rid5);

                tree.printTree();

                System.out.println(
                                "\nB-Tree Search Test:");

                RID rid = tree.search(30);

                System.out.println(
                                "RID: " + rid);

                System.out.println(
                                "Row: " +
                                                pm.getRow(rid)
                                                                .getValues());

                System.out.println(
                                "\nB-Tree Range Search Test:");

                for (RID rangeRid : tree.searchRange(20, 40)) {

                        System.out.println(
                                        pm.getRow(rangeRid)
                                                        .getValues());
                }

                /*
                 * Trigger child overflow.
                 */
                tree.insert(
                                60,
                                rid5);
                System.out.println(tree.search(30));
                System.out.println(tree.search(50));
                System.out.println(tree.search(70));
                System.out.println(
                                "\nOWLET-052 Leaf Chain Test:");

                var leaf = tree.getLeftMostLeaf();

                while (leaf != null) {

                        System.out.println(
                                        leaf.getKeys());

                        leaf = leaf.getNext();
                }
        }
}