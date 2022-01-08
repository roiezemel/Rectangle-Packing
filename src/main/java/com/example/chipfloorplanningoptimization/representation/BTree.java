package com.example.chipfloorplanningoptimization.representation;

import com.example.chipfloorplanningoptimization.abstract_structures.CModule;
import com.example.chipfloorplanningoptimization.abstract_structures.Point;

import java.util.*;

public class BTree implements Representation {

    private BNode<CModule> root;
    private List<BNode> nodes; // TODO: update this list

    public BTree() {}

    public BTree(BTree t) {
        if (t.root == null)
            return;
        this.root = new BNode<>(new CModule(t.root.getValue()));
        copyTree(t.root, this.root);
    }

    public BTree(BNode<CModule> root) {
        this.root = root;
    }

    /**
     * Helper to copy another tree
     * @param pos current node on given tree
     * @param copyTo current node to of the copy-to tree
     */
    private void copyTree(BNode<CModule> pos, BNode<CModule> copyTo) {
        if (pos.hasLeft()) {
            copyTo.setLeft(new BNode<>(new CModule(pos.getLeft().getValue())));
            copyTree(pos.getLeft(), copyTo.getLeft());
        }

        if (pos.hasRight()) {
            copyTo.setRight(new BNode<>(new CModule(pos.getRight().getValue())));
            copyTree(pos.getRight(), copyTo.getRight());
        }
    }

    @Override
    public Runnable[] operations() {
        // TODO: implement operations
        return new Runnable[] {
                () -> {
                    BNode<CModule> node = nodes.get(new Random().nextInt(nodes.size()));
                    double height = node.getValue().getHeight();
                    node.getValue().setHeight(node.getValue().getWidth());
                    node.getValue().setWidth(height);
                }, // op1
                () -> {
                    BNode<CModule> randNode = nodes.get(new Random().nextInt(nodes.size()));

                }, // op2
                () -> {
                    BNode<CModule> node = nodes.get(new Random().nextInt(nodes.size()));
                    BNode<CModule> randNode = nodes.get(new Random().nextInt(nodes.size()));
                    CModule module = node.getValue();
                    node.setValue(randNode.getValue());
                    randNode.setValue(module);
                }  // op3
        };
    }

    @Override
    public void pack(Floorplan floorplan) {
        if (floorplan.getModules().isEmpty())
            return;

        Floorplan copyFloorplan = new Floorplan(floorplan);
        Queue<CModule> modules = copyFloorplan.getModulesQueue();

        BNode<CModule> beforeTree = new BNode<>(null);
        BNode<CModule> pos = beforeTree;
        double minimumWidth = (modules.size() / 2. * modules.peek().getWidth());
        double widthBound = Math.random() * (Math.sqrt(copyFloorplan.area()) - minimumWidth) + minimumWidth;
        while (!modules.isEmpty()) {
            pos.setRight(new BNode<>(modules.poll()));
            pos = pos.getRight();
            double width = pos.getValue().getWidth();
            widthBound = packLeft(pos, width, widthBound, modules);
        }

        this.root = beforeTree.getRight();
    }

    private double packLeft(BNode<CModule> pos, double width, double widthBound, Queue<CModule> modules) {
        if (modules.isEmpty() || width + modules.peek().getWidth() > widthBound)
            return width;

        pos.setLeft(new BNode<>(modules.poll()));
        return packLeft(pos.getLeft(), width + pos.getLeft().getValue().getWidth(), widthBound, modules);
    }

    @Override
    public Floorplan unpack() {
        return unpack(this);
    }

    public static BTree packFloorplan(Floorplan floorplan) {
        BTree tree = new BTree();
        tree.pack(floorplan);
        return tree;
    }

    /**
     * Unpack a B* Tree to a floorplan, based on the relative position of the left-bottom module (0, 0).
     * @param tree B* Tree representing a floorplan
     * @return a floorplan with meaningful positions
     */
    public static Floorplan unpack(BTree tree) {
        LinkedList<Point> contour = new LinkedList<>() {{
            add(new Point(0, 0));
            add(new Point(100000000, 0));
        }};

        Floorplan result = new Floorplan();
        unpack(0, tree.root, result, contour);
        return result;
    }

    /**
     * This function is used for recursion. It handles a sub-tree:
     * - First it calculates the current module's position and adds it to the floorplan result.
     * - Then it recursively applies itself on both the left and right subtrees.
     * @param x X value of the current module
     * @param pos Current node (sub-tree)
     * @param result result floorplan
     * @param contour a helper structure for an efficient calculation of the Y values
     */
    private static void unpack(double x, BNode<CModule> pos, Floorplan result, LinkedList<Point> contour) {
        if (pos == null)
            return;

        // Calculate new points to add to the contour structure
        double width = pos.getValue().getWidth(), height = pos.getValue().getHeight();


        // Find Y value of current module
        int index = 0, start = 1;
        boolean startSet = false;
        double maxY = -1;
        Iterator<Point> it = contour.iterator();
        Point curr = it.next();
        while (it.hasNext() && curr.x() <= x + width) {
            Point next = it.next();
            if (!startSet && next.x() > x) { // curr is the start
                start = index;
                startSet = true;
            }
            if (startSet && curr.y() > maxY) // Look for max Y in range
                maxY = curr.y();
            curr = next;
            index++;
        }

        // Update current module's position
        pos.getValue().setPosition(new Point(x, maxY));

        // Add module
        result.addModule(pos.getValue());

        // Update contour structure
        Point topLeft = new Point(x, maxY + height);
        Point topRight = new Point(x + width, maxY + height);
        Point bottomRight = new Point(x + width,maxY);

        Point after = contour.get(start);
        if (after.x() != 0 || after.y() != 0) {
            contour.remove(start--);
        }
        contour.addAll(start + 1, Arrays.stream(new Point[] {topLeft, topRight, bottomRight}).toList());

        // Apply on children
        unpack(x + width, pos.getLeft(), result, contour); // left
        unpack(x, pos.getRight(), result, contour); // right
    }
}
