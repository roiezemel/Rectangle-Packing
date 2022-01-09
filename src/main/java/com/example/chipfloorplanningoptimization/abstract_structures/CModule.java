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

    public String serialize() {
        return "[" + name + "," + position.x() + "," + position.y() + "," + width + "," + height + "]";
    }

    public static CModule deserialize(String t) {
        String[] data = t.substring(1, t.length() - 1).split(",");
        CModule module = new CModule(Double.parseDouble(data[3]), Double.parseDouble(data[4]), data[0]);
        module.setPosition(new Point(Double.parseDouble(data[1]), Double.parseDouble(data[2])));
        return module;
    }
}
