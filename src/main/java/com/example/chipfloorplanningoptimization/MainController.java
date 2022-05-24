package com.example.chipfloorplanningoptimization;
import com.example.chipfloorplanningoptimization.gui.CanvasPagesView;
import com.example.chipfloorplanningoptimization.gui.Painter;
import com.example.chipfloorplanningoptimization.representation.Floorplan;
import javafx.scene.Group;
import javafx.stage.Stage;
import java.text.DecimalFormat;

public class MainController implements Painter {

    private CanvasPagesView canvas;
    private boolean wiresVisible = false;
    private Floorplan[] floorplans;

    /**
     * Start the GUI.
     * @param stage JavaFX stage.
     * @param floorplans array of floorplans to display.
     */
    public void start(Stage stage, Floorplan... floorplans) { // This is called just before the window shows up
        Group root = new Group();
        stage.getScene().setRoot(root);
        this.floorplans = floorplans;
        this.canvas = new CanvasPagesView(this, floorplans.length, stage, root);
        this.canvas.firstPage();
    }

    /**
     * Draw a single page.
     * @param index index of the page.
     */
    @Override
    public void draw(int index) { // Draw a page according to the current page index

        canvas.drawFloorplan(floorplans[index], wiresVisible);
        canvas.setTitle("#" + (index + 1)
                + "   Area: " + new DecimalFormat("#,###").format(floorplans[index].area())
                + "   " +
                floorplans[index].getName());
    }

    /**
     * When Toggle Wires button is pressed.
     */
    @Override
    public void onToggleWires() {
        wiresVisible = !wiresVisible;
    }

    /**
     * When Next button is pressed.
     */
    public void next() {
        canvas.getNext().fire();
    }

    /**
     * When Back Button is pressed.
     */
    public void back() {
        canvas.getBack().fire();
    }

}