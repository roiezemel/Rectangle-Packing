package com.example.chipfloorplanningoptimization.optimization.genetic_algorithm.mutation;

import com.example.chipfloorplanningoptimization.representation.Representation;

public class RegularMutation<T extends Representation<T>> implements Mutation<T> {

    private double mutationRate;
    private final int mutationStrength;

    /**
     * Initialize mutation
     * @param mutationRate mutation rate
     * @param mutationStrength mutation strength
     */
    public RegularMutation(double mutationRate, int mutationStrength) {
        this.mutationRate = mutationRate;
        this.mutationStrength = mutationStrength;
    }

    /**
     * Initialize mutation
     * @param mutationRate mutation rate
     */
    public RegularMutation(double mutationRate) {
        this(mutationRate, 1);
    }

    /**
     * Perform mutation
     * @param child a child
     * @param time current time
     * @param lowestCost lowest cost found
     */
    @Override
    public void mutate(T child, int time, double lowestCost) {
        if (Math.random() < mutationRate)
            for (int i = 0; i < mutationStrength; i++)
                child.perturb();
    }

    /**
     * Get mutation rate
     * @return
     */
    @Override
    public double getMutationRate() {
        return mutationRate;
    }

    /**
     * Set mutation rate
     * @param mutationRate
     */
    protected void setMutationRate(double mutationRate) {
        this.mutationRate = mutationRate;
    }

    /**
     * Get mutation name
     * @return
     */
    @Override
    public String getName() {
        return "Regular Mutation";
    }

    /**
     * Get description of parameters
     * @return
     */
    @Override
    public String paramsDescription() {
        return "=> Mutation rate: " + mutationRate;
    }
}
