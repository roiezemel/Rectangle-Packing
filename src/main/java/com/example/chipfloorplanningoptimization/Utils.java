package com.example.chipfloorplanningoptimization;

import com.example.chipfloorplanningoptimization.abstract_structures.CModule;
import com.example.chipfloorplanningoptimization.gui.IOManager;
import com.example.chipfloorplanningoptimization.representation.BNode;
import com.example.chipfloorplanningoptimization.representation.BTree;
import com.example.chipfloorplanningoptimization.representation.Floorplan;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class Utils {

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
        BTree tree = new BTree(n2, nodes);

        tree.perturb();
        tree.perturb();
        tree.perturb();
        return tree;
    }

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

}
