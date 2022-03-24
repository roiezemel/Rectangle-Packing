package com.example.chipfloorplanningoptimization.optimization.genetic_algorithm;

import com.example.chipfloorplanningoptimization.representation.BTree;
import com.example.chipfloorplanningoptimization.representation.Representation;

import java.util.List;

public interface Crossover<T extends Representation<T>> {

    List<T> crossover(List<T> parents);

    String getName();

}
