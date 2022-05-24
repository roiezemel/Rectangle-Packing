package com.example.chipfloorplanningoptimization.optimization;

import com.example.chipfloorplanningoptimization.optimization.costs.Cost;
import com.example.chipfloorplanningoptimization.optimization.costs.NormCost;
import com.example.chipfloorplanningoptimization.representation.Representation;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class DumbSearch<T extends Representation<T>> implements Optimizer<T> {

    /*
        This class is not yet optimal since it was the last to be developed
     */

    private final Cost<T> cost;
    private DataCollector dc;
    private List<T> progress;
    private static final Random random = new Random();
    private boolean initiallyPerturb = true;

    /**
     * Initialize DumbSearch
     * @param cost Cost function
     */
    public DumbSearch(Cost<T> cost) {
        this.cost = cost;
    }

    /**
     * Initialize DumbSearch
     * @param cost Cost function
     * @param initiallyPerturb how many perturbations to perform before the optimization starts
     */
    public DumbSearch(Cost<T> cost, boolean initiallyPerturb) {
        this(cost);
        this.initiallyPerturb = initiallyPerturb;
    }

    /**
     * Optimize a solution
     * @param initialSolution the initial solution
     * @return an optimized solution
     */
    @Override
    public T optimize(T initialSolution) {
        return optimize(initialSolution, 1000000, true);
    }

    /**
     * Optimize a solution on a single "branch". Since this algorithm is recursive,
     * this method is called multiple times on different "branches".
     * @param initialSolution initial solution
     * @param its number of iterations to perform
     * @param mutate whether to activate the mutation stage (should only be true on main branch,
     *               called by the second optimize() method)
     * @return an optimized solution
     */
    private T optimize(T initialSolution, int its, boolean mutate) {
        T copy = initialSolution.copy();
        if (mutate) {
            if (initiallyPerturb)
                for (int i = 0; i < 10; i++)
                    copy.perturb();
            progress = new LinkedList<>();
        }
        double lcost = cost.evaluate(copy);
        double sum = 0;
        int[] dis = new int[200];
        for (int i = 1; i < its; i++) {
            T local = copy.copy();
            int perts = (int)(Math.abs(random.nextGaussian()) * 50) + 1;
//            int perts = (random.nextInt(100)) + 1;
            if (perts < dis.length)
                dis[perts]++;
            for (int j = 0; j < perts; j++) // perturb current solution
                local.perturb();
            double newCost = cost.evaluate(local); // neighbor's Cost
            T mutation;
            double mutationCost;

            // Send solution to a "mutation branch" in a recursive manner
            if (mutate && Math.random() < 0.1 && (mutationCost = cost.evaluate(mutation = optimize(local, 100, false))) < newCost) {
                local = mutation;
                newCost = mutationCost;
            }
            if (newCost < lcost) { // update best solution
                lcost = newCost;
                copy = local;

                if (progress.size() <= i / 100) {
                    progress.add(copy.copy());
                }
            }
            sum += newCost;

            if (mutate && i % 10000 == 0) { // Update loggers
                dc.getLogger("Time", "Lowest Cost").log(i / 10000., lcost);
                dc.getLogger("Time", "Average Cost").log(i / 10000., sum / 10000);
                sum = 0;
            }
        }

        if (mutate) {
            int total = Arrays.stream(dis).sum();
            for (int i = 1; i < dis.length; i++) {
                dc.getLogger("Perturbations", "Probability").log(i, (double) dis[i] / total);
            }
        }

        return copy;
    }

    /**
     * Save the parameters of the algorithm
     * @param path data folder in  which the params.txt file will be saved
     * @throws IOException
     */
    @Override
    public void saveParams(String path) throws IOException {
        FileWriter writer = new FileWriter(path + "/params.txt");
        writer.write("Optimizer: Dumb Search\n");
        writer.write("Cost Function: " + cost.getName() + "\n");
        writer.write(cost.paramsDescription() + "\n");
        writer.close();
    }

    /**
     * Get the Cost function
     * @return
     */
    @Override
    public Cost<T> getCost() {
        return cost;
    }

    /**
     * Set the data collector instance to track certain metrics
     * @param outputDirectory data folder
     */
    @Override
    public void setDataCollector(String outputDirectory) {
        this.dc = new DataCollector(outputDirectory);
        dc.addLogger("Time", "Lowest Cost");
        dc.addLogger("Time", "Average Cost");
        dc.addLogger("Perturbations", "Probability");
    }

    /**
     * Get the DataCollector instance
     * @return
     */
    @Override
    public DataCollector getDataCollector() {
        return dc;
    }

    /**
     * Close data collector
     */
    @Override
    public void closeDataCollector() {
        dc.close();
    }

    /**
     * Get algorithm name
     * @return
     */
    @Override
    public String getName() {
        return "Dumb Search";
    }

    /**
     * Get list of solutions representing the progress of the algorithm
     * @return
     */
    @Override
    public List<T> getProgress() {
        return progress;
    }
}
