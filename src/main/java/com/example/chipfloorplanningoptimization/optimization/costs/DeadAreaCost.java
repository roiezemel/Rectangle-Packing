package com.example.chipfloorplanningoptimization.optimization.costs;

import com.example.chipfloorplanningoptimization.representation.Representation;

public class DeadAreaCost<T extends Representation<T>> implements Cost<T> {

    private double minArea = -1;
    private static final double A_BIT = 0.0001;

    public DeadAreaCost() {}

    public DeadAreaCost(T initialSolution) {
        prepareForOptimization(initialSolution);
    }

    @Override
    public void prepareForOptimization(T initialSolution) {
        minArea = initialSolution.unpack().getModules().stream()
                .mapToDouble(module -> module.getWidth() * module.getHeight())
                .sum();
    }

    @Override
    public double evaluate(T r) {
        return evalArea(r.unpack().area());
    }

    @Override
    public boolean isReady() {
        return minArea > -1;
    }

    private double evalArea(double area) {
        return area - minArea + A_BIT; // to avoid problems with division by zero
    }

    @Override
    public double getMinimumPossible() {
        return evalArea(minArea);
    }

    @Override
    public String paramsDescription() {
        return "=> Minimum area (sum of areas of all rectangles): " + minArea;
    }

    @Override
    public String getName() {
        return "Dead-Area Cost";
    }
}
