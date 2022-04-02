package com.example.chipfloorplanningoptimization;

import com.example.chipfloorplanningoptimization.abstract_structures.CModule;
import com.example.chipfloorplanningoptimization.gui.CanvasPagesView;
import com.example.chipfloorplanningoptimization.gui.GifSequenceWriter;
import com.example.chipfloorplanningoptimization.gui.IOManager;
import com.example.chipfloorplanningoptimization.gui.Painter;
import com.example.chipfloorplanningoptimization.representation.BTree;
import com.example.chipfloorplanningoptimization.representation.Floorplan;
import javafx.concurrent.Task;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.awt.image.RenderedImage;
import java.util.stream.IntStream;


public class MainController implements Painter {

//    private Stage stage;
//    private Scene scene;
//    private Group root;
    private CanvasPagesView canvas;
    private boolean wiresVisible = false;
    private Floorplan[] floorplans;
    private java.awt.Rectangle rect;
    private List<BufferedImage> images;

    public void start(Stage stage, Floorplan... floorplans) { // This is called just before the window shows up
        Group root = new Group();
        stage.getScene().setRoot(root);
        this.floorplans = floorplans;
        this.canvas = new CanvasPagesView(this, floorplans.length, stage, root);
        this.canvas.firstPage();

        this.images = new LinkedList<>();
        stage.setOnShown(event -> {
            rect = new Rectangle((int)stage.getX() + 13, (int)stage.getY() + 50,
                    (int)stage.getWidth() - 26, (int)stage.getHeight() - 105);
            shoot();
        });
        canvas.getStage().setAlwaysOnTop(true);
//        canvas.getStage().setAlwaysOnTop(false);
    }

    private void createGIF() throws IOException {
        IntStream.range(0, 2).forEach(i -> images.add(images.get(images.size() - 1)));
        BufferedImage first = images.get(0);
        ImageOutputStream output = new FileImageOutputStream(new File(floorplans[0].getSaveTo() + "/animation.gif"));

        GifSequenceWriter writer = new GifSequenceWriter(output, first.getType(), 250, true);
        writer.writeToSequence(first);

        for (BufferedImage bufferedImage : images) {
            writer.writeToSequence(bufferedImage);
        }

        writer.close();
        output.close();
    }

    private void shoot() {
        delay(1500, () -> {
            back();
            goNext(0, floorplans.length, () -> {
                try {
                    createGIF();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        });
    }

    private void goNext(int i, int times, Runnable finalRun) {
        delay(300, () -> {
            next();
            if (i < times - 1)
                goNext(i + 1, times, finalRun);
            else
                finalRun.run();
        });
    }

    @Override
    public void draw(int index) { // Draw a page according to the current page index

        canvas.drawFloorplan(floorplans[index], wiresVisible);
        canvas.setTitle("#" + (index + 1)
                + "   Area: " + new DecimalFormat("#,###").format(floorplans[index].area())
                + "   " +
                floorplans[index].getName());

        if (rect != null)
            takeScreenshot(rect, index);
    }

    @Override
    public void onToggleWires() {
        wiresVisible = !wiresVisible;
    }

    public void next() {
        canvas.getNext().fire();
    }

    public void back() {
        canvas.getBack().fire();
    }

    private void takeScreenshot(java.awt.Rectangle rect, int index) {
        if (floorplans[index].getSaveTo() == null)
            return;
        new Thread(() -> {
            try {
                Thread.sleep(100);
                BufferedImage image = new Robot().createScreenCapture(rect);
                images.add(image);
//                ImageIO.write(image, "png", new File(floorplans[index].getSaveTo()));
            } catch (AWTException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void delay(long millis, Runnable continuation) {
        Task<Void> sleeper = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try { Thread.sleep(millis); }
                catch (InterruptedException ignored) { }
                return null;
            }
        };
        sleeper.setOnSucceeded(event -> continuation.run());
        new Thread(sleeper).start();
    }

}