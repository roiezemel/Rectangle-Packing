package com.example.chipfloorplanningoptimization.optimization.genetic_algorithm.selection;

import com.example.chipfloorplanningoptimization.representation.Representation;

import java.util.*;

public class RouletteSelection<T extends Representation<T>> implements Selection<T> {

    private final Random random = new Random();
    private NavigableMap<Double, T> rouletteWheel;
    private double total = 0;

    /**
     * Perform selection, pick two parents
     * @return
     */
    @Override
    public List<T> select() {
        T t1 = next();
        T t2;
        do {
            t2 = next();
        } while (t2 == t1);
        return Arrays.asList(t1, t2);
    }

    /**
     * Pick one parent
     * @return
     */
    private T next() {
        double value = random.nextDouble() * total;
        return rouletteWheel.higherEntry(value).getValue();
    }

    /**
     * Update the population and their fitness values
     * @param population population
     * @param fitness a solution-to-fitness map
     */
    @Override
    public void updatePopulation(ArrayList<T> population, HashMap<T, Double> fitness) {
        total = 0;
        List<T> sorted = new ArrayList<>(population);

        sorted.sort(Comparator.comparingDouble(fitness::get).reversed());

        rouletteWheel = new TreeMap<>();

        for (T tree : sorted) {
            if (fitness.get(tree) > 0) {
                total += fitness.get(tree);
                rouletteWheel.put(total, tree);
            }
        }
    }

    /**
     * Get name of selection
     * @return
     */
    @Override
    public String getName() {
        return "Roulette Wheel Selection";
    }
}
