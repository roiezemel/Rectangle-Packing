package com.example.chipfloorplanningoptimization.abstract_structures;

/**
 * Chip-Module
 * Represents a module on a chip
 */
public class CModule {

    private Point position;
    private double width, height;
    private String name = "";

    public CModule() {}

    public CModule(CModule c) {
        this.position = c.position;
        this.width = c.width;
        this.height = c.height;
        this.name = c.name;
    }

    public CModule(double width, double height, String name) {
        this.width = width;
        this.height = height;
        this.name = name;
        this.position = new Point(0, 0);
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public String getName() {
        return name;
    }
}
