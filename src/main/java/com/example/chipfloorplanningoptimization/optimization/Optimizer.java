package com.example.chipfloorplanningoptimization.optimization;

import com.example.chipfloorplanningoptimization.representation.Representation;

import java.io.IOException;

public interface Optimizer {

    <T extends Representation<T>> T optimize(T initialSolution, OptimizationLogger... loggers);

    void saveParams(String path) throws IOException;

}
