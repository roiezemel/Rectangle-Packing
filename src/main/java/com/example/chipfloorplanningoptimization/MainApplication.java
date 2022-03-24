package com.example.chipfloorplanningoptimization;

import com.example.chipfloorplanningoptimization.abstract_structures.CModule;
import com.example.chipfloorplanningoptimization.gui.IOManager;
import com.example.chipfloorplanningoptimization.optimization.Cost;
import com.example.chipfloorplanningoptimization.optimization.DeadAreaCost;
import com.example.chipfloorplanningoptimization.optimization.NormCost;
import com.example.chipfloorplanningoptimization.optimization.Experiment;
import com.example.chipfloorplanningoptimization.optimization.genetic_algorithm.GeneticAlgorithm;
import com.example.chipfloorplanningoptimization.optimization.genetic_algorithm.fitness_functions.ReciprocalFitness;
import com.example.chipfloorplanningoptimization.optimization.genetic_algorithm.fitness_functions.RegularFitness;
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

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main-view.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 800, 700, Color.SKYBLUE);
        stage.setTitle("Floor Planning GUI");
        stage.setScene(scene);

        Floorplan[] floorplans = Lab.lab();
        ((MainController)loader.getController()).start(stage, floorplans);
        stage.show();
    }

}

