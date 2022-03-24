package com.example.chipfloorplanningoptimization.optimization;

import com.example.chipfloorplanningoptimization.gui.DataVisualizer;
import com.example.chipfloorplanningoptimization.optimization.Optimizer;
import com.example.chipfloorplanningoptimization.representation.Representation;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;

public class Experiment {

    private Optimizer op;

    public Experiment() {}

    public Experiment(Optimizer op) {
        setOptimizer(op);
    }

    public void setOptimizer(Optimizer op) {
        this.op = op;
    }

    public <T extends Representation<T>> T run(T original, String inputName) throws IOException {
        String exFolder = exDirectoryPath();
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

    private String exDirectoryPath() throws IOException {
        File file = new File("src/main/data");
        String[] directories = file.list((current, name) -> new File(current, name).isDirectory());
        int exNumber = 0;
        if (directories != null && directories.length > 0) {
            String lastEx = Arrays.stream(directories).max(Comparator.comparingInt((this::toExNumber))).get();
            exNumber = toExNumber(lastEx) + 1;
        }
        String path = "src/main/data/ex #" + exNumber;
        Files.createDirectories(Paths.get(path));
        return path;
    }

    private int toExNumber(String fileName) {
        return Integer.parseInt(fileName.replace("ex #", ""));
    }

}
