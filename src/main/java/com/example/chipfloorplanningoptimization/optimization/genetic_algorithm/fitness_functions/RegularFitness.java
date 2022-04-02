package com.example.chipfloorplanningoptimization.optimization.genetic_algorithm.fitness_functions;

import com.example.chipfloorplanningoptimization.optimization.costs.Cost;
import com.example.chipfloorplanningoptimization.representation.Representation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class RegularFitness<T extends Representation<T>> implements FitnessFunction<T> {


    @Override
    public HashMap<T, Double> apply(ArrayList<T> population, Cost<T> cost) {
        ArrayList<Double> costs = new ArrayList<>(population.size());
        population.forEach(tree -> costs.add(cost.evaluate(tree)));
        double maxArea = Collections.max(costs) + 1;

        HashMap<T, Double> fitness = new HashMap<>();
        for (int i = 0; i < population.size(); i++) {
            fitness.put(population.get(i), maxArea - costs.get(i));
        }
        return fitness;
    }

    @Override
    public String getName() {
        return "Regular Fitness";
    }
}
