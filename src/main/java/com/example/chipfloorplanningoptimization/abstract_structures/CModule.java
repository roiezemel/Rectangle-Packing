package com.example.chipfloorplanningoptimization.abstract_structures;

/**
 * Chip-Module
 * Represents a module on a chip
 */
public abstract class CModule {

    private final Point[] points = new Point[4];

    public CModule() {}

    public CModule(Point... points) {
        setPoints(points);
    }

    public Point[] getPoints() {
        return points;
    }

    public void setPoints(Point... points) {
        System.arraycopy(points, 0, this.points, 0, 4);
    }
}
