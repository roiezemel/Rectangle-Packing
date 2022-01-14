package com.example.chipfloorplanningoptimization.optimization;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.function.Function;

public class OptimizationLogger {

    private FileWriter writer;
    private final Function<double[], String> logger;

    public OptimizationLogger(String directoryPath, String name, Function<double[], String> logger) throws IOException {
        this.writer = new FileWriter(directoryPath + "/" + name + ".csv");
        this.logger = logger;
    }

    public void log(double... values) {
        try {
            writer.write(logger.apply(values) + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() throws IOException {
        writer.close();
    }

}
