package com.example.chipfloorplanningoptimization.optimization;
import com.example.chipfloorplanningoptimization.representation.Representation;

import java.util.Random;
import java.lang.Math;

public class SimulatedAnnealing {

    private double temperature;
    private double final_temperature;

    private int iterations;
    private double tempChange;
    public SimulatedAnnealing() {}

    public SimulatedAnnealing(double temperature,double final_temperature, double tempChange, int iterations) {
        this.temperature = temperature;
        this.final_temperature = final_temperature;
        this.tempChange = tempChange;
        this.iterations = iterations;
    }
/*
* do the change in the temperature
*/
    private void temperatureProtocol() {
        this.temperature = temperature - tempChange;
    }

    /**
     * the loop that change the floorplan untill it's find a selution
     * @param floorplan represntation
     * @return the represntation solution
     */
    public Representation optimization (Representation floorplan){
        Representation temp;
        Cost cost = new Cost();
        Random rand = new Random();
        while (temperature > final_temperature) {
            for (int i = 0; i < iterations; i++) {
                temp.copy(floorplan);
                floorplan.operations()[rand.nextInt(3)].run();
                double rateChange = cost.evaluate(floorplan);
                double rateBefore = cost.evaluate(temp);
                if (rateChange >= rateBefore && rand.nextDouble() > Math.exp(-(rateChange - rateBefore)/temperature))/* it's backward - if it's true the change is bad*/
                {
                  floorplan.copy(temp);
                }
            }
            temperatureProtocol();
        }
        return floorplan;
    }
}

