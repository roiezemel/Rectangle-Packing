package com.example.chipfloorplanningoptimization.optimization.costs;

import com.example.chipfloorplanningoptimization.representation.Floorplan;
import com.example.chipfloorplanningoptimization.representation.Representation;

public class SquareCost<T extends Representation<T>> extends NormCost<T> {

    private double Snorm;

    /**
     * @param alpha           "controls the weight between area and wirelength"
     * @param perturbations   number of perturbations for the calculation of
     *                        Anorm and Wnorm (maybe proportional to the problem size)
     */
    public SquareCost(double alpha, int perturbations) {
        super(alpha, perturbations);
    }

    /**
     * @param alpha           "controls the weight between area and wirelength"
     * @param perturbations   number of perturbations for the calculation of
     *                        Anorm and Wnorm (maybe proportional to the problem size)
     * @param initialSolution an initial solution to apply perturbations on (the solution will be changed)
     */
    public SquareCost(double alpha, int perturbations, T initialSolution) {
        super(alpha, perturbations);
        prepareForOptimization(initialSolution);
    }

    /**
     * Happens before optimization starts
     * @param initialSolution
     */
    @Override
    public void prepareForOptimization(T initialSolution) {
        super.prepareForOptimization(initialSolution);
        Snorm = 0;
        T solution = initialSolution.copy();
        for (int i = 0; i < perturbations; i++) {
            solution.perturb();
            Floorplan floorplan = solution.unpack();
            Snorm += calcCost(floorplan.getSidesRatio());
        }
        Snorm /= perturbations; // average square-cost
    }

    /**
     * Calculate cost by ratio (only one part of the cost function)
     * @param ratio ratio between sides of the rectangle
     * @return
     */
    private double calcCost(double ratio) {
        return 1 - ratio;
    }

    /**
     * Evaluate a single solution
     * @param r a solution
     * @return
     */
    @Override
    public double evaluate(T r) {
        Floorplan floorplan = r.unpack();
        return evalByAreaAndRatio(floorplan.area(), floorplan.getSidesRatio());
    }

    /**
     * Evaluate by area and ratio
     * @param area area
     * @param ratio ratio
     * @return
     */
    private double evalByAreaAndRatio(double area, double ratio) {
        return alpha * (area / Anorm) + (1 - alpha) * (calcCost(ratio) / Snorm);
    }

    /**
     * Get minimum value possible
     * @return
     */
    @Override
    public double getMinimumPossible() {
        return evalByAreaAndRatio(minArea, 1); // TODO: complete this
    }

    /**
     * Get a description of parameters
     * @return
     */
    @Override
    public String paramsDescription() {
        return "=> Cost weight (alpha): " + alpha + "\n=> Anorm: " + Anorm + "\n=> Snorm: " + Snorm;
    }

    /**
     * Get the name of the Cost function
     * @return
     */
    @Override
    public String getName() {
        return "Square Cost";
    }


}
