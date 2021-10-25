package com.example.chipfloorplanningoptimization.representation;

import com.example.chipfloorplanningoptimization.abstract_structures.CModule;
import com.example.chipfloorplanningoptimization.abstract_structures.Point;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

public class BTree {

    private BNode<CModule> root;

    public BTree() {}

    public BTree(BNode<CModule> root) {
        this.root = root;
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

    /**
     * This function randomly packs a floorplan to a B* tree representation without any
     * consideration of the modules' positions.
     * @param f floorplan
     * @return a B* Tree containing all modules randomly positioned.
     */
    public static BTree packRandomly(Floorplan f) {
        if (f.getModules().isEmpty())
            return null;
        BNode<CModule> root = new BNode<>(f.getModules().get(0));
        BNode<CModule> pos = root;
        for (int i = 1; i < f.getModules().size(); i++) {
            BNode<CModule> currNode = new BNode<>(f.getModules().get(i));
            if (Math.random() > 0.5)
                pos.setLeft(currNode);
            else
                pos.setRight(currNode);
            pos = currNode;
        }

        return new BTree(root);
    }

}
