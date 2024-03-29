package com.example.chipfloorplanningoptimization.representation;

import com.example.chipfloorplanningoptimization.abstract_structures.CModule;
import com.example.chipfloorplanningoptimization.abstract_structures.Point;
import javafx.scene.shape.Rectangle;

import java.util.*;

public class Floorplan {

    private Point min = new Point(1000000, 1000000); // (min(xs), min(ys))
    private Point max = new Point(-1, -1); // (max(xs), max(ys))
    private final List<CModule> modules;
    private final Map<String, CModule> modulesByNames;
    private List<List<CModule>> net;
    private String saveTo = null;
    private String name = "";

    /**
     * Initialize floorplan
     */
    public Floorplan() {
        modules = new LinkedList<>();
        modulesByNames = new HashMap<>();
    }

    /**
     * Initialize floorplan
     * @param modules list of modules
     */
    public Floorplan(List<CModule> modules) {
        this();
        modules.forEach(this::addModule);
    }

    /**
     * Initialize floorplan with another floorplan (deep copy)
     * @param f another floorplan
     */
    public Floorplan(Floorplan f) {
        this();
        f.getModules().forEach(m -> addModule(new CModule(m)));
        setNet(f);
    }

    /**
     * Add module to the floorplan.
     * Complexity: O(1)
     * @param module new module
     */
    public void addModule(CModule module) {
        Point p = module.getPosition();
        double xMax = max.x(), yMax = max.y(), xMin = min.x(), yMin = min.y();
        if (p.x() + module.getWidth() > xMax)
            xMax = p.x() + module.getWidth();
        if (p.x() < xMin)
            xMin = p.x();
        if (p.y() + module.getHeight() > yMax)
            yMax = p.y() + module.getHeight();
        if (p.y() < yMin)
            yMin = p.y();
        min = new Point(xMin, yMin);
        max = new Point(xMax, yMax);
        modules.add(module);
        modulesByNames.put(module.getName(), module);
    }

    /**
     * Set the net of the floorplan
     * @param net list containing lists of names of connected modules
     */
    public void setNet(List<List<String>> net) {
        if (net == null)
            return;

        this.net = new LinkedList<>();
        net.forEach(names -> {
            List<CModule> connectedModules = new LinkedList<>();
            names.forEach(name -> connectedModules.add(modulesByNames.get(name)));
            this.net.add(connectedModules);
        });
    }

    /**
     * Set the net according to another floorplan
     * @param other another floorplan.
     */
    public void setNet(Floorplan other) {
        if (other.net != null)
            setNet(other.getNameNet());
    }

    /**
     * Get the net information.
     * @return list containing lists of names of connected modules.
     */
    public List<List<CModule>> getNet() {
        return net;
    }

    /**
     * Get a list containing lists of names of connected modules.
     * @return list containing lists of names of connected modules.
     */
    public List<List<String>> getNameNet() {
        if (net == null)
           return null;
        List<List<String>> result = new LinkedList<>();
        net.forEach(connected -> result.add(new LinkedList<>() {{
            connected.forEach(module -> add(module.getName()));
        }}));
        return result;
    }

    /**
     * Get the area of the floorplan (the area of the bounding rectangle)
     * Complexity: O(1)
     * @return area of the rectangle
     */
    public double area() {
        return (max.x() - min.x()) * (max.y() - min.y());
    }

    /**
     * Get the total wire length between the modules.
     * @return total wire length (sum of distances of all connections).
     */
    public double totalWireLength() {
        if (net == null || net.isEmpty())
            return 0;
        double length = 0;
        for (List<CModule> connectedModules : net) {
            for (int i = 0; i < connectedModules.size() - 1; i++) {
                CModule module1 = connectedModules.get(i);
                CModule module2 = connectedModules.get(i + 1);

                Point pos1 = new Point(module1.getPosition().x() + module1.getWidth() / 2,
                        module1.getPosition().y() + module1.getHeight() / 2);
                Point pos2 = new Point(module2.getPosition().x() + module2.getWidth() / 2,
                        module2.getPosition().y() + module2.getHeight() / 2);
                length += Point.distance(pos1, pos2);
            }
        }
        return length;
    }

    /**
     * Get the ratio between the sides of the bounding rectangle.
     * The ratio is always smaller than 1.
     * @return ratio between the sides of the bounding rectangle.
     */
    public double getSidesRatio() {
        Rectangle rect = getBoundingBox();
        double ratio = rect.getWidth() / rect.getHeight();
        if (ratio < 1)
            return ratio;
        return 1 / ratio;
    }

    /**
     * Get list of modules.
     * @return list of CModules.
     */
    public List<CModule> getModules() {
        return modules;
    }

    /**
     * Get a queue of modules.
     * @return queue of CModules.
     */
    public Queue<CModule> getModulesQueue() {
        return (LinkedList<CModule>) modules;
    }

    /**
     * Get the bounding rectangle of the floorplan.
     * @return bounding rectangle.
     */
    public Rectangle getBoundingBox() {
        return new Rectangle(min.x(), min.y(), max.x() - min.x(), max.y() - min.y());
    }

    /**
     * Get the path to which the floorplan's image should be saved.
     * This is used when creating animations of the progress of the algorithms.
     * @return a path to a folder.
     */
    public String getSaveTo() {
        return saveTo;
    }

    /**
     * Set the path to which the floorplan's image should be saved.
     * This is used when creating animations of the progress of the algorithms.
     * @param saveTo a path to a folder.
     */
    public void setSaveTo(String saveTo) {
        this.saveTo = saveTo;
    }

    /**
     * Set Floorplan's name.
     * Used for the GUI.
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get Floorplan's name.
     * Used for the GUI.
     * @return
     */
    public String getName() {
        return name;
    }
}
