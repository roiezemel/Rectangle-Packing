package com.example.chipfloorplanningoptimization.optimization;

import com.example.chipfloorplanningoptimization.gui.DataVisualizer;
import com.example.chipfloorplanningoptimization.optimization.Optimizer;
import com.example.chipfloorplanningoptimization.representation.Representation;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;

public class Experiment<T extends Representation<T>> {

    public enum Optimizers {
        GENETIC_ALGORITHM,
        SIMULATED_ANNEALING
    }

    private Optimizer<T> op;

    public Experiment() {}

    public Experiment(Optimizer<T> op) {
        setOptimizer(op);
    }

    public void setOptimizer(Optimizer<T> op) {
        this.op = op;
    }

    public T run(T original, String inputName) throws IOException {
        return run(original, inputName, op.getName() + "/" + LocalDate.now());
    }

    public T run(T original, String inputName, String experimentFolder) throws IOException {
        String exFolder = exDirectoryPath(experimentFolder);
        op.setDataCollector(exFolder);

        T optimized = op.optimize(original);

        op.closeDataCollector();
        op.saveParams(exFolder);

        try {
            optimized.save(exFolder + "/" + inputName + "-result.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }

        DataVisualizer dv = op.getDataCollector().getDataVisualizer();
        try {
            dv.saveCharts();
        } catch (IOException e) {
            e.printStackTrace();
        }
        dv.displayCharts(true);

        return optimized;
    }

    private String exDirectoryPath(String relativeFolder) throws IOException {
        String pathToFolder = "src/main/data/" + relativeFolder;
        File file = new File(pathToFolder);
        String[] directories = file.list((current, name) -> new File(current, name).isDirectory());
        int exNumber = 0;
        if (directories != null && directories.length > 0) {
            String lastEx = Arrays.stream(directories).max(Comparator.comparingInt((this::toExNumber))).get();
            exNumber = toExNumber(lastEx) + 1;
        }
        String path = pathToFolder + "/ex #" + exNumber;
        Files.createDirectories(Paths.get(path));
        return path;
    }

    private int toExNumber(String fileName) {
        return Integer.parseInt(fileName.replace("ex #", ""));
    }

}
