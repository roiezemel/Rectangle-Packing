package com.example.chipfloorplanningoptimization.optimization.genetic_algorithm.mutation;

import com.example.chipfloorplanningoptimization.representation.Representation;

public class RegularMutation<T extends Representation<T>> implements Mutation<T> {

    private double mutationRate;
    private final int mutationStrength;

    public RegularMutation(double mutationRate, int mutationStrength) {
        this.mutationRate = mutationRate;
        this.mutationStrength = mutationStrength;
    }

    public RegularMutation(double mutationRate) {
        this(mutationRate, 1);
    }

    @Override
    public void mutate(T child, int time, double lowestCost) {
        if (Math.random() < mutationRate)
            for (int i = 0; i < mutationStrength; i++)
                child.perturb();
    }

    @Override
    public double getMutationRate() {
        return mutationRate;
    }

    protected void setMutationRate(double mutationRate) {
        this.mutationRate = mutationRate;
    }

    @Override
    public String getName() {
        return "Regular Mutation";
    }

    @Override
    public String paramsDescription() {
        return "=> Mutation rate: " + mutationRate;
    }
}
