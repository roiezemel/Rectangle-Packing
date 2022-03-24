package com.example.chipfloorplanningoptimization.optimization.genetic_algorithm;

import com.example.chipfloorplanningoptimization.optimization.Cost;
import com.example.chipfloorplanningoptimization.optimization.DataCollector;
import com.example.chipfloorplanningoptimization.optimization.Optimizer;
import com.example.chipfloorplanningoptimization.representation.BTree;
import com.example.chipfloorplanningoptimization.representation.Representation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.function.DoubleFunction;
import java.util.function.DoubleSupplier;
import java.util.function.ToDoubleFunction;

public class GeneticAlgorithm {

    private int populationSize;
    private double mutationRate;
    private Crossover cross;
    private Selection selector;
    private FitnessFunction fitnessFunction;
    private Cost cost;

    public GeneticAlgorithm(int populationSize, double mutationRate, Crossover cross, Selection selector, Cost cost, FitnessFunction fitnessFunction) {
        this.populationSize = populationSize;
        this.mutationRate = mutationRate;
        this.cross = cross;
        this.selector = selector;
        this.cost = cost;
        this.fitnessFunction = fitnessFunction;
    }

    public GeneticAlgorithm(int populationSize, double mutationRate, Cost cost, FitnessFunction fitnessFunction) {
        this(populationSize, mutationRate, new PartiallyMappedCrossover(), new RouletteSelection(), cost, fitnessFunction);
    }

    public BTree[] optimize(BTree initialSolution, int generations) {
        // Initial population
        ArrayList<BTree> population = createInitialPopulation(initialSolution, populationSize);
        HashMap<BTree, Double> fitness = fitnessFunction.apply(population, cost);
        selector.updatePopulation(population, fitness);

        BTree bestSolution = getCurrentBestSolution(population, fitness);
        double lowestCost = cost.evaluate(bestSolution);
        for (int i = 0; i < generations; i++) {

            // Selection + Crossover
            ArrayList<BTree> newPopulation = new ArrayList<>(populationSize + 1);
            for (int j = 0; j < populationSize / 2; j++) {
                BTree[] children = cross.crossover(selector.select());
                for (BTree child : children) {
                    // Mutation:
                    if (Math.random() < mutationRate)
                        child.perturb();
                    newPopulation.add(child);
                }
            }

            // New population
            population = newPopulation;

            // Create fitness map
            fitness = fitnessFunction.apply(population, cost);

            // Compare to bestSolution
            BTree currentBest = getCurrentBestSolution(population, fitness);
            if (cost.evaluate(currentBest) < lowestCost) {
                bestSolution = currentBest;
                lowestCost = cost.evaluate(currentBest);
            }
            if (lowestCost == cost.getMinimumPossible())
                break;

            // Update selector
            selector.updatePopulation(population, fitness);

            if (i % 100 == 0)
                System.out.println(population.stream().mapToDouble(fitness::get).sum() / population.size());
        }

        return new BTree[] {bestSolution};
    }

    private BTree getCurrentBestSolution(ArrayList<BTree> population, HashMap<BTree, Double> fitness) {
        return population.stream()
                .reduce((a, b) -> (fitness.get(a) > fitness.get(b) ? a : b))
                .orElse(null);
    }

    private ArrayList<BTree> createInitialPopulation(BTree initialSolution, int populationSize) {
        ArrayList<BTree> population = new ArrayList<>(populationSize + 1);

        BTree tree = new BTree(initialSolution);
        for (int i = 0; i < populationSize; i++) {
            population.add(tree);
            tree = BTree.packFloorplan(initialSolution.unpack());
            int perturbs = (int)(Math.random() * 10);
            for (int j = 0; j < perturbs; j++) tree.perturb();
        }

        return population;
    }
}
