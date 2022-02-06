package com.example.chipfloorplanningoptimization;

import com.example.chipfloorplanningoptimization.abstract_structures.CModule;
import com.example.chipfloorplanningoptimization.gui.IOManager;
import com.example.chipfloorplanningoptimization.optimization.*;
import com.example.chipfloorplanningoptimization.optimization.cooling_schedule.ClimberSchedule;
import com.example.chipfloorplanningoptimization.optimization.cooling_schedule.FastSA;
import com.example.chipfloorplanningoptimization.optimization.cooling_schedule.WaveSchedule;
import com.example.chipfloorplanningoptimization.representation.BNode;
import com.example.chipfloorplanningoptimization.representation.BTree;
import com.example.chipfloorplanningoptimization.representation.Floorplan;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class MainApplication extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    private BTree createInitialSolution() {
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

    public Floorplan[] createFloorplans() throws IOException {
//        Floorplan fileFloorplan = getFloorplanFromFile("n50.txt", "nets50.txt");
        BTree original = createInitialSolution();
//        BTree original = BTree.loadTree("src/main/data/ex #34/n50-result.txt");

        Floorplan originalFloorplan = original.unpack();
//        originalFloorplan.setNet(Objects.requireNonNull(fileFloorplan));

        // optimize
        Cost cost =  new Cost(1, 100, original);

//        SimulatedAnnealing op = new SimulatedAnnealing(100000, 0.996,
//                0, 1.0, 200, cost);
//        op.setDefaultCoolingSchedule(0.85);
//        op.setCoolingSchedule(new WaveSchedule(0.01, 0.3, 0.9));

        Optimizer op = new DumbSearch(cost);
        Experiment ex = new Experiment();

        ex.setOptimizer(op);
        Floorplan optimizedFloorplan = ex.run(original, "custom").unpack();
//        optimizedFloorplan.setNet(fileFloorplan);

        return new Floorplan[] {originalFloorplan, optimizedFloorplan};
    }

    private Floorplan getFloorplanFromFile(String blocksFilePath, String netFilePath) {
        File netsFile = new File(Objects.requireNonNull(getClass()
                    .getResource(netFilePath)).getFile());
        try {
            return IOManager.extractBlocksToFloorplan(
                    new File(Objects.requireNonNull(getClass().getResource(blocksFilePath)).getFile()), netsFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main-view.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 800, 700, Color.SKYBLUE);
        stage.setTitle("Floor Planning GUI");
        stage.setScene(scene);

        Floorplan[] floorplans = createFloorplans();
        ((MainController)loader.getController()).start(stage, floorplans);
        stage.show();
    }

}

