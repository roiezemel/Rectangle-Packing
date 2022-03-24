package com.example.chipfloorplanningoptimization.optimization;

import com.example.chipfloorplanningoptimization.representation.Floorplan;
import com.example.chipfloorplanningoptimization.representation.Representation;

import java.util.Random;

public class NormCost<T extends Representation<T>> implements Cost<T> {


    private double Anorm;
    private double Wnorm;
    private final double alpha; // 0 < alpha < 1
    private final double minArea; // the sum of areas of all rectangles

    /**
     * @param alpha "controls the weight between area and wirelength"
     * @param perturbations number of perturbations for the calculation of
     *                      Anorm and Wnorm (maybe proportional to the problem size)
     * @param initialSolution an initial solution to apply perturbations on (the solution will be changed)
     */
    public NormCost(double alpha, int perturbations, T initialSolution) {
        if (alpha > 1 || alpha < 0)
            throw new IllegalArgumentException("0 < alpha < 1!");
        this.alpha = alpha;

        // calculate Anorm and Wnorm
        Random random = new Random();
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
