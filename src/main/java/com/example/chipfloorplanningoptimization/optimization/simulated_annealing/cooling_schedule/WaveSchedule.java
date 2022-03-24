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

    public WaveSchedule(double finalTemperature, double extension, double reduceWaveRatio) {
        this.finalT = finalTemperature;
        this.b = extension;
        this.r = reduceWaveRatio;
    }

    @Override
    public double next(double T, double avgCostChange, int time, double avgCost, double lowestCost, int rejections) {
        if (time == 1)
            this.T0 = T;

        return T0 * Math.pow(r, time / T0) * (Math.cos(b * time) + 1 + finalT) / (finalT + 2);
    }

    @Override
    public String getName() {
        return "Wave Schedule";
    }
}
