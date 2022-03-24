package com.example.chipfloorplanningoptimization.optimization;

import com.example.chipfloorplanningoptimization.representation.Representation;

import java.io.IOException;

public interface Optimizer<T extends Representation<T>> {

    T optimize(T initialSolution);

    void saveParams(String path) throws IOException;

    void setDataCollector(String outputDirectory);

    DataCollector getDataCollector();

    void closeDataCollector();

    String getName();

}
