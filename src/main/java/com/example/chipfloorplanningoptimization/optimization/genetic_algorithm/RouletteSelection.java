package com.example.chipfloorplanningoptimization.optimization.genetic_algorithm;

import com.example.chipfloorplanningoptimization.optimization.Cost;
import com.example.chipfloorplanningoptimization.representation.BTree;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RouletteSelection implements Selection {

    private final Random random = new Random();
    private NavigableMap<Double, BTree> rouletteWheel;
    private double total = 0;

    @Override
    public BTree[] select() {
        BTree t1 = next();
        BTree t2;
        do {
            t2 = next();
        } while (t2 == t1);
        return new BTree[] {t1, t2};
    }

    private BTree next() {
        double value = random.nextDouble() * total;
        return rouletteWheel.higherEntry(value).getValue();
    }

    @Override
    public void updatePopulation(ArrayList<BTree> population, HashMap<BTree, Double> fitness) {
        total = 0;
        List<BTree> sorted = new ArrayList<>(population);
        sorted.sort((a, b) -> (int) (fitness.get(b) - fitness.get(a)));

        rouletteWheel = new TreeMap<>();

        for (BTree tree : sorted) {
            if (fitness.get(tree) > 0) {
                total += fitness.get(tree);
                rouletteWheel.put(total, tree);
            }
        }
    }
}
