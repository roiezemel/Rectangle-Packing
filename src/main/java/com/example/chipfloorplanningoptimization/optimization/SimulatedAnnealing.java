package com.example.chipfloorplanningoptimization.optimization;
import com.example.chipfloorplanningoptimization.representation.Representation;

import java.util.Random;
import java.lang.Math;

public class SimulatedAnnealing implements Optimizer{

    private final int iterations;    // number of iterations
    private final double finalTemperature;
    private final double r; // the ratio by which the temperature is reduced each iteration
    private final double P; // initial probability of accepting an uphill solution
    private final Cost cost;
    private static final Random random = new Random();

    /**
     * Initiate the Simulated Annealing
     * @param iterations number of iterations to perform in each temperature level
     * @param uphillProbability the initial probability of accepting an uphill solution.
     *                          Can be set very close to 1 (but not 1).
     * @param finalTemperature the minimum temperature that the algorithm can get to
     * @param reduceRatio the ratio by which the temperature is reduced each iteration
     * @param cost a Cost object
     */
    public SimulatedAnnealing(int iterations, double uphillProbability, double finalTemperature, double reduceRatio, Cost cost) {
        this.r = reduceRatio;
        this.P = uphillProbability;
        this.iterations = iterations;
        this.finalTemperature = finalTemperature;
        this.cost = cost;
    }

    public SimulatedAnnealing(int iterations, double uphillProbability, double finalTemperature, Cost cost) {
        this(iterations, uphillProbability, finalTemperature, 0.85, cost);
    }
/*
* do the change in the temperature
*/
    private double temperatureProtocol(double T) {
        return r * T; // reduce temperature
    }

    private <T extends Representation<T>> double calculateInitialTemperature(T initialSolution, int perturbations) {
        T solution = initialSolution.copy();
        double avgCost = 0;
        double prevCost = cost.evaluate(solution);
        for (int i = 0; i < perturbations; i++) {
            solution.perturb();
            double newCost = cost.evaluate(solution);
            if (newCost > prevCost)
                avgCost += newCost - prevCost;
            prevCost = newCost;
        }
        avgCost /= perturbations;

        return -avgCost / Math.log(P);
    }

    /**
     * the loop that change the floorplan untill it's find a selution
     * @param initialSolution represntation
     * @return the represntation solution
     */
    @Override
    public <T extends Representation<T>> T optimize(T initialSolution) {
        T solution = initialSolution.copy();
        T bestSolution = initialSolution.copy();
        double lowestCost = cost.evaluate(bestSolution);
        double T = calculateInitialTemperature(initialSolution, 20);
        T temp;
        int reject = 0;
        System.out.println("Starting with temperature: " + T);
        System.out.println("Cost: " + lowestCost);
        while ((double) reject / iterations <= 0.95 && T >= finalTemperature) {
            reject = 0;
            for (int i = 0; i < iterations; i++) {
                temp = solution.copy();
                solution.perturb();
                double rateChange = cost.evaluate(solution);
                double rateBefore = cost.evaluate(temp);
                if (rateChange >= rateBefore && random.nextDouble() > Math.exp(-(rateChange - rateBefore)/T)) {/* it's backward - if it's true the change is bad*/
                    solution = temp;
                    reject++;
                }
                else if (rateChange < lowestCost) {
                    lowestCost = rateChange;
                    bestSolution = solution.copy();
                }
            }
            T = temperatureProtocol(T);
        }

        System.out.println("Finished with temperature: " + T);
        System.out.println("Cost: " + lowestCost);
        return bestSolution;
    }
}

