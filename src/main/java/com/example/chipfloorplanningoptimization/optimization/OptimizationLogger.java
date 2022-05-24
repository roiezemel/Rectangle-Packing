package com.example.chipfloorplanningoptimization.optimization;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.function.Function;

public class OptimizationLogger {

    private final FileWriter writer;
    private final Function<double[], String> logger;

    /**
     *
     * Initialize Logger
     * @param directoryPath path to experiment folder
     * @param name name of csv file
     * @param logger a function that translates an array of metrics (doubles) to a string
     * @throws IOException
     */
    public OptimizationLogger(String directoryPath, String name, Function<double[], String> logger) throws IOException {
        this.writer = new FileWriter(directoryPath + "/" + name + ".csv");
        this.logger = logger;
    }

    /**
     * Write values to file
     * @param values array of values
     */
    public void log(double... values) {
        try {
            writer.write(logger.apply(values) + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Close the file
     * @throws IOException
     */
    public void close() throws IOException {
        writer.close();
    }

}
