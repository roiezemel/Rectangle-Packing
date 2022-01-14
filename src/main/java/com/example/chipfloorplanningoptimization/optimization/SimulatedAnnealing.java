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

    private double uphillProbability(double costChange, double temperature) {
        return Math.exp(-(costChange)/temperature);
    }

    /**
     * the loop that change the floorplan untill it's find a selution
     * @param initialSolution represntation
     * @return the represntation solution
     */
    @Override
    public <T extends Representation<T>> T optimize(T initialSolution, OptimizationLogger... loggers) {
        T solution = initialSolution.copy();
        T bestSolution = initialSolution.copy();
        double lowestCost = cost.evaluate(bestSolution);
        double T = calculateInitialTemperature(initialSolution, 20);
        int reject = 0;
        T prevSolution = solution.copy();
        double prevCost = lowestCost;

        System.out.println("Starting with temperature: " + T);
        System.out.println("Cost: " + lowestCost);

        while ((double) reject / iterations <= 0.95 && T >= finalTemperature) {
            reject = 0;
            double avgCost = 0;
            for (int i = 0; i < iterations; i++) {
                solution.perturb(); // random operation

                double newCost = cost.evaluate(solution);
                double costChange = newCost - prevCost;

                if (newCost >= prevCost && random.nextDouble() > uphillProbability(costChange, T)) {/* it's backward - if it's true the change is bad*/
                    solution = prevSolution;
                    newCost = prevCost;
                    reject++;
                }
                else if (newCost < lowestCost) {
                    lowestCost = newCost;
                    bestSolution = solution.copy();
                }

                prevSolution = solution.copy();
                prevCost = newCost;

                avgCost += newCost;
            }
            avgCost /= iterations;

            loggers[0].log(T, avgCost);
            loggers[1].log(T, reject);
            T = temperatureProtocol(T);
        }

        System.out.println("Finished with temperature: " + T);
        System.out.println("Cost: " + lowestCost);
        return bestSolution;
    }

}

