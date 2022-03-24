package com.example.chipfloorplanningoptimization.optimization.genetic_algorithm;

import com.example.chipfloorplanningoptimization.representation.BTree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface Selection {

    /**
     * Called every generation to update the current population and their fitness values.
     * @param population population
     * @param fitness a solution-to-fitness map
     */
    void updatePopulation(ArrayList<BTree> population, HashMap<BTree, Double> fitness);

    /**
     * Selects two parents from the population
     * @return two solutions from the population to become parents
     */
    BTree[] select();

}
