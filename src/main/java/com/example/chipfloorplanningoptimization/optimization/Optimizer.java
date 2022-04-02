package com.example.chipfloorplanningoptimization.optimization;

import com.example.chipfloorplanningoptimization.optimization.costs.Cost;
import com.example.chipfloorplanningoptimization.representation.Representation;

import java.io.IOException;
import java.util.List;

public interface Optimizer<T extends Representation<T>> {

    T optimize(T initialSolution);

    void saveParams(String path) throws IOException;

    Cost<T> getCost();

    void setDataCollector(String outputDirectory);

    DataCollector getDataCollector();

    void closeDataCollector();

    String getName();

    List<T> getProgress();

}
