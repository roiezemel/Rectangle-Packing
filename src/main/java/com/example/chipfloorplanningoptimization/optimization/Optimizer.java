package com.example.chipfloorplanningoptimization.optimization;

import com.example.chipfloorplanningoptimization.representation.Representation;

public interface Optimizer {

    <T extends Representation<T>> T optimize(T initialSolution, OptimizationLogger... loggers);

}
