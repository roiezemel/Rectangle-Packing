package com.example.chipfloorplanningoptimization.optimization.genetic_algorithm.fitness_functions;

import com.example.chipfloorplanningoptimization.optimization.costs.Cost;
import com.example.chipfloorplanningoptimization.representation.Representation;

import java.util.ArrayList;
import java.util.HashMap;

public class ReciprocalFitness<T extends Representation<T>> implements FitnessFunction<T> {
    @Override
    public HashMap<T, Double> apply(ArrayList<T> population, Cost<T> cost) {
        HashMap<T, Double> fitness = new HashMap<>();
        for (T tree : population) {
            fitness.put(tree, 1. / cost.evaluate(tree));
        }
        return fitness;
    }

    @Override
    public String getName() {
        return "Reciprocal Fitness";
    }
}
