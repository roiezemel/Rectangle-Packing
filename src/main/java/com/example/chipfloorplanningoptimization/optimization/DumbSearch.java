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

    private final Cost<T> cost;
    private DataCollector dc;
    private List<T> progress;
    private static final Random random = new Random();
    private boolean initiallyPerturb = true;

    public DumbSearch(Cost<T> cost) {
        this.cost = cost;
    }

    public DumbSearch(Cost<T> cost, boolean initiallyPerturb) {
        this(cost);
        this.initiallyPerturb = initiallyPerturb;
    }

    @Override
    public T optimize(T initialSolution) {
        return optimize(initialSolution, 1000000, true);
    }

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
            for (int j = 0; j < perts; j++)
                local.perturb();
            double newCost = cost.evaluate(local);
            T mutation;
            double mutationCost;
            if (mutate && Math.random() < 0.1 && (mutationCost = cost.evaluate(mutation = optimize(local, 100, false))) < newCost) {
                local = mutation;
                newCost = mutationCost;
            }
            if (newCost < lcost) {
                lcost = newCost;
                copy = local;

                if (progress.size() <= i / 100) {
                    progress.add(copy.copy());
                }
            }
            sum += newCost;

            if (mutate && i % 10000 == 0) {
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


    @Override
    public void saveParams(String path) throws IOException {
        FileWriter writer = new FileWriter(path + "/params.txt");
        writer.write("Optimizer: Dumb Search\n");
        writer.write("Cost Function: " + cost.getName() + "\n");
        writer.write(cost.paramsDescription() + "\n");
        writer.close();
    }

    @Override
    public Cost<T> getCost() {
        return cost;
    }

    @Override
    public void setDataCollector(String outputDirectory) {
        this.dc = new DataCollector(outputDirectory);
        dc.addLogger("Time", "Lowest Cost");
        dc.addLogger("Time", "Average Cost");
        dc.addLogger("Perturbations", "Probability");
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
        return "Dumb Search";
    }

    @Override
    public List<T> getProgress() {
        return progress;
    }
}
