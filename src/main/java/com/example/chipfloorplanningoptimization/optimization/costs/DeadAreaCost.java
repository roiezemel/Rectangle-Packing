package com.example.chipfloorplanningoptimization.optimization.costs;

import com.example.chipfloorplanningoptimization.representation.Representation;

public class DeadAreaCost<T extends Representation<T>> implements Cost<T> {

    private double minArea = -1;
    private static final double A_BIT = 0.0001;

    /**
     * Initialize Cost function
     */
    public DeadAreaCost() {}

    /**
     * Initialize Cost function
     */
    public DeadAreaCost(T initialSolution) {
        prepareForOptimization(initialSolution);
    }

    /**
     * Happens before optimization starts
     * @param initialSolution
     */
    @Override
    public void prepareForOptimization(T initialSolution) {
        minArea = initialSolution.unpack().getModules().stream()
                .mapToDouble(module -> module.getWidth() * module.getHeight())
                .sum();
    }

    /**
     * Evaluate a single solution
     * @param r a solution
     * @return
     */
    @Override
    public double evaluate(T r) {
        return evalArea(r.unpack().area());
    }

    /**
     * Is the cost function prepared
     * @return
     */
    @Override
    public boolean isReady() {
        return minArea > -1;
    }

    /**
     * Evaluate by area
     * @param area
     * @return
     */
    private double evalArea(double area) {
        return area - minArea + A_BIT; // to avoid problems with division by zero
    }

    /**
     * Get minimum value possible
     * @return
     */
    @Override
    public double getMinimumPossible() {
        return evalArea(minArea);
    }

    /**
     * Get a description of parameters
     * @return
     */
    @Override
    public String paramsDescription() {
        return "=> Minimum area (sum of areas of all rectangles): " + minArea;
    }

    /**
     * Get the name of the Cost function
     * @return
     */
    @Override
    public String getName() {
        return "Dead-Area Cost";
    }
}
