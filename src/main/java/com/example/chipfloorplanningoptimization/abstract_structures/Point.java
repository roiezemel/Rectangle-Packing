package com.example.chipfloorplanningoptimization.abstract_structures;

public record Point(double x, double y) {

    /**
     * Calculate the distance between two points
     * @param a point 1
     * @param b point 2
     * @return distance
     */
    public static double distance(Point a, Point b) {
        return Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2));
    }

}
