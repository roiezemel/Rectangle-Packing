package com.example.chipfloorplanningoptimization.optimization.simulated_annealing;
import com.example.chipfloorplanningoptimization.optimization.NormCost;
import com.example.chipfloorplanningoptimization.optimization.DataCollector;
import com.example.chipfloorplanningoptimization.optimization.Optimizer;
import com.example.chipfloorplanningoptimization.optimization.simulated_annealing.cooling_schedule.CoolingSchedule;
import com.example.chipfloorplanningoptimization.representation.Representation;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.lang.Math;

public class SimulatedAnnealing<T extends Representation<T>> implements Optimizer<T> {

    private final int iterations;    // number of iterations
    private final double finalTemperature;
    private double timeLimit;
    private double r; // the ratio by which the temperature is reduced each iteration
    private final double P; // initial probability of accepting an uphill solution
    private final NormCost<T> cost;
    private final double rejectQuitPercent;
    private static final Random random = new Random();
    private DataCollector dc;
    private CoolingSchedule coolingSchedule;

    /**
     * Initiate the Simulated Annealing
     * @param iterations number of iterations to perform in each temperature level
     * @param uphillProbability the initial probability of accepting an uphill solution.
     *                          Can be set very close to 1 (but not 1).
     * @param finalTemperature the minimum temperature that the algorithm can get to
     * @param rejectQuitPercent the algorithm will stop once the ratio rejections/iterations
     *                          is greater than this number
     * @param cost a Cost object
     */
    public SimulatedAnnealing(int iterations, double uphillProbability, double finalTemperature, double rejectQuitPercent, NormCost<T> cost) {
        this.r = 0.85;
        this.P = uphillProbability;
        this.iterations = iterations;
        this.finalTemperature = finalTemperature;
        this.cost = cost;
        this.rejectQuitPercent = rejectQuitPercent;
        this.timeLimit = -1;
        setDefaultCoolingSchedule(r);
    }

    public SimulatedAnnealing(int iterations, double uphillProbability, double finalTemperature, double rejectQuitPercent, int timeLimit, NormCost<T> cost) {
        this(iterations, uphillProbability, finalTemperature, rejectQuitPercent, cost);
        this.timeLimit = timeLimit;
    }

    /**
     * do the change in the temperature
     * @param reduceRatio the ratio by which the temperature is reduced each iteration
     */
    public void setDefaultCoolingSchedule(double reduceRatio) {
        this.r = reduceRatio;
        this.coolingSchedule = (T, ig1, ig2, ig3, ig4, ig5) -> r * T;
    }

    public void setCoolingSchedule(CoolingSchedule coolingSchedule) {
        this.coolingSchedule = coolingSchedule;
    }

    private double calculateInitialTemperature(T initialSolution, int perturbations) {
        return -calculateAverageUphillCost(initialSolution, perturbations) / Math.log(P);
    }

    private double calculateAverageUphillCost(T initialSolution, int perturbations) {
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
        return avgCost / perturbations;
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
    public T optimize(T initialSolution) {
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
            dc.getLogger("Time", "Lowest Cost").log(count, lowestCost); // first value of the lowest cost

        while ((double) reject / iterations <= rejectQuitPercent && T >= finalTemperature && count != timeLimit) {
            reject = 0;
            double avgCost = 0;
            double avgCostChange = 0;
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
                avgCostChange += Math.abs(costChange);
            }
            avgCost /= iterations;
            avgCostChange /= iterations;

            if (dc != null) { // log values
                dc.getLogger("Temperature", "Average Cost").log(T, avgCost);
                dc.getLogger("Time", "Rejections").log(count, reject);
                dc.getLogger("Time", "Lowest Cost").log(count, lowestCost);
//                if (count > 0)
                    dc.getLogger("Time", "Temperature").log(count, T);
                dc.getLogger("Time", "Average Cost").log(count, avgCost);
            }

            count++;
            T = coolingSchedule.next(T, avgCostChange, count, avgCost, lowestCost, reject);
        }

        System.out.println("Finished with temperature: " + T);
        System.out.println("Cost: " + lowestCost);
        return bestSolution;
    }

    @Override
    public void saveParams(String folderPath) throws IOException {
        FileWriter writer = new FileWriter(folderPath + "/params.txt");
        writer.write("Optimizer: Simulated Annealing\n");
        writer.write("Cost Function: " + cost.getName() + "\n");
        writer.write(cost.paramsDescription() + "\n");
        writer.write("Iterations (per temperature): " + iterations + "\n");
        writer.write("Initial uphill probability: " + P + "\n");
        writer.write("Minimum temperature: " + finalTemperature + "\n");
        writer.write("Reduce ratio: " + r + "\n");
        writer.write("Quit at reject/iterations > " + rejectQuitPercent + "\n");
        writer.write("Cooling Schedule: " + coolingSchedule.getName());
        writer.close();
    }

    @Override
    public void setDataCollector(String outputDirectory) {
        this.dc = new DataCollector(outputDirectory);
        dc.addLogger("Temperature", "Average Cost");
        dc.addLogger("Time", "Rejections");
        dc.addLogger("Time", "Lowest Cost");
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

    @Override
    public String getName() {
        return "Simulated Annealing";
    }

}

