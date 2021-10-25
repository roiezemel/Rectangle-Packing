package com.example.chipfloorplanningoptimization.representation;

import com.example.chipfloorplanningoptimization.abstract_structures.CModule;
import com.example.chipfloorplanningoptimization.abstract_structures.Point;

import java.util.LinkedList;
import java.util.List;

public class Floorplan {

    private Point min = new Point(1000000, 1000000); // (min(xs), min(ys))
    private Point max = new Point(-1, -1); // (max(xs), max(ys))
    private final List<CModule> modules;

    /**
     * Initialize floorplan
     */
    public Floorplan() {
        modules = new LinkedList<>();
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
    }

    /**
     * Add module to the floorplan.
     * Complexity: O(1)
     * @param module new module
     */
    public void addModule(CModule module) {
        Point p = module.getPosition();
        double xMax = max.x(), yMax = max.y(), xMin = min.x(), yMin = min.y();
        if (p.x() > xMax)
            xMax = p.x();
        else if (p.x() < xMin)
            xMin = p.x();
        if (p.y() > yMax)
            yMax = p.y();
        else if (p.y() < yMin)
            yMin = p.y();
        min = new Point(xMin, yMin);
        max = new Point(xMax, yMax);
        modules.add(module);
    }

    /**
     * Get the area of the floorplan (the area of the bounding rectangle)
     * Complexity: O(1)
     * @return area of the rectangle
     */
    public double area() {
        return (max.x() - min.x()) * (max.y() - min.y());
    }

    public List<CModule> getModules() {
        return modules;
    }

    // TODO: implement cost function
}
