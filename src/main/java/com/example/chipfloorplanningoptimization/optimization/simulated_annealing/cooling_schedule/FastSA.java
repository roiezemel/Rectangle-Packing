package com.example.chipfloorplanningoptimization.optimization.simulated_annealing.cooling_schedule;

public class FastSA implements CoolingSchedule {

    private final double c;
    private final double k;
    private double T1;

    /**
     * Initialize a FastSA schedule
     * @param c controls the rate of decrease in the second stage
     * @param k how much time to perform the second stage - fast decrease
     */
    public FastSA(double c, double k) {
        this.c = c;
        this.k = k;
    }

    /**
     * Get schedule's name
     * @return
     */
    @Override
    public String getName() {
        return "FastSA";
    }

    /**
     * Get next Temperature
     * @param T current temperature
     * @param avgCostChange average cost change
     * @param time current time
     * @return next temperature
     * The rest of the parameters are ignored
     */
    @Override
    public double next(double T, double avgCostChange, int time, double ig1, double ig2, int ig3) {
        if (time == 1)
            T1 = T;
        if (time < k)
            return T1 * avgCostChange / time / c;
        return T1 * avgCostChange / time;
    }
}
