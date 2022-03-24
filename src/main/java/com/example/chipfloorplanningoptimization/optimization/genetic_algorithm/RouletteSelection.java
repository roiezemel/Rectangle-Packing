package com.example.chipfloorplanningoptimization.optimization.genetic_algorithm;

import com.example.chipfloorplanningoptimization.representation.Representation;

import java.util.*;

public class RouletteSelection<T extends Representation<T>> implements Selection<T> {

    private final Random random = new Random();
    private NavigableMap<Double, T> rouletteWheel;
    private double total = 0;

    @Override
    public List<T> select() {
        T t1 = next();
        T t2;
        do {
            t2 = next();
        } while (t2 == t1);
        return Arrays.asList(t1, t2);
    }

    private T next() {
        double value = random.nextDouble() * total;
        return rouletteWheel.higherEntry(value).getValue();
    }

    @Override
    public void updatePopulation(ArrayList<T> population, HashMap<T, Double> fitness) {
        total = 0;
        List<T> sorted = new ArrayList<>(population);
        sorted.sort((a, b) -> (int) (fitness.get(b) - fitness.get(a)));

        rouletteWheel = new TreeMap<>();

        for (T tree : sorted) {
            if (fitness.get(tree) > 0) {
                total += fitness.get(tree);
                rouletteWheel.put(total, tree);
            }
        }
    }

    @Override
    public String getName() {
        return "Roulette Wheel Selection";
    }
}
