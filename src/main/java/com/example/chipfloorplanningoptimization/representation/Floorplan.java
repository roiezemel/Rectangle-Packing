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

    public void setNet(Floorplan other) {
        if (other.net != null)
            setNet(other.getNameNet());
    }

    public List<List<CModule>> getNet() {
        return net;
    }

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

    public List<CModule> getModules() {
        return modules;
    }

    public Queue<CModule> getModulesQueue() {
        return (LinkedList<CModule>) modules;
    }

    public Rectangle getBoundingBox() {
        return new Rectangle(min.x(), min.y(), max.x() - min.x(), max.y() - min.y());
    }
}
