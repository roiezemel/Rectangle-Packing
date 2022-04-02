package com.example.chipfloorplanningoptimization.optimization.genetic_algorithm.mutation;

import com.example.chipfloorplanningoptimization.representation.Representation;

public interface Mutation<T extends Representation<T>> {

    void mutate(T child, int time, double lowestCost);

    double getMutationRate();

    String getName();

    String paramsDescription();

}
