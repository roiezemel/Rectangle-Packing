package com.example.chipfloorplanningoptimization.optimization;

import com.example.chipfloorplanningoptimization.gui.DataVisualizer;
import com.example.chipfloorplanningoptimization.optimization.Optimizer;
import com.example.chipfloorplanningoptimization.representation.Floorplan;
import com.example.chipfloorplanningoptimization.representation.Representation;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class Experiment<T extends Representation<T>> {

    private List<Optimizer<T>> ops;
    private List<T> progress;
    private List<Floorplan> floorplansProgress;
    private String exFolder;

    /**
     * Initialize Experiment
     */
    public Experiment() {}

    /**
     * Initialize Experiment
     * @param op optimizer
     */
    public Experiment(Optimizer<T> op) {
        setOptimizer(op);
    }

    /**
     * Initialize Experiment
     * @param ops array of optimizers
     */
    @SafeVarargs
    public Experiment(Optimizer<T>... ops) {
        setOptimizers(ops);
    }

    /**
     * Set optimizer
     * @param op single optimizer
     */
    public void setOptimizer(Optimizer<T> op) {
        this.ops = new LinkedList<>() {{add(op);}};
    }

    /**
     * Set optimizers
     * @param ops list of optimizers
     */
    @SafeVarargs
    public final void setOptimizers(Optimizer<T>... ops) {
        this.ops = new LinkedList<>(Arrays.asList(ops));
    }

    /**
     * Run an experiment
     * @param original original solution
     * @param inputName name of the input data (used for the name of the result file)
     * @return an optimized solution
     * @throws IOException
     */
    public T run(T original, String inputName) throws IOException {
        String experimentFolder = ops.stream()
                .map(Optimizer::getName)
                .collect(Collectors.joining("-")) + "/" + LocalDate.now();
        return run(original, inputName, experimentFolder);
    }

    /**
     * Run an experiment, save data to specific experiment folder
     * @param original original solution
     * @param inputName name of the input data (used for the name of the result file)
     * @param experimentFolder path to experiment folder
     * @return an optimized solution
     * @throws IOException
     */
    public T run(T original, String inputName, String experimentFolder) throws IOException {
        exFolder = exDirectoryPath(experimentFolder);
        T result = original;
        progress = new LinkedList<>() {{add(original.copy());}};
        floorplansProgress = new LinkedList<>() {{add(original.unpack());}};
        for (Optimizer<T> op : ops) { // run each optimizer
            String path = exFolder;
            if (ops.size() > 1) {
                path += "/" + op.getName();
                Files.createDirectories(Paths.get(path));
            }
            if (!op.getCost().isReady())
                op.getCost().prepareForOptimization(result);
            System.out.println("Optimizing with - " + op.getName());
            result = runSingleOptimizer(op, result, inputName, path);
            progress.addAll(op.getProgress());
            floorplansProgress.addAll(op.getProgress().stream().map(t -> {
                Floorplan f = t.unpack();
                f.setName(op.getName());
                return f;
            }).collect(Collectors.toList()));
        }

        return result;
    }

    /**
     * Run a single optimizer
     * @param op optimizer
     * @param original original solution
     * @param inputName name of input data
     * @param exFolder experiment folder
     * @return an optimized solution
     * @throws IOException
     */
    private T runSingleOptimizer(Optimizer<T> op,T original, String inputName, String exFolder) throws IOException {
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

    /**
     * Get path to a new directory with a name starting with "ex #" + number of experiment
     * @param relativeFolder path folder in which to add the "ex#" folder, relative to src/main/data
     * @return path to experiment directory
     * @throws IOException
     */
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

    /**
     * Parse number of out "ex #" folder name
     * @param fileName name of the folder
     * @return ex number
     */
    private int toExNumber(String fileName) {
        return Integer.parseInt(fileName.replace("ex #", ""));
    }

    /**
     * Save the progress result of the algorithms
     * @throws IOException
     */
    public void saveProgress() throws IOException {
        String progressFolder = exFolder + "/progress/";
        Files.createDirectories(Paths.get(progressFolder));
        int i = 0;
        for (T t : progress)
            t.save(progressFolder + i++ + ".txt");
    }

    /**
     * Get the progress result represented as Floorplans
     * @return an array of floorplans
     * @throws IOException
     */
    public Floorplan[] progress() throws IOException {
        String path = exFolder + "/animation";
        Files.createDirectories(Paths.get(path));
        return floorplansProgress.stream().peek(f -> f.setSaveTo(path)).toArray(Floorplan[]::new);
    }

}
