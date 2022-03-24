package com.example.chipfloorplanningoptimization.representation;

import com.example.chipfloorplanningoptimization.abstract_structures.CModule;
import com.example.chipfloorplanningoptimization.abstract_structures.Point;
import com.example.chipfloorplanningoptimization.gui.IOManager;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class BTree implements Representation<BTree> {

    private BNode<CModule> root;
    private List<BNode<CModule>> nodes;
    private List<List<String>> net;
    private final Random random = new Random();
    private final Runnable[] operations = new Runnable[] {
            () -> {
                BNode<CModule> node = nodes.get(random.nextInt(nodes.size()));
                double height = node.getValue().getHeight();
                node.getValue().setHeight(node.getValue().getWidth());
                node.getValue().setWidth(height);
            }, // op1 - rotate a module
            () -> {
                // step 1: deletion - If has left, replace with left (because lefts only have left child).
                // otherwise, replace with right (no problem since it doesn't have left).
                BNode<CModule> toMove = nodes.get(random.nextInt(nodes.size()));
                BNode<CModule> replacer = toMove.hasLeft() ? toMove.getLeft() : (toMove.hasRight() ? toMove.getRight() : null);

                // 1.1 replacer children
                if (replacer != null) {
                    if (toMove.getLeft() == replacer) {
                        replacer.setRight(toMove.getRight());
                        if (replacer.hasRight()) // if it's not null
                            replacer.getRight().setParent(replacer);
                    }
                    replacer.setParent(toMove.getParent());
                }

                // 1.2 replacer parent
                if (toMove.getParent() != null) {
                    if (toMove == toMove.getParent().getLeft())
                        toMove.getParent().setLeft(replacer);
                    else
                        toMove.getParent().setRight(replacer);
                }
                else if (replacer != null) {
                    replacer.setParent(null);
                    this.root = replacer;
                }

                // step 2: insertion - if it's a left child node, add to the left.
                // if it's a right child: if it has a right child, add to the left.
                // if it doesn't have a left child - add to the left.
                // Otherwise, randomly choose between adding to the left or to the right.
                BNode<CModule> putAfter = nodes.get(random.nextInt(nodes.size()));
                while (putAfter == toMove || putAfter == toMove.getParent())
                    putAfter = nodes.get(random.nextInt(nodes.size())); // make sure it's not put in the same place

                if (putAfter.getParent() != null && putAfter == putAfter.getParent().getRight() && !putAfter.hasRight() && putAfter.hasLeft() && Math.random() < 0.5) { // add to right
                    toMove.setLeft(null);
                    toMove.setRight(putAfter.getRight());
                    putAfter.setRight(toMove);
                    if (toMove.hasRight())
                        toMove.getRight().setParent(toMove);
                }
                else { // add to left
                    toMove.setRight(null);
                    toMove.setLeft(putAfter.getLeft());
                    putAfter.setLeft(toMove);
                    if (toMove.hasLeft())
                        toMove.getLeft().setParent(toMove);
                }
                toMove.setParent(putAfter);



            }, // op2 - move node to another place
            () -> {
                BNode<CModule> node = nodes.get(random.nextInt(nodes.size()));
                BNode<CModule> randNode = nodes.get(random.nextInt(nodes.size()));
                CModule module = node.getValue();
                node.setValue(randNode.getValue());
                randNode.setValue(module);
            }  // op3 - swap two nodes
    };


    public BTree() {
        nodes = new LinkedList<>();
    }

    public BTree(BTree t) {
        this();
        if (t.root == null)
            return;
        this.root = new BNode<>(new CModule(t.root.getValue()), null);
        nodes.add(this.root);
        copyTree(t.root, this.root);
    }

    public BTree(BNode<CModule> root, List<BNode<CModule>> nodes) {
        this.root = root;
        this.nodes = nodes;
    }

    /**
     * Helper to copy another tree
     * @param pos current node on given tree
     * @param copyTo current node to of the copy-to tree
     */
    private void copyTree(BNode<CModule> pos, BNode<CModule> copyTo) {
        if (pos.hasLeft()) {
            copyTo.setLeft(new BNode<>(new CModule(pos.getLeft().getValue()), copyTo));
            nodes.add(copyTo.getLeft());
            copyTree(pos.getLeft(), copyTo.getLeft());
        }

        if (pos.hasRight()) {
            copyTo.setRight(new BNode<>(new CModule(pos.getRight().getValue()), copyTo));
            nodes.add(copyTo.getRight());
            copyTree(pos.getRight(), copyTo.getRight());
        }
    }

    @Override
    public Runnable[] operations() {
        return operations;
    }

    @Override
    public void perturb() {
        operations[random.nextInt(operations.length)].run();
    }

    @Override
    public void pack(Floorplan floorplan) {
        if (floorplan.getModules().isEmpty())
            return;

        nodes = new LinkedList<>();
        Floorplan copyFloorplan = new Floorplan(floorplan);
        Queue<CModule> modules = copyFloorplan.getModulesQueue();

        BNode<CModule> beforeTree = new BNode<>(null, null);
        BNode<CModule> pos = beforeTree;
        double minimumWidth = (modules.size() / 2. * modules.peek().getWidth());
        double widthBound = Math.random() * (Math.sqrt(copyFloorplan.area()) - minimumWidth) + minimumWidth;
        while (!modules.isEmpty()) {
            pos.setRight(new BNode<>(modules.poll(), pos));
            pos = pos.getRight();
            nodes.add(pos);
            double width = pos.getValue().getWidth();
            widthBound = packLeft(pos, width, widthBound, modules);
        }

        this.net = floorplan.getNameNet();
        this.root = beforeTree.getRight();
        this.root.setParent(null);
    }

    private double packLeft(BNode<CModule> pos, double width, double widthBound, Queue<CModule> modules) {
        if (modules.isEmpty() || width + modules.peek().getWidth() > widthBound)
            return width;

        pos.setLeft(new BNode<>(modules.poll(), pos));
        nodes.add(pos.getLeft());
        return packLeft(pos.getLeft(), width + pos.getLeft().getValue().getWidth(), widthBound, modules);
    }

    /**
     * Unpack a B* Tree to a floorplan, based on the relative position of the left-bottom module (0, 0).
     * @return a floorplan with meaningful positions
     */
    @Override
    public Floorplan unpack() {
        LinkedList<Point> contour = new LinkedList<>() {{
            add(new Point(0, 0));
            add(new Point(100000000, 0));
        }};
        Floorplan result = new Floorplan();
        unpack(0, this.root, result, contour);
        result.setNet(net);
        return result;
    }

    @Override
    public BTree copy() {
        return new BTree(this);
    }

    @Override
    public void save(String path) throws IOException {
        IOManager.saveList(path, nodes, (node) ->
                  (node.hasLeft() ? node.getLeft().getValue().getName() : "null") + ";"
                + (node.hasRight() ? node.getRight().getValue().getName() : "null") + ";"
                + (node.getParent() != null ? node.getParent().getValue().getName() : "null") + ";"
                + node.getValue().serialize());
    }

    public static BTree loadTree(String path) throws FileNotFoundException {
        List<String[]> references = new LinkedList<>();
        List<BNode<CModule>> nodes = IOManager.loadList(path, (t) -> {
            String[] data = t.split(";");
            references.add(new String [] {data[0], data[1], data[2]});
            return new BNode<>(CModule.deserialize(data[3]), null);
        });

        BNode<CModule> root = null;
        for (int i = 0; i < nodes.size(); i++) {
            BNode<CModule> node = nodes.get(i);
            String[] data = references.get(i);
            BNode<CModule> left = null;
            BNode<CModule> right = null;
            BNode<CModule> parent = null;

            if (!data[0].equals("null"))
                left = nodes.stream()
                        .filter(n -> n.getValue().getName().equals(data[0])).findFirst().get();
            if (!data[1].equals("null"))
                right = nodes.stream()
                        .filter(n -> n.getValue().getName().equals(data[1])).findFirst().get();
            if (!data[2].equals("null"))
                parent = nodes.stream()
                        .filter(n -> n.getValue().getName().equals(data[2])).findFirst().get();

            node.setLeft(left);
            node.setRight(right);
            node.setParent(parent);

            if (parent == null)
                root = node;
        }

        return new BTree(root, nodes);
    }

    /**
     * Get a name to CModule map, with the ORIGINAL CModules!
     * @return name to CModule map
     */
    @Override
    public HashMap<String, CModule> createNameModuleMap() {
        HashMap<String, CModule> modulesByNames = new HashMap<>();
        for (BNode<CModule> node : nodes)
            modulesByNames.put(node.getValue().getName(), node.getValue());
        return modulesByNames;
    }

    @Override
    public String serialize() {

        BNode<CModule> pos = this.root;
        StringBuilder text = new StringBuilder();
        while (pos != null) {
            serializeLeftBranch(pos, text, true);
            pos = pos.getRight();
        }

        return text.toString();
    }

    private void serializeLeftBranch(BNode<CModule> pos, StringBuilder text, boolean first) {
        if (pos == null) {
            text.append("|");
            return;
        }

        if (!first)
            text.append(",");

        text.append(pos.getValue().getName());
        serializeLeftBranch(pos.getLeft(), text, false);
    }

    @Override
    public BTree deserialize(String text, HashMap<String, CModule> modulesByNames) {
        return deserializeBTree(text, modulesByNames);
    }

    public static BTree deserializeBTree(String text, HashMap<String, CModule> modulesByNames) {
        List<BNode<CModule>> nodes = new LinkedList<>();

        String[] leftBranches = text.split("\\|");

        ArrayList<BNode<CModule>> rightBranch = new ArrayList<>(leftBranches.length);

        for (String leftBranchData : leftBranches) {
            ArrayList<BNode<CModule>> leftBranch = Arrays.stream(leftBranchData.split(","))
                    .map( (name) -> {
                        BNode<CModule> node = new BNode<>(new CModule(modulesByNames.get(name)), null);
                        nodes.add(node);
                        return node;})
                    .collect(Collectors.toCollection(ArrayList::new));
            buildBranch(leftBranch, true);
            rightBranch.add(leftBranch.get(0));
        }

        buildBranch(rightBranch, false);

        return new BTree(rightBranch.get(0), nodes);
    }

    private static void buildBranch(ArrayList<BNode<CModule>> nodes, boolean goLeft) {
        if (nodes.size() <= 1) // no branch or one node => no need to make connections
            return;

        for (int i = 1; i < nodes.size(); i++) {
            nodes.get(i).setParent(nodes.get(i - 1));

            if (goLeft)
                nodes.get(i - 1).setLeft(nodes.get(i));
            else
                nodes.get(i - 1).setRight(nodes.get(i));
        }
    }

    public static BTree packFloorplan(Floorplan floorplan) {
        BTree tree = new BTree();
        tree.pack(floorplan);
        return tree;
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
    private void unpack(double x, BNode<CModule> pos, Floorplan result, LinkedList<Point> contour) {
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
        while (it.hasNext() && curr.x() < x + width) { // replaced <= with <
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
        CModule module = new CModule(pos.getValue());
        module.setPosition(new Point(x, maxY));

        // Add module
        result.addModule(module);

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
