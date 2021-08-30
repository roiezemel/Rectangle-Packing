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

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public class MainController implements Painter {

//    private Stage stage;
//    private Scene scene;
//    private Group root;
    private CanvasPagesView canvas;

    public void start(Stage stage) { // This is called just before the window shows up
        Group root = new Group();
        stage.getScene().setRoot(root);
        this.canvas = new CanvasPagesView(this, 2, stage, root, "Chip Floor-Planning GUI");
        this.canvas.firstPage();
    }

    @Override
    public void draw(int index) { // Draw a page according to the current page index
        // this function currently only demonstrates the use of the draw method and the pages
        if (index == 0) { // page 1
            showBlocks();
        }

        else { // page 2 - currently just a rectangle and a circle,
            // only for demonstration purpose
            Rectangle rect = new Rectangle(200, 100, 100, 100);
            canvas.drawShape(rect);
            Circle c = new Circle(300, 300, 30);
            canvas.drawShape(c);
        }

    }

    private void showBlocks() {
        // This function isn't important since it only shows the blocks in a nice grid.
        // In the future we will only use the gui to watch the algorithms' results,
        // therefore this function will become redundant
        try {
            Map<String, Rectangle> blocks = IOManager.extractBlocks(new File(Objects.requireNonNull(getClass().getResource("in.txt")).getFile()));

            // The next few lines just organize the blocks from the input file into a convenient grid view
            int xMax = 30;
            int yMax = 70;
            double heightMax = 0;
            for (String blockName : blocks.keySet()) {
                Rectangle block = blocks.get(blockName);

                if (xMax + block.getWidth() >= canvas.getWidth() - 5) {
                    xMax = 50;
                    yMax += heightMax + 5;
                    heightMax = 0;
                }

                block.setX(block.getX() + xMax);
                block.setY(block.getY() + yMax);

                xMax += block.getWidth() + 5;

                if (block.getHeight() > heightMax)
                    heightMax = block.getHeight();

                canvas.drawShape(block); // draw a shape

                Text nameTitle = new Text(block.getX() + block.getWidth() / 2, block.getY() + block.getHeight() / 2, blockName);
                nameTitle.setFill(Color.WHITE);
                canvas.drawShape(nameTitle);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}