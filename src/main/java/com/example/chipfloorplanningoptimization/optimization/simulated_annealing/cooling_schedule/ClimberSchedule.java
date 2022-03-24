package com.example.chipfloorplanningoptimization.optimization.simulated_annealing.cooling_schedule;

public class ClimberSchedule implements CoolingSchedule {

    private static final int bigNumber = 1000000000;
    private double prevLowestCost;
    private double r;
    private int jumpTime;
    private int prevTime;

    /**
     * @param reduceRatio the ratio by which the temperature is reduced each iteration
     * @param plainTravelTime how much time should pass with no improvement before jumping to a higher temperature
     */
    public ClimberSchedule(double reduceRatio, int plainTravelTime) {
        this.r = reduceRatio;
        this.jumpTime = plainTravelTime;
    }

    @Override
    public double next(double T, double avgCostChange, int time, double avgCost, double lowestCost, int rejections) {
        if (prevTime > time) // in case optimization restarted
            prevLowestCost = bigNumber;

        if (prevLowestCost > lowestCost) {
            prevTime = time;
            prevLowestCost = lowestCost;
        }
        else if (time - prevTime >= jumpTime) {
            prevLowestCost = bigNumber; // new cooling procedure
            return T / Math.pow(r, jumpTime);
        }

        return r * T;
    }

    @Override
    public String getName() {
        return "Climber Schedule";
    }
}
