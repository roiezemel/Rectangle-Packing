package com.example.chipfloorplanningoptimization;

import com.example.chipfloorplanningoptimization.abstract_structures.CModule;
import com.example.chipfloorplanningoptimization.gui.IOManager;
import com.example.chipfloorplanningoptimization.representation.BNode;
import com.example.chipfloorplanningoptimization.representation.BTree;
import com.example.chipfloorplanningoptimization.representation.Floorplan;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Utils {

    /**
     * Create an initial solution:
     * A BTree consisting of 6 modules.
     * @return a BTree.
     */
    public static BTree createInitialSolution() {
        CModule m1 = new CModule(9, 6, "1");
        CModule m2 = new CModule(6, 8, "2");
        CModule m3 = new CModule(6, 3, "3");
        CModule m4 = new CModule(3, 7, "4");
        CModule m5 = new CModule(6, 5, "5");
        CModule m6 = new CModule(12, 2, "6");

        BNode<CModule> n2 = new BNode<>(m2, null);
        BNode<CModule> n1 = new BNode<>(m1, n2);
        BNode<CModule> n3 = new BNode<>(m3, n2);
        BNode<CModule> n5 = new BNode<>(m5, n3);
        BNode<CModule> n6 = new BNode<>(m6, n3);
        BNode<CModule> n4 = new BNode<>(m4, n6);

        n2.setLeft(n1);
        n2.setRight(n3);
        n3.setLeft(n5);
        n3.setRight(n6);

        n6.setLeft(n4);

        List<BNode<CModule>> nodes = new LinkedList<>() {{ add(n1); add(n2); add(n3); add(n4); add(n5); add(n6); }};
        return new BTree(n2, nodes);
    }

    /**
     * Load a floorplan from blocks and net files.
     * This method wraps the IOManager tools.
     * @param blocksFilePath path to blocks file.
     * @param netFilePath path to net file.
     * @return a Floorplan.
     */
    public static Floorplan getFloorplanFromFile(String blocksFilePath, String netFilePath) {
        File netsFile = new File(Objects.requireNonNull(Utils.class
                .getResource(netFilePath)).getFile());
        try {
            return IOManager.extractBlocksToFloorplan(
                    new File(Objects.requireNonNull(Utils.class.getResource(blocksFilePath)).getFile()), netsFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Load a serialized solution (of a BTree) to an instance of Floorplan.
     * @param filePath path to serialized file.
     * @return a Floorplan.
     * @throws FileNotFoundException
     */
    public static Floorplan loadSolution(String filePath) throws FileNotFoundException {
        return BTree.loadTree(filePath).unpack();
    }

    /**
     * Load a serialized solution (of a BTree), convert it to a Floorplan,
     * and then add the net information.
     * @param filePath path to serialized file.
     * @param infoFileName block file name (like "n30.txt")
     * @return a Floorplan
     * @throws FileNotFoundException
     */
    public static Floorplan loadSolution(String filePath, String infoFileName) throws FileNotFoundException {
        Floorplan result = loadSolution(filePath);
        Floorplan fileFloorplan = getFloorplanFromFile(infoFileName, infoFileName.replaceFirst("n", "nets"));
        result.setNet(Objects.requireNonNull(fileFloorplan));
        return result;
    }

    /**
     * Load a Floorplan sequence from a progress folder, created after an experiment.
     * The progress folder contains solutions that show the progress of the algorithm.
     * @param progressFolder path to progress folder.
     * @return an array of Floorplans.
     */
    static Floorplan[] getProgress(String progressFolder) {
        File folder = new File(progressFolder);
        return Arrays.stream(Objects.requireNonNull(folder.listFiles()))
                .map(f -> Integer.parseInt(f.getName().replace(".txt", "")))
                .collect(Collectors.toList()).stream().sorted()
                .map(index -> {
                    try {
                        return loadSolution(progressFolder + "/" + index + ".txt");
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    return new Floorplan();
                }).toArray(Floorplan[]::new);
    }

}
