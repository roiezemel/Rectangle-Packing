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
        this.canvas = new CanvasPagesView(this, 6 + floorplans.length, stage, root);
        this.canvas.firstPage();
    }

    @Override
    public void draw(int index) { // Draw a page according to the current page index

        if (index == 1) {
            Floorplan f = BTree.unpack(BTree.packRandomly(new Floorplan(floorplans[1])));
            canvas.drawFloorplan(f);
            canvas.setTitle("Cost: " + f.area());
        }

        else if (index < floorplans.length) {
            canvas.drawFloorplan(floorplans[index]);
            canvas.setTitle("Cost: " + floorplans[index].area());
        }

        else {
            index -= floorplans.length;
            // this function currently only demonstrates the use of the draw method and the pages
            String[] blocks = {"n10.txt", "n30.txt", "n50.txt", "n100.txt", "n200.txt", "n300.txt"};
            String[] nets = {"nets10.txt", "nets30.txt", "nets50.txt", "nets100.txt", "nets200.txt", "nets300.txt"};
            showBlocks(blocks[index], nets[index]);
            canvas.setTitle(blocks[index]);
        }

    }

    @Override
    public void onToggleWires() {
        wiresVisible = !wiresVisible;
    }

    private void showBlocks(String blocksFile, String netsFile) {
        // This function isn't important since it only shows the blocks in a nice grid.
        // In the future we will only use the gui to watch the algorithms' results,
        // therefore this function will become redundant
        try {
            Map<String, Rectangle> blocks = IOManager.extractBlocks(new File(Objects.requireNonNull(getClass().getResource(blocksFile)).getFile()));

            // The next few lines just organize the blocks from the input file into a convenient grid view
            int xMax = 0;
            int yMax = 0;
            double heightMax = 0;
            for (String blockName : blocks.keySet()) {
                Rectangle block = blocks.get(blockName);

                if (xMax > 800) { // xMax + block.getWidth() >= canvas.getWidth()) {
                    xMax = 0;
                    yMax += heightMax;
                    heightMax = 0;
                }

                block.setX(block.getX() + xMax);
                block.setY(block.getY() + yMax);
                block.setFill(Color.DARKSLATEGRAY);
                block.setStroke(Color.SKYBLUE);

                xMax += block.getWidth();

                if (block.getHeight() > heightMax)
                    heightMax = block.getHeight();
            }

            canvas.drawFloorplan(blocks.values().stream().toList(), blocks.keySet().stream().toList(), false);

            drawWires(blocks, netsFile);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void drawWires(Map<String, Rectangle> blocks, String netsFile) throws FileNotFoundException {
        if (wiresVisible) {
            List<List<String>> nets = IOManager.parseNet(new File(Objects.requireNonNull(getClass()
                    .getResource(netsFile)).getFile()));

            for (List<String> net : nets) {
                for (int i = 0; i < net.size() - 1; i++) {
                    drawLine(blocks.get(net.get(i)), blocks.get(net.get(i + 1)));
                }
            }
        }
    }

    private void drawLine(Rectangle r1, Rectangle r2) {
        double x1 = r1.getX() + r1.getWidth() / 2,
            y1 = r1.getY() + r1.getHeight() / 2,
            x2 = r2.getX() + r2.getWidth() / 2,
            y2 = r2.getY() + r2.getHeight() / 2;
        Line line = new Line(x1, y1, x2, y2);
        line.setStrokeWidth(2);
        line.setOpacity(0.2);
        canvas.drawShape(line);
    }

}