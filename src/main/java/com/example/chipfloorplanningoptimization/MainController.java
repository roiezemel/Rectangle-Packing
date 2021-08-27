package com.example.chipfloorplanningoptimization;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

public class MainController implements Painter {

    private Stage stage;
    private Scene scene;
    private Group root;
    private CanvasPagesView canvas;

    public void start(Stage stage) {
        this.stage = stage;
        this.scene = stage.getScene();
        this.root = new Group();
        this.scene.setRoot(this.root);
        this.canvas = new CanvasPagesView(this, 2, scene, root, "Chip Floor-Planning GUI");
        this.canvas.firstPage();
    }

    @Override
    public void draw(int index) {

        if (index == 0) {

            canvas.loadShapes(Rectangle::new);
            Rectangle rect = (Rectangle) canvas.shapeAt(0);
            rect.setX(100);
            rect.setY(100);
            rect.setWidth(100);
            rect.setHeight(100);
        }

        else {
            canvas.loadShapes(Rectangle::new, Circle::new);
            Rectangle rect = (Rectangle) canvas.shapeAt(0);
            rect.setX(200);
            rect.setY(100);
            rect.setWidth(100);
            rect.setHeight(100);

            Circle c = (Circle) canvas.shapeAt(1);
            c.setCenterX(300);
            c.setCenterY(300);
            c.setRadius(30);
        }

    }

}