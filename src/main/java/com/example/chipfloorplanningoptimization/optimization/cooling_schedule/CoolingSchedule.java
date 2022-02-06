package com.example.chipfloorplanningoptimization.optimization.cooling_schedule;

@FunctionalInterface
public interface CoolingSchedule {

    double next(double T, double avgCostChange, int time, double avgCost, double lowestCost, int rejections);

    default String getName() {
        return "Default";
    }

}
