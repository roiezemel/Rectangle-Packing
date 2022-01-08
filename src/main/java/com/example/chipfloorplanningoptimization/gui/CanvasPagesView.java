package com.example.chipfloorplanningoptimization.gui;

import com.example.chipfloorplanningoptimization.abstract_structures.CModule;
import com.example.chipfloorplanningoptimization.abstract_structures.Point;
import com.example.chipfloorplanningoptimization.representation.Floorplan;
import javafx.beans.value.ChangeListener;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.*;

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

    private double transformOrigin(double moduleY, double moduleHeight) {
        return boundingRect.getHeight() - 6 - moduleY - moduleHeight;
    }

    public void drawFloorplan(Floorplan floorplanIn, boolean drawWires) {
        Floorplan floorplan = new Floorplan(floorplanIn);
        double x0 = boundingRect.getX() + 3, y0 = boundingRect.getY() + 3,
                width = boundingRect.getWidth() - 6, height = boundingRect.getHeight() - 6;

        double xMax = -1, yMax = -1;
        for (CModule rect : floorplan.getModules()) {
            double x = rect.getPosition().x() + rect.getWidth(), y = rect.getPosition().y() + rect.getHeight();
            if (x > xMax)
                xMax = x;
            if (y > yMax)
                yMax = y;
        }

        double averageHeight = 0;
        double scale = Math.min(width / xMax, height / yMax);
        for (CModule r : floorplan.getModules()) {
            r.setPosition(new Point(r.getPosition().x() * scale, r.getPosition().y() * scale));
            r.setWidth(r.getWidth() * scale);
            r.setHeight(r.getHeight() * scale);
            averageHeight += r.getHeight();
        }

        averageHeight /= floorplan.getModules().size();
        scaleText.setText("Scale: " + ((int)(scale * 100)) / 100.);

       for (int i = 0; i < floorplan.getModules().size(); i++) {
            CModule r = floorplan.getModules().get(i);
            // Transform Origin:
            r.setPosition(new Point(r.getPosition().x(), transformOrigin(r.getPosition().y(), r.getHeight())));

            r.setPosition(new Point(r.getPosition().x() + x0, r.getPosition().y() + y0));
            Rectangle block = new Rectangle(r.getPosition().x(), r.getPosition().y(), r.getWidth(), r.getHeight());
            block.setFill(Color.DARKSLATEGRAY);
            block.setStroke(Color.SKYBLUE);
            drawShape(block);

            Text nameTitle = new Text(r.getPosition().x() + r.getWidth() / 2, r.getPosition().y() + r.getHeight() / 2, r.getName());
            double fontSize = averageHeight / 10;
            nameTitle.setFont(Font.font("Ariel", FontWeight.NORMAL, fontSize));
            nameTitle.setFill(Color.WHITE);
            drawShape(nameTitle);
        }

        if (drawWires && floorplan.getNet() != null)
           for (List<CModule> connectedModules : floorplan.getNet())
               for (int i = 0; i < connectedModules.size() - 1; i++)
                   drawLine(connectedModules.get(i), connectedModules.get(i + 1));
       Rectangle boundingRect = getBoundingRect(floorplan);
       boundingRect.setFill(Color.TRANSPARENT);
       boundingRect.setStroke(Color.GRAY);
        drawShape(boundingRect);
    }

    private Rectangle getBoundingRect(Floorplan floorplan) {
        double minX = floorplan.getModules().stream().min(Comparator.comparingDouble(m -> m.getPosition().x())).get().getPosition().x();
        double minY = floorplan.getModules().stream().min(Comparator.comparingDouble(m -> m.getPosition().y())).get().getPosition().y();


        CModule right = floorplan.getModules().stream().max(Comparator.comparingDouble(m -> m.getPosition().x() + m.getWidth())).get();
        CModule bottom = floorplan.getModules().stream().max(Comparator.comparingDouble(m -> m.getPosition().y() + m.getHeight())).get();
        double maxX = right.getPosition().x() + right.getWidth();
        double maxY = bottom.getPosition().y() + bottom.getHeight();
        return new Rectangle(minX, minY, maxX - minX, maxY - minY);
    }

    private void drawLine(CModule r1, CModule r2) {
        double x1 = r1.getPosition().x() + r1.getWidth() / 2,
                y1 = r1.getPosition().y() + r1.getHeight() / 2,
                x2 = r2.getPosition().x() + r2.getWidth() / 2,
                y2 = r2.getPosition().y() + r2.getHeight() / 2;
        Line line = new Line(x1, y1, x2, y2);
        line.setStrokeWidth(2);
        line.setOpacity(0.2);
        drawShape(line);
    }

    private void clear() {
        root.getChildren().removeIf(s -> s instanceof Shape);
        root.getChildren().add(scaleText);
        root.getChildren().add(headline);
        root.getChildren().add(boundingRect);
    }

}
