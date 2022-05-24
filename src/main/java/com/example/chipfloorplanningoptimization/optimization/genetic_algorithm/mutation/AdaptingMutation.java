package com.example.chipfloorplanningoptimization.optimization.genetic_algorithm.mutation;

import com.example.chipfloorplanningoptimization.representation.Representation;

public class AdaptingMutation<T extends Representation<T>> extends RegularMutation<T> {

    private final double originalMutationRate;
    private final double adaptationRate;

    private double prevLowestCost = -1;
    private double prevTime = 0;

    /**
     * Initialize Adapting Mutation
     * @param mutationRate mutation rate (initial)
     * @param adaptationRate adaptation rate
     * @param mutationStrength strength
     */
    public AdaptingMutation(double mutationRate, double adaptationRate, int mutationStrength) {
        super(mutationRate, mutationStrength);
        this.originalMutationRate = mutationRate;
        this.adaptationRate = adaptationRate;
    }

    /**
     * Initialize Adapting Mutation
     * @param mutationRate mutation rate (initial)
     * @param adaptationRate adaptation rate
     */
    public AdaptingMutation(double mutationRate, double adaptationRate) {
        this(mutationRate, adaptationRate, 1);
    }

    /**
     * Perform mutation
     * @param child a child
     * @param time current time
     * @param lowestCost lowest cost found
     */
    @Override
    public void mutate(T child, int time, double lowestCost) {
        if (time == prevTime) // For any child who's not the first of the generation
            super.mutate(child, time, lowestCost);

        if (time < prevTime) {
            prevTime =  time;
            prevLowestCost = -1;
            setMutationRate(originalMutationRate);
            mutate(child, time, lowestCost);
            return;
        }

        if (lowestCost == prevLowestCost) // As long as there is no improvement, increase mutation rate
            inc(time);
        else
            setMutationRate(originalMutationRate); // If there has been even the slightest improvement, return to the original mutation rate

        prevLowestCost = lowestCost;
        prevTime = time;

        super.mutate(child, time, lowestCost);
    }

    /**
     * Increase mutation rate according to the current time
     * @param time
     */
    private void inc(int time) {
        setMutationRate(getMutationRate() * Math.pow(1 + adaptationRate, time - prevTime));
    }

    /**
     * Get mutation name
     * @return
     */
    @Override
    public String getName() {
        return "Adapting Mutation";
    }

    /**
     * Get description of parameters
     * @return
     */
    @Override
    public String paramsDescription() {
        return "=> Mutation rate: " + originalMutationRate + "\n=> Adaptation rate: " + adaptationRate;
    }

}
