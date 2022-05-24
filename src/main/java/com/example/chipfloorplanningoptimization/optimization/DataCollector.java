package com.example.chipfloorplanningoptimization.optimization;

import com.example.chipfloorplanningoptimization.gui.DataVisualizer;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;

public class DataCollector {

    private final HashMap<String, OptimizationLogger> loggers;
    private final String directoryPath;

    /**
     * Initialize DataCollector
     * @param directoryPath data folder
     */
    public DataCollector(String directoryPath) {
        this.loggers = new HashMap<>();
        this.directoryPath = directoryPath;
    }

    /**
     * Add a new logger
     * @param seriesNames names of series to be followed
     */
    public void addLogger(String... seriesNames) {
        String name = String.join("_", seriesNames);
        OptimizationLogger logger = null;
        try {
            logger = new OptimizationLogger(directoryPath,
                    name,
                    (values) -> Arrays.stream(values)
                            .mapToObj(String::valueOf)
                            .collect(Collectors.joining(",")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        loggers.put(name, logger);
    }

    /**
     * Get a Logger according the series names
     * @param seriesNames series names that the DataCollector follows
     * @return a Logger
     */
    public OptimizationLogger getLogger(String... seriesNames) {
        return loggers.get(String.join("_", seriesNames));
    }

    /**
     * Create a DataVisualizer containing the data collected
     * @return DataVisualizer instance
     */
    public DataVisualizer getDataVisualizer() {
        DataVisualizer dv = new DataVisualizer(directoryPath);
        for (String name : loggers.keySet())
            dv.addFromCSV(name);
        return dv;
    }

    /**
     * Close all the loggers.
     */
    public void close() {
        for (OptimizationLogger logger : loggers.values()) {
            try {
                logger.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
