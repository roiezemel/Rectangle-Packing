package com.example.chipfloorplanningoptimization;

import com.example.chipfloorplanningoptimization.abstract_structures.CModule;
import com.example.chipfloorplanningoptimization.representation.BNode;
import com.example.chipfloorplanningoptimization.representation.BTree;
import com.example.chipfloorplanningoptimization.representation.Floorplan;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class MainApplication extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    private static Floorplan[] createFloorplans() {
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

       Floorplan perturbed = BTree.unpack(BTree.packRandomly(new Floorplan(result)));

       return new Floorplan[] {result, perturbed};
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