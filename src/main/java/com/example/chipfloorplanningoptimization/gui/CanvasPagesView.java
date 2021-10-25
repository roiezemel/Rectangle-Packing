package com.example.chipfloorplanningoptimization.gui;

import com.example.chipfloorplanningoptimization.abstract_structures.CModule;
import com.example.chipfloorplanningoptimization.representation.Floorplan;
import javafx.beans.value.ChangeListener;
import javafx.scene.Group;
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

    public CanvasPagesView(Painter painter, int numPages, Stage stage, Group root) {
        this.painter = painter;
        this.numPages = numPages;
        this.stage = stage;
        this.root = root;
        headline = new Text();
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
        Button toggleWires = new Button("Toggle Wires");

        locateButtons(next, back, toggleWires, 50);

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

        toggleWires.setOnAction(e -> {
            clear();
            painter.onToggleWires();
            painter.draw(index);
        });

        this.root.getChildren().add(next);
        this.root.getChildren().add(back);
        this.root.getChildren().add(toggleWires);
        this.root.getChildren().add(headline);
        this.root.getChildren().add(scaleText);
        this.root.getChildren().add(boundingRect);

        ChangeListener<Number> stageSizeListener
                = (observable, oldValue, newValue) -> {
            boundingRect.setWidth(getWidth() - 20);
            boundingRect.setHeight(getHeight() - 110);
            clear();
            painter.draw(index);
            locateButtons(next, back, toggleWires, 50);
        };
        stage.widthProperty().addListener(stageSizeListener);
        stage.heightProperty().addListener(stageSizeListener);
    }

    public void setTitle(String title) {
        headline.setText(title);
    }

    public double getWidth() {
        return stage.getScene().getWidth();
    }

    public double getHeight() {
        return stage.getScene().getHeight();
    }

    private void locateButtons(Button next, Button back, Button toggleWires, int padding) {
        next.setLayoutX(stage.getWidth() - 20 - padding - next.getWidth());
        next.setLayoutY(stage.getHeight() - 30 - padding);
        back.setLayoutX(padding);
        back.setLayoutY(stage.getHeight() - 30 - padding);
        toggleWires.setLayoutX(stage.getWidth() / 2 - toggleWires.getWidth() / 2);
        toggleWires.setLayoutY(stage.getHeight() - 30 - padding);
    }

    public void drawShape(Shape shape) {
        root.getChildren().add(shape);
    }

    public void drawFloorplan(Floorplan floorplan) {
        List<Rectangle> blocks = new LinkedList<>();
        List<String> names = new LinkedList<>();
        for (CModule module : floorplan.getModules()) {
            Rectangle block = new Rectangle(
                    module.getPosition().x(),
                    module.getPosition().y(),
                    module.getWidth(),
                    module.getHeight());
            block.setFill(Color.DARKSLATEGRAY);
            block.setStroke(Color.SKYBLUE);
            blocks.add(block);
            names.add(module.getName());
        }
        drawFloorplan(blocks, names, true);
    }

    private double transformOrigin(double moduleY, double moduleHeight) {
        return boundingRect.getHeight() - 6 - moduleY - moduleHeight;
    }

    public void drawFloorplan(List<Rectangle> plan, List<String> names, boolean transformOrigin) {
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

        double averageHeight = 0;
        double scale = Math.min(width / xMax, height / yMax);
        for (Rectangle r : plan) {
            r.setX(r.getX() * scale);
            r.setY(r.getY() * scale);
            r.setWidth(r.getWidth() * scale);
            r.setHeight(r.getHeight() * scale);
            averageHeight += r.getHeight();
        }

        averageHeight /= plan.size();
        scaleText.setText("Scale: " + ((int)(scale * 100)) / 100.);

       for (int i = 0; i < plan.size(); i++) {
           Rectangle r = plan.get(i);
            if (transformOrigin)
                r.setY(transformOrigin(r.getY(), r.getHeight()));

            r.setX(r.getX() + x0);
            r.setY(r.getY() + y0);
            drawShape(r);

            Text nameTitle = new Text(r.getX() + r.getWidth() / 2, r.getY() + r.getHeight() / 2, names.get(i));
            double fontSize = averageHeight / 10;
            nameTitle.setFont(Font.font("Ariel", FontWeight.NORMAL, fontSize));
            nameTitle.setFill(Color.WHITE);
            drawShape(nameTitle);
        }
    }

    private void clear() {
        root.getChildren().removeIf(s -> s instanceof Shape);
        root.getChildren().add(scaleText);
        root.getChildren().add(headline);
        root.getChildren().add(boundingRect);
    }

}
