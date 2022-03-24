package com.example.chipfloorplanningoptimization.optimization.genetic_algorithm;

import com.example.chipfloorplanningoptimization.optimization.Cost;
import com.example.chipfloorplanningoptimization.representation.BTree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.stream.DoubleStream;

@FunctionalInterface
public interface FitnessFunction {

    HashMap<BTree, Double> apply(ArrayList<BTree> population, Cost cost);

    FitnessFunction RegularFitness = (population, cost) -> {
        ArrayList<Double> costs = new ArrayList<>(population.size());
        population.forEach(tree -> costs.add(cost.evaluate(tree)));
        double maxArea = Collections.max(costs) + 1;

        HashMap<BTree, Double> fitness = new HashMap<>();
        for (int i = 0; i < population.size(); i++) {
            fitness.put(population.get(i), maxArea - costs.get(i));
        }
        return fitness;
    };

    FitnessFunction ReciprocalFitness = (population, cost) -> {
        HashMap<BTree, Double> fitness = new HashMap<>();
        for (BTree tree : population) {
            fitness.put(tree, 1. / cost.evaluate(tree));
        }
        return fitness;
    };

}
