package com.example.chipfloorplanningoptimization;

import com.example.chipfloorplanningoptimization.abstract_structures.CModule;
import com.example.chipfloorplanningoptimization.gui.IOManager;
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
import java.util.Objects;

public class MainApplication extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    private Floorplan[] createFloorplans() {
        CModule m1 = new CModule(9, 6, "1");
        CModule m2 = new CModule(6, 8, "2");
        CModule m3 = new CModule(6, 3, "3");
        CModule m4 = new CModule(3, 7, "4");
        CModule m5 = new CModule(6, 5, "5");
        CModule m6 = new CModule(12, 2, "6");

        BNode<CModule> n1 = new BNode<>(m1);
        BNode<CModule> n2 = new BNode<>(m2);
        BNode<CModule> n3 = new BNode<>(m3);
        BNode<CModule> n4 = new BNode<>(m4);
        BNode<CModule> n5 = new BNode<>(m5);
        BNode<CModule> n6 = new BNode<>(m6);

        n2.setLeft(n1);
        n2.setRight(n3);

        n3.setLeft(n5);
        n3.setRight(n6);

        n6.setLeft(n4);

       BTree tree = new BTree(n2);

       Floorplan result = tree.unpack();

       Floorplan perturbed = BTree.unpack(BTree.packFloorplan(result));

       Floorplan[] floorplans = new Floorplan[8];

       floorplans[0] = result;
       floorplans[1] = perturbed;

        String[] blocks = {"n10.txt", "n30.txt", "n50.txt", "n100.txt", "n200.txt", "n300.txt"};
        String[] nets = {"nets10.txt", "nets30.txt", "nets50.txt", "nets100.txt", "nets200.txt", "nets300.txt"};

        for (int i = 0; i < blocks.length; i++) {
            Floorplan floorplan = getFloorplanFromFile(blocks[i], nets[i]);
            Floorplan p = BTree.unpack(BTree.packFloorplan(floorplan));
            p.setNet(Objects.requireNonNull(floorplan));
            floorplans[i + 2] = p;
        }
       return floorplans;
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