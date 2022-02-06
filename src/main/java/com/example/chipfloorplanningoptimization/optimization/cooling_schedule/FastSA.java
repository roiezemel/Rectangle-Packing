package com.example.chipfloorplanningoptimization.optimization.cooling_schedule;

public class FastSA implements CoolingSchedule {

    private final double c;
    private final double k;
    private double T1;

    public FastSA(double c, double k) {
        this.c = c;
        this.k = k;
    }

    @Override
    public String getName() {
        return "FastSA";
    }

    @Override
    public double next(double T, double avgCostChange, int time, double ig1, double ig2, int ig3) {
        if (time == 1)
            T1 = T;
        if (time < k)
            return T1 * avgCostChange / time / c;
        return T1 * avgCostChange / time;
    }
}
