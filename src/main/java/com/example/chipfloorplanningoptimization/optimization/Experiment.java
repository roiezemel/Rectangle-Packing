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

    public Experiment() {}

    public Experiment(Optimizer<T> op) {
        setOptimizer(op);
    }

    @SafeVarargs
    public Experiment(Optimizer<T>... ops) {
        setOptimizers(ops);
    }

    public void setOptimizer(Optimizer<T> op) {
        this.ops = new LinkedList<>() {{add(op);}};
    }

    @SafeVarargs
    public final void setOptimizers(Optimizer<T>... ops) {
        this.ops = new LinkedList<>(Arrays.asList(ops));
    }

    public T run(T original, String inputName) throws IOException {
        String experimentFolder = ops.stream()
                .map(Optimizer::getName)
                .collect(Collectors.joining("-")) + "/" + LocalDate.now();
        return run(original, inputName, experimentFolder);
    }

    public T run(T original, String inputName, String experimentFolder) throws IOException {
        exFolder = exDirectoryPath(experimentFolder);
        T result = original;
        progress = new LinkedList<>() {{add(original.copy());}};
        floorplansProgress = new LinkedList<>() {{add(original.unpack());}};
        for (Optimizer<T> op : ops) {
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

    public void saveProgress() throws IOException {
        String progressFolder = exFolder + "/progress/";
        Files.createDirectories(Paths.get(progressFolder));
        int i = 0;
        for (T t : progress)
            t.save(progressFolder + i++ + ".txt");
    }

    public Floorplan[] progress() throws IOException {
        String path = exFolder + "/animation";
        Files.createDirectories(Paths.get(path));
        return floorplansProgress.stream().peek(f -> f.setSaveTo(path)).toArray(Floorplan[]::new);
    }

}
