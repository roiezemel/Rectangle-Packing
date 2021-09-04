package com.example.chipfloorplanningoptimization;

import javafx.beans.value.ChangeListener;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
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
    private final Text scaleText;
    private final Text headline;
    private final Rectangle boundingRect;

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

        scaleText = new Text("Scale: 1.0");
        scaleText.setFill(Color.WHITE);
        scaleText.setFont(Font.font("Ariel", FontWeight.BOLD, 20));
        scaleText.setX(500);
        scaleText.setY(50);
        boundingRect = new Rectangle(10, 60, getWidth() - 20, getHeight() - 110);
        boundingRect.setFill(stage.getScene().getFill());
        boundingRect.setStroke(Paint.valueOf("black"));
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
        this.root.getChildren().add(scaleText);
        this.root.getChildren().add(boundingRect);

        ChangeListener<Number> stageSizeListener
                = (observable, oldValue, newValue) -> {
            boundingRect.setWidth(getWidth() - 20);
            boundingRect.setHeight(getHeight() - 110);
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

    public void drawFloorPlan(List<Rectangle> plan) {
        double x0 = boundingRect.getX() + 3, y0 = boundingRect.getY() + 3,
                width = boundingRect.getWidth() - 6, height = boundingRect.getHeight() - 6;

        double xMax = -1, yMax = -1;
        for (Rectangle rect : plan) {
            double x = rect.getX() + rect.getWidth(), y = rect.getY() + rect.getHeight();
            if (x > xMax)
                xMax = x;
            if (y > yMax)
                yMax = y;
        }

        double scale = Math.min(width / xMax, height / yMax);
        for (Rectangle r : plan) {
            r.setX(r.getX() * scale);
            r.setY(r.getY() * scale);
            r.setWidth(r.getWidth() * scale);
            r.setHeight(r.getHeight() * scale);
        }

        scaleText.setText("Scale: " + ((int)(scale * 100)) / 100.);

        plan.forEach(r -> {
            r.setX(r.getX() + x0);
            r.setY(r.getY() + y0);
            drawShape(r);
        });
    }

    private void clear() {
        root.getChildren().removeIf(s -> s instanceof Shape);
        root.getChildren().add(scaleText);
        root.getChildren().add(headline);
        root.getChildren().add(boundingRect);
    }

}
