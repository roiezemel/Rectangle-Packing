package com.example.chipfloorplanningoptimization.optimization.genetic_algorithm.selection;

import com.example.chipfloorplanningoptimization.representation.BTree;
import com.example.chipfloorplanningoptimization.representation.Representation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface Selection <T extends Representation<T>> {

    /**
     * Called every generation to update the current population and their fitness values.
     * @param population population
     * @param fitness a solution-to-fitness map
     */
    void updatePopulation(ArrayList<T> population, HashMap<T, Double> fitness);

    /**
     * Selects two parents from the population
     * @return two solutions from the population to become parents
     */
    List<T> select();

    String getName();

}
