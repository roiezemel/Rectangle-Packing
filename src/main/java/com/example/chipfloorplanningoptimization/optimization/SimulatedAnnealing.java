package com.example.chipfloorplanningoptimization.optimization;
import com.example.chipfloorplanningoptimization.gui.IOManager;
import com.example.chipfloorplanningoptimization.representation.Representation;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.lang.Math;

public class SimulatedAnnealing implements Optimizer{

    private final int iterations;    // number of iterations
    private final double finalTemperature;
    private final double r; // the ratio by which the temperature is reduced each iteration
    private final double P; // initial probability of accepting an uphill solution
    private final Cost cost;
    private final double rejectQuitPercent;
    private static final Random random = new Random();
    private DataCollector dc;

    /**
     * Initiate the Simulated Annealing
     * @param iterations number of iterations to perform in each temperature level
     * @param uphillProbability the initial probability of accepting an uphill solution.
     *                          Can be set very close to 1 (but not 1).
     * @param finalTemperature the minimum temperature that the algorithm can get to
     * @param reduceRatio the ratio by which the temperature is reduced each iteration
     * @param rejectQuitPercent the algorithm will stop once the ratio rejections/iterations
     *                          is greater than this number
     * @param cost a Cost object
     */
    public SimulatedAnnealing(int iterations, double uphillProbability, double finalTemperature, double reduceRatio, double rejectQuitPercent, Cost cost) {
        this.r = reduceRatio;
        this.P = uphillProbability;
        this.iterations = iterations;
        this.finalTemperature = finalTemperature;
        this.cost = cost;
        this.rejectQuitPercent = rejectQuitPercent;
    }

    public SimulatedAnnealing(int iterations, double uphillProbability, double finalTemperature, double rejectQuitPercent, Cost cost) {
        this(iterations, uphillProbability, finalTemperature, 0.85, rejectQuitPercent, cost);
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
    public <T extends Representation<T>> T optimize(T initialSolution) {
        T solution = initialSolution.copy();
        T bestSolution = initialSolution.copy();
        double lowestCost = cost.evaluate(bestSolution);
        double T = calculateInitialTemperature(initialSolution, 50);
        int reject = 0;
        T prevSolution = solution.copy();
        double prevCost = lowestCost;

        System.out.println("Starting with temperature: " + T);
        System.out.println("Cost: " + lowestCost);
        int count = 0;

        if (dc != null)
            dc.getLogger("Temperature", "Lowest Cost").log(T, lowestCost); // first value of the lowest cost

        while ((double) reject / iterations <= rejectQuitPercent && T >= finalTemperature) {
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

            if (dc != null) { // log values
                dc.getLogger("Temperature", "Average Cost").log(T, avgCost);
                dc.getLogger("Temperature", "Rejections").log(T, reject);
                dc.getLogger("Temperature", "Lowest Cost").log(T, lowestCost);
                dc.getLogger("Time", "Temperature").log(count, T);
                dc.getLogger("Time", "Average Cost").log(count, avgCost);
            }

            count++;
            T = temperatureProtocol(T);
        }

        System.out.println("Finished with temperature: " + T);
        System.out.println("Cost: " + lowestCost);
        return bestSolution;
    }

    @Override
    public void saveParams(String folderPath) throws IOException {
        FileWriter writer = new FileWriter(folderPath + "/params.txt");
        writer.write("Cost weight (alpha): " + cost.getAlpha() + "\n");
        writer.write("Iterations (per temperature): " + iterations + "\n");
        writer.write("Initial uphill probability: " + P + "\n");
        writer.write("Minimum temperature: " + finalTemperature + "\n");
        writer.write("Reduce ratio: " + r + "\n");
        writer.write("Quit at reject/iterations > " + rejectQuitPercent);
        writer.close();
    }

    @Override
    public void setDataCollector(String outputDirectory) {
        this.dc = new DataCollector(outputDirectory);
        dc.addLogger("Temperature", "Average Cost");
        dc.addLogger("Temperature", "Rejections");
        dc.addLogger("Temperature", "Lowest Cost");
        dc.addLogger("Time", "Temperature");
        dc.addLogger("Time", "Average Cost");
    }

    @Override
    public DataCollector getDataCollector() {
        return dc;
    }

    @Override
    public void closeDataCollector() {
        dc.close();
    }

}

