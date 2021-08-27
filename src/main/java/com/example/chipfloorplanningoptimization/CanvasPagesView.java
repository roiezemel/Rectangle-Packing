package com.example.chipfloorplanningoptimization;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

public class CanvasPagesView {

    private Scene scene;
    private Group root;
    private List<Shape> visibleShapes = new LinkedList<>();
    private Painter painter;
    private int index = 0;
    private int numPages;
    String title;

    public CanvasPagesView(Painter painter, int numPages, Scene scene, Group root, String title) {
        this.painter = painter;
        this.numPages = numPages;
        this.scene = scene;
        this.root = root;
        this.title = title;
        addComponents();
    }

    public void firstPage() {
        painter.draw(0);
    }

    public Shape shapeAt(int index) {
        return visibleShapes.get(index);
    }

    private void addComponents() {
        Button next = new Button("Next");
        Button back = new Button("Back");

        int padding = 50;
        next.setLayoutX(scene.getWidth() - 30 -  padding - next.getWidth());
        next.setLayoutY(650);

        back.setLayoutX(padding);
        back.setLayoutY(650);

        next.setOnAction(e -> {
            if (index < numPages - 1) {
                painter.draw(++index);
            }
        });

        back.setOnAction(e -> {
            if (index > 0) {
                painter.draw(--index);
            }
        });

        Text headline = new Text(title);
        headline.setFill(Color.WHITE);
        headline.setFont(Font.font("Ariel", FontWeight.BOLD, 30));
        headline.setX(50);
        headline.setY(50);

        this.root.getChildren().add(next);
        this.root.getChildren().add(back);
        this.root.getChildren().add(headline);
    }

    private void clearShapes() {
        for (Shape shape : visibleShapes)
            shape.setVisible(false);
        visibleShapes.clear();
    }

    private void addToRoot(Shape shape) {
        if (!root.getChildren().contains(shape))
            root.getChildren().add(shape);
    }

    /**
     * Returns a list of shapes.
     * If a shape that has the same type as the type given to the supplier (as argument)
     * appears in the shapes list, this shape will be added instead of a new object.
     * @param suppliers array of suppliers. Each should be set to the supplier of the constructor of the desired shape
     */
    @SafeVarargs
    public final void loadShapes(Supplier<? extends Shape>... suppliers) {
        List<Shape> shapes = new LinkedList<>();
        for (Supplier<? extends Shape> supplier : suppliers) {
            Shape shape = supplier.get();
            for (Shape s : visibleShapes) {
                if (s.getClass().equals(shape.getClass())) {
                    shape = s; // Valid since shape's class equals to the desired class
                    visibleShapes.remove(s);
                    break;
                }
            }
            shapes.add(shape);
            addToRoot(shape);
        }
        clearShapes(); // clear left unneeded shapes
        visibleShapes.addAll(shapes); // add all new shapes
    }

}
