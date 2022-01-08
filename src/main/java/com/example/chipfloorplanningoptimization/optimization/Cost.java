package com.example.chipfloorplanningoptimization.optimization;

import com.example.chipfloorplanningoptimization.representation.Floorplan;
import com.example.chipfloorplanningoptimization.representation.Representation;

import java.util.Random;

public class Cost {


    private double Anorm;
    private double Wnorm;
    private final double alpha; // 0 < alpha < 1

    /**
     * @param alpha "controls the weight between area and wirelength"
     * @param perturbations number of perturbations for the calculation of
     *                      Anorm and Wnorm (maybe proportional to the problem size)
     * @param initialSolution an initial solution to apply perturbations on (the solution will be changed)
     */
    public Cost(double alpha, int perturbations, Representation initialSolution) {
        if (alpha > 1 || alpha < 0)
            throw new IllegalArgumentException("0 < alpha < 1!");
        this.alpha = alpha;

        // calculate Anorm and Wnorm
        Random random = new Random();
        Anorm = 0;
        Wnorm = 0;
        for (int i = 0; i < perturbations; i++) {
            Runnable[] operations = initialSolution.operations();
            operations[random.nextInt(operations.length)].run();
            Floorplan floorplan = initialSolution.unpack();
            Anorm += floorplan.area();
            Wnorm += floorplan.totalWireLength();
        }
        Anorm /= perturbations; // average area
        Wnorm /= perturbations; // average wirelength
        if (Wnorm == 0)
            Wnorm = 1;
    }

    /**
     * Evaluate the cost of a solution.
     * @param r - a solution
     * @return cost
     */
    public double evaluate(Representation r) {
        Floorplan floorplan = r.unpack();
        return alpha * (floorplan.area() / Anorm) + (1 - alpha) * (floorplan.totalWireLength() / Wnorm);
    }

}
