package com.example.chipfloorplanningoptimization.optimization;

import com.example.chipfloorplanningoptimization.representation.Representation;

public interface Cost<T extends Representation<T>> {

    /**
     * Evaluate a given solution
     * @param r a solution
     * @return cost of the given solution
     */
    double evaluate(T r);

    /**
     * Get the minimum cost possible by this specific Cost implementation
     * @return the minimum cost possible
     */
    double getMinimumPossible();

    /**
     * Get a description of the parameters that the Cost function uses.
     * Used for analysing experiments.
     * @return a text describing the parameters of the Cost function
     */
    String paramsDescription();

    /**
     * Get the name of the Cost function
     * @return the name of the Cost function
     */
    String getName();

}
