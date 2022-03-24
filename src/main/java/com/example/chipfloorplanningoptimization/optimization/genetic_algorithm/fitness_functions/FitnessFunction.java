package com.example.chipfloorplanningoptimization.optimization.genetic_algorithm.fitness_functions;

import com.example.chipfloorplanningoptimization.optimization.Cost;
import com.example.chipfloorplanningoptimization.optimization.NormCost;
import com.example.chipfloorplanningoptimization.representation.Representation;

import java.util.ArrayList;
import java.util.HashMap;

public interface FitnessFunction<T extends Representation<T>> {

    HashMap<T, Double> apply(ArrayList<T> population, Cost<T> cost);

    String getName();

}
