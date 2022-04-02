package com.example.chipfloorplanningoptimization.optimization.costs;

import com.example.chipfloorplanningoptimization.representation.Floorplan;
import com.example.chipfloorplanningoptimization.representation.Representation;

public class NormCost<T extends Representation<T>> implements Cost<T> {


    protected double Anorm = -1;
    protected double Wnorm;
    protected final double alpha; // 0 < alpha < 1
    protected double minArea; // the sum of areas of all rectangles
    protected final int perturbations;

    /**
     * @param alpha "controls the weight between area and wirelength"
     * @param perturbations number of perturbations for the calculation of
     *                      Anorm and Wnorm (maybe proportional to the problem size)
     */
    public NormCost(double alpha, int perturbations) {
        if (alpha > 1 || alpha < 0)
            throw new IllegalArgumentException("0 < alpha < 1!");
        this.alpha = alpha;
        this.perturbations = perturbations;
    }

    /**
     * @param alpha "controls the weight between area and wirelength"
     * @param perturbations number of perturbations for the calculation of
     *                      Anorm and Wnorm (maybe proportional to the problem size)
     * @param initialSolution an initial solution to apply perturbations on (the solution will be changed)
     */
    public NormCost(double alpha, int perturbations, T initialSolution) {
        this(alpha, perturbations);
        prepareForOptimization(initialSolution);
    }

    @Override
    public void prepareForOptimization(T initialSolution) {
        // calculate Anorm and Wnorm
        Anorm = 0;
        Wnorm = 0;
        T solution = initialSolution.copy();
        for (int i = 0; i < perturbations; i++) {
            solution.perturb();
            Floorplan floorplan = solution.unpack();
            Anorm += floorplan.area();
            Wnorm += floorplan.totalWireLength();
        }
        Anorm /= perturbations; // average area
        Wnorm /= perturbations; // average wirelength
        if (Wnorm == 0)
            Wnorm = 1;
        minArea = initialSolution.unpack().getModules().stream()
                .mapToDouble(module -> module.getWidth() * module.getHeight())
                .sum();
    }

    /**
     * Evaluate the cost of a solution.
     * @param r - a solution
     * @return cost
     */
    @Override
    public double evaluate(T r) {
        Floorplan floorplan = r.unpack();
        return evalByAreaAndWireLength(floorplan.area(), floorplan.totalWireLength());
    }

    @Override
    public boolean isReady() {
        return Anorm > -1;
    }

    private double evalByAreaAndWireLength(double area, double wireLength) {
        return alpha * (area / Anorm) + (1 - alpha) * (wireLength / Wnorm);
    }

    public double getAlpha() {
        return alpha;
    }

    @Override
    public double getMinimumPossible() {
        return evalByAreaAndWireLength(minArea, 0); // TODO: complete this
    }

    @Override
    public String paramsDescription() {
        return "=> Cost weight (alpha): " + alpha + "\n=> Anorm: " + Anorm + "\n=> Wnorm: " + Wnorm;
    }

    @Override
    public String getName() {
        return "Norm Cost";
    }

}
