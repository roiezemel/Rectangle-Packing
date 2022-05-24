package com.example.chipfloorplanningoptimization.optimization.simulated_annealing.cooling_schedule;

public class WaveSchedule implements CoolingSchedule {

    private double T0;
    private final double finalT;
    private final double b;
    private final double r;

    /**
     * @param finalTemperature the minimum temperature
     * @param extension how wide each wave is going to be
     */
    public WaveSchedule(double finalTemperature, double extension) {
        this(finalTemperature, extension, 1);
    }

    /**
     *
     * @param finalTemperature the minimum temperature
     * @param extension how wide each wave is going to be
     * @param reduceWaveRatio used to continuously decrease the amplitude of the waves (1 - no decrease)
     */
    public WaveSchedule(double finalTemperature, double extension, double reduceWaveRatio) {
        this.finalT = finalTemperature;
        this.b = extension;
        this.r = reduceWaveRatio;
    }

    /**
     * Get next Temperature
     * @param T current temperature
     * @param avgCostChange average cost change
     * @param time current time
     * @param avgCost average cost
     * @param lowestCost lowest cost found
     * @param rejections number of rejections
     * @return next temperature
     */
    @Override
    public double next(double T, double avgCostChange, int time, double avgCost, double lowestCost, int rejections) {
        if (time == 1)
            this.T0 = T;

        return T0 * Math.pow(r, time / T0) * (Math.cos(b * time) + 1 + finalT) / (finalT + 2);
    }

    /**
     * Get schedule's name
     * @return
     */
    @Override
    public String getName() {
        return "Wave Schedule";
    }
}
