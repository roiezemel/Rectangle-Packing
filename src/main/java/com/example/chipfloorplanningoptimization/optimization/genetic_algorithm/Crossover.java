package com.example.chipfloorplanningoptimization.optimization.genetic_algorithm;

import com.example.chipfloorplanningoptimization.representation.BTree;

public interface Crossover {

    BTree[] crossover(BTree[] parents);

}
