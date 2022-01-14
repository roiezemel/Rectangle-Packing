package com.example.chipfloorplanningoptimization;

import com.example.chipfloorplanningoptimization.abstract_structures.CModule;
import com.example.chipfloorplanningoptimization.gui.DataVisualizer;
import com.example.chipfloorplanningoptimization.gui.IOManager;
import com.example.chipfloorplanningoptimization.optimization.Cost;
import com.example.chipfloorplanningoptimization.optimization.OptimizationLogger;
import com.example.chipfloorplanningoptimization.optimization.Optimizer;
import com.example.chipfloorplanningoptimization.optimization.SimulatedAnnealing;
import com.example.chipfloorplanningoptimization.representation.BNode;
import com.example.chipfloorplanningoptimization.representation.BTree;
import com.example.chipfloorplanningoptimization.representation.Floorplan;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class MainApplication extends Application {

    public static void main(String[] args) {
        launch(args);
    }

//    private Floorplan[] createFloorplans() {
//        CModule m1 = new CModule(9, 6, "1");
//        CModule m2 = new CModule(6, 8, "2");
//        CModule m3 = new CModule(3, 6, "3");
//        CModule m4 = new CModule(3, 7, "4");
//        CModule m5 = new CModule(6, 5, "5");
//        CModule m6 = new CModule(12, 2, "6");
//
//        BNode<CModule> n1 = new BNode<>(m1, null);
//        BNode<CModule> n2 = new BNode<>(m2, n1);
//        BNode<CModule> n3 = new BNode<>(m3, n1);
//        BNode<CModule> n4 = new BNode<>(m4, n3);
//        BNode<CModule> n5 = new BNode<>(m5, n4);
//        BNode<CModule> n6 = new BNode<>(m6, n3);
//
//        n1.setLeft(n2);
//        n1.setRight(n3);
//
//        n3.setLeft(n4);
//        n3.setRight(n6);
//
//        n4.setLeft(n5);
//
//        List<BNode<CModule>> nodes = new LinkedList<>() {{ add(n1); add(n2); add(n3); add(n4); add(n5); add(n6); }};
//       BTree tree = new BTree(n1, nodes);
//
//       tree.perturb();
//
//        Floorplan[] floorplans = new Floorplan[8];
//        floorplans[0] = tree.unpack();
//
//        Cost cost =  new Cost(1, 50, tree);
//        Optimizer op = new SimulatedAnnealing(1000, 0.999, 0.000001, 0.95, cost);
//        BTree optimizedTree = op.optimize(tree);
//        floorplans[1] = optimizedTree.unpack();
//
//        String[] blocks = {"n10.txt", "n30.txt", "n50.txt", "n100.txt", "n200.txt", "n300.txt"};
//        String[] nets = {"nets10.txt", "nets30.txt", "nets50.txt", "nets100.txt", "nets200.txt", "nets300.txt"};
//
//        for (int i = 0; i < blocks.length; i++) {
//            Floorplan floorplan = getFloorplanFromFile(blocks[i], nets[i]);
//            Floorplan p = BTree.packFloorplan(floorplan).unpack();
//            p.setNet(Objects.requireNonNull(floorplan));
//            floorplans[i + 2] = p;
//        }
//       return floorplans;
//    }

    public Floorplan[] createFloorplans() throws IOException {
        Floorplan fileFloorplan = getFloorplanFromFile("n30.txt", "nets30.txt");
        BTree original = BTree.packFloorplan(fileFloorplan);

        Floorplan originalFloorplan = original.unpack();
        originalFloorplan.setNet(Objects.requireNonNull(fileFloorplan));

        // optimize
        Cost cost =  new Cost(1, 50, original);
        Optimizer op = new SimulatedAnnealing(100, 0.99,
                0.0001, 0.9, cost);

        String exFolder = exDirectoryPath();
        OptimizationLogger costLogger = new OptimizationLogger(exFolder,
                "Temperature_Average Cost",
                (values) -> values[0] + "," + values[1]);
        OptimizationLogger rejectLogger = new OptimizationLogger(exFolder,
                "Temperature_Rejections",
                (values) -> values[0] + "," + values[1]);

        BTree optimized = op.optimize(original, costLogger, rejectLogger);
        Floorplan optimizedFloorplan = optimized.unpack();
        optimizedFloorplan.setNet(fileFloorplan);

        costLogger.close();
        rejectLogger.close();

        try {
            optimized.save(exFolder + "/result.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }

        createCharts(exFolder);

        return new Floorplan[] {originalFloorplan, optimizedFloorplan};
    }

    private void createCharts(String exFolder) {
        DataVisualizer dv = new DataVisualizer(exFolder);
        dv.addFromCSV("Temperature_Average Cost");
        dv.addFromCSV("Temperature_Rejections");

        try {
            dv.saveCharts();
        } catch (IOException e) {
            e.printStackTrace();
        }
        dv.displayCharts(true);
    }

    private int toExNumber(String fileName) {
        return Integer.parseInt(fileName.replace("ex #", ""));
    }

    private String exDirectoryPath() throws IOException {
        File file = new File("src/main/data");
        String[] directories = file.list((current, name) -> new File(current, name).isDirectory());
        int exNumber = 0;
        if (directories != null && directories.length > 0) {
            String lastEx = Arrays.stream(directories).max(Comparator.comparingInt((this::toExNumber))).get();
            exNumber = toExNumber(lastEx) + 1;
        }
        String path = "src/main/data/ex #" + exNumber;
        Files.createDirectories(Paths.get(path));
        return path;
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

