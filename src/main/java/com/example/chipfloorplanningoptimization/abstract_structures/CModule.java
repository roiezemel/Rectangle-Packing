package com.example.chipfloorplanningoptimization.abstract_structures;

/**
 * Chip-Module
 * Represents a module on a chip
 */
public class CModule {

    private Point position;
    private double width, height;
    private String name = "";

    /**
     * Initialize CModule
     */
    public CModule() {}

    /**
     * Initialize CModule
     * @param c another CModule
     */
    public CModule(CModule c) {
        this.position = c.position;
        this.width = c.width;
        this.height = c.height;
        this.name = c.name;
    }

    /**
     * Initialize CModule
     */
    public CModule(double width, double height, String name) {
        this.width = width;
        this.height = height;
        this.name = name;
        this.position = new Point(0, 0);
    }

    /**
     * Get module's position
     * @return module's position
     */
    public Point getPosition() {
        return position;
    }

    /**
     * Set module's position
     * @param position
     */
    public void setPosition(Point position) {
        this.position = position;
    }

    /**
     * Get module's width
     * @return
     */
    public double getWidth() {
        return width;
    }

    /**
     * Set module's width
     * @param width
     */
    public void setWidth(double width) {
        this.width = width;
    }

    /**
     * Get module's height
     * @return
     */
    public double getHeight() {
        return height;
    }

    /**
     * Set module's height
     * @param height
     */
    public void setHeight(double height) {
        this.height = height;
    }

    /**
     * Get module's name
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Serialize module
     * @return
     */
    public String serialize() {
        return "[" + name + "," + position.x() + "," + position.y() + "," + width + "," + height + "]";
    }

    /**
     * Deserialize module
     * @param t serialized string
     * @return
     */
    public static CModule deserialize(String t) {
        String[] data = t.substring(1, t.length() - 1).split(",");
        CModule module = new CModule(Double.parseDouble(data[3]), Double.parseDouble(data[4]), data[0]);
        module.setPosition(new Point(Double.parseDouble(data[1]), Double.parseDouble(data[2])));
        return module;
    }
}
