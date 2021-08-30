package com.example.chipfloorplanningoptimization;

import javafx.beans.value.ChangeListener;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

public class CanvasPagesView {

    /*
     * This class takes care of managing the pages layout
     */

    private final Stage stage;
    private final Group root;
    private final Painter painter;
    private int index = 0;
    private final int numPages;
    private final Text headline;

    public CanvasPagesView(Painter painter, int numPages, Stage stage, Group root, String title) {
        this.painter = painter;
        this.numPages = numPages;
        this.stage = stage;
        this.root = root;
        headline = new Text(title);
        headline.setFill(Color.WHITE);
        headline.setFont(Font.font("Ariel", FontWeight.BOLD, 30));
        headline.setX(50);
        headline.setY(50);
        addComponents();
    }

    public void firstPage() {
        painter.draw(0);
    }

    private void addComponents() {
        Button next = new Button("Next");
        Button back = new Button("Back");

        locateButtons(next, back, 50);

        next.setOnAction(e -> {
            if (index < numPages - 1) {
                clear();
                painter.draw(++index);
            }
        });

        back.setOnAction(e -> {
            if (index > 0) {
                clear();
                painter.draw(--index);
            }
        });

        this.root.getChildren().add(next);
        this.root.getChildren().add(back);
        this.root.getChildren().add(headline);

        ChangeListener<Number> stageSizeListener
                = (observable, oldValue, newValue) -> {
            clear();
            painter.draw(index);
            locateButtons(next, back, 50);
        };
        stage.widthProperty().addListener(stageSizeListener);
        stage.heightProperty().addListener(stageSizeListener);
    }

    public double getWidth() {
        return stage.getScene().getWidth();
    }

    public double getHeight() {
        return stage.getScene().getHeight();
    }

    private void locateButtons(Button next, Button back, int padding) {
        next.setLayoutX(stage.getWidth() - 20 - padding - next.getWidth());
        next.setLayoutY(stage.getHeight() - 30 - padding);
        back.setLayoutX(padding);
        back.setLayoutY(stage.getHeight() - 30 - padding);
    }

    public void drawShape(Shape shape) {
        root.getChildren().add(shape);
    }

    private void clear() {
        root.getChildren().removeIf(s -> s instanceof Shape);
        root.getChildren().add(headline);
    }

}
