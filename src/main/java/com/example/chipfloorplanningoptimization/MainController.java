package com.example.chipfloorplanningoptimization;

import com.example.chipfloorplanningoptimization.abstract_structures.CModule;
import com.example.chipfloorplanningoptimization.gui.CanvasPagesView;
import com.example.chipfloorplanningoptimization.gui.IOManager;
import com.example.chipfloorplanningoptimization.gui.Painter;
import com.example.chipfloorplanningoptimization.representation.BTree;
import com.example.chipfloorplanningoptimization.representation.Floorplan;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MainController implements Painter {

//    private Stage stage;
//    private Scene scene;
//    private Group root;
    private CanvasPagesView canvas;
    private boolean wiresVisible = false;
    private Floorplan[] floorplans;

    public void start(Stage stage, Floorplan... floorplans) { // This is called just before the window shows up
        Group root = new Group();
        stage.getScene().setRoot(root);
        this.floorplans = floorplans;
        this.canvas = new CanvasPagesView(this, floorplans.length, stage, root);
        this.canvas.firstPage();
    }

    @Override
    public void draw(int index) { // Draw a page according to the current page index

        canvas.drawFloorplan(floorplans[index], wiresVisible);
        canvas.setTitle("Area: " + new DecimalFormat("#,###").format(floorplans[index].area()));

    }

    @Override
    public void onToggleWires() {
        wiresVisible = !wiresVisible;
    }



}