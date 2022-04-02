package com.example.chipfloorplanningoptimization.optimization.genetic_algorithm;

import com.example.chipfloorplanningoptimization.optimization.costs.Cost;
import com.example.chipfloorplanningoptimization.optimization.DataCollector;
import com.example.chipfloorplanningoptimization.optimization.Optimizer;
import com.example.chipfloorplanningoptimization.optimization.genetic_algorithm.crossover.Crossover;
import com.example.chipfloorplanningoptimization.optimization.genetic_algorithm.crossover.PartiallyMappedCrossover;
import com.example.chipfloorplanningoptimization.optimization.genetic_algorithm.fitness_functions.FitnessFunction;
import com.example.chipfloorplanningoptimization.optimization.genetic_algorithm.mutation.Mutation;
import com.example.chipfloorplanningoptimization.optimization.genetic_algorithm.selection.RouletteSelection;
import com.example.chipfloorplanningoptimization.optimization.genetic_algorithm.selection.Selection;
import com.example.chipfloorplanningoptimization.representation.Representation;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class GeneticAlgorithm<T extends Representation<T>> implements Optimizer<T> {

    private final int populationSize;
    private final int generations;
    private final Crossover<T> cross;
    private final Selection<T> selector;
    private final FitnessFunction<T> fitnessFunction;
    private final Cost<T> cost;
    private final Mutation<T> mutation;
    private List<T> progress;
    private DataCollector dc;

    public GeneticAlgorithm(int populationSize, int generations, Crossover<T> cross, Selection<T> selector, Cost<T> cost, FitnessFunction<T> fitnessFunction, Mutation<T> mutation) {
        this.populationSize = populationSize;
        this.generations = generations;
        this.cross = cross;
        this.selector = selector;
        this.cost = cost;
        this.fitnessFunction = fitnessFunction;
        this.mutation = mutation;
    }

    public GeneticAlgorithm(int populationSize, int generations, Cost<T> cost, FitnessFunction<T> fitnessFunction, Mutation<T> mutation) {
        this(populationSize, generations, new PartiallyMappedCrossover<>(), new RouletteSelection<>(), cost, fitnessFunction, mutation);
    }

    @Override
    public T optimize(T initialSolution) {
        // Initial population
        ArrayList<T> population = createInitialPopulation(initialSolution, populationSize);
        HashMap<T, Double> fitness = fitnessFunction.apply(population, cost);
        selector.updatePopulation(population, fitness);

        progress = new LinkedList<>();

        T bestSolution = getCurrentBestSolution(population, fitness);
        double lowestCost = cost.evaluate(bestSolution);
        for (int i = 0; i < generations; i++) {

            // Selection + Crossover
            ArrayList<T> newPopulation = new ArrayList<>(populationSize + 1);
            for (int j = 0; j < populationSize / 2; j++) {
                List<T> children = cross.crossover(selector.select());
                for (T child : children) {
                    // Mutation:
                    mutation.mutate(child, i, lowestCost);
                    newPopulation.add(child);
                }
            }

            dc.getLogger("Generation", "Mutation Rate").log(i, mutation.getMutationRate());

            // New population
            population = newPopulation;

            // Create fitness map
            fitness = fitnessFunction.apply(population, cost);

            // Compare to bestSolution
            T currentBest = getCurrentBestSolution(population, fitness);
            dc.getLogger("Generation", "Current Lowest Cost").log(i, cost.evaluate(currentBest));
            if (cost.evaluate(currentBest) < lowestCost) {
                bestSolution = currentBest;
                lowestCost = cost.evaluate(currentBest);

                if (progress.size() <= i / 20)
                    progress.add(bestSolution.copy());
            }
            dc.getLogger("Generation", "Lowest Cost So Far").log(i, lowestCost);
            if (lowestCost == cost.getMinimumPossible())
                break;

            // Update selector
            selector.updatePopulation(population, fitness);

            if (i % 10 == 0) {
                double averageCost = population.stream().mapToDouble(cost::evaluate).sum() / population.size();
                dc.getLogger("Generation", "Average Cost").log(i, averageCost);
                if (i % 100 == 0)
                    System.out.println(averageCost);
            }
        }

        return bestSolution;
    }

    private T getCurrentBestSolution(ArrayList<T> population, HashMap<T, Double> fitness) {
        return population.stream()
                .reduce((a, b) -> (fitness.get(a) > fitness.get(b) ? a : b))
                .orElse(null);
    }

    private ArrayList<T> createInitialPopulation(T initialSolution, int populationSize) {
        ArrayList<T> population = new ArrayList<>(populationSize + 1);

        T tree = initialSolution.copy();
        for (int i = 0; i < populationSize; i++) {
            population.add(tree.copy());
            tree.pack(initialSolution.unpack());
            int perturbs = (int)(Math.random() * 10);
            for (int j = 0; j < perturbs; j++) tree.perturb();
        }

        return population;
    }

    @Override
    public void saveParams(String folderPath) throws IOException {
        FileWriter writer = new FileWriter(folderPath + "/params.txt");
        writer.write("Optimizer: Genetic Algorithm\n");
        writer.write("Cost Function: " + cost.getName() + "\n");
        writer.write(cost.paramsDescription() + "\n");
        writer.write("Population size: " + populationSize + "\n");
        writer.write("Max generations: " + generations + "\n");
        writer.write("Crossover: " + cross.getName() + "\n");
        writer.write("Selection: " + selector.getName() + "\n");
        writer.write("Fitness Function: " + fitnessFunction.getName() + "\n");
        writer.write("Mutation: " + mutation.getName() + "\n");
        writer.write(mutation.paramsDescription());
        writer.close();
    }

    @Override
    public Cost<T> getCost() {
        return cost;
    }

    @Override
    public void setDataCollector(String outputDirectory) {
        this.dc = new DataCollector(outputDirectory);
        dc.addLogger("Generation", "Current Lowest Cost");
        dc.addLogger("Generation", "Lowest Cost So Far");
        dc.addLogger("Generation", "Average Cost");
        dc.addLogger("Generation", "Mutation Rate");
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
        return "Genetic Algorithm";
    }

    @Override
    public List<T> getProgress() {
        return progress;
    }
}
