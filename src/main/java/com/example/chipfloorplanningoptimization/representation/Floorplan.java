package com.example.chipfloorplanningoptimization.representation;

import com.example.chipfloorplanningoptimization.abstract_structures.CModule;
import com.example.chipfloorplanningoptimization.abstract_structures.Point;

import java.util.LinkedList;
import java.util.List;

public class Floorplan {

    private Point min; // (min(xs), min(ys))
    private Point max; // (max(xs), max(ys))
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

    /**
     * Add module to the floorplan.
     * Complexity: O(1)
     * @param module new module
     */
    public void addModule(CModule module) {
        // The loop may not be required, if the
        // first point of each module is set
        // to be the bottom-left point
        for (int i = 0; i < 4; i++) {
            Point p = module.getPoints()[i];
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
        }

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

    // TODO: implement cost function
}
