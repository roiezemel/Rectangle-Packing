package com.example.chipfloorplanningoptimization.optimization;
import com.example.chipfloorplanningoptimization.representation.BTree;
import com.example.chipfloorplanningoptimization.representation.Representation;

import java.util.Random;
import java.lang.Math;

public class SimulatedAnnealing implements Optimizer{

    private double temperature;
    private final double finalTemperature;
    private final int iterations;
    private final double tempChange;

    public SimulatedAnnealing(double temperature,double finalTemperature, double tempChange, int iterations) {
        this.temperature = temperature;
        this.finalTemperature = finalTemperature;
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
     * @param initialSolution represntation
     * @return the represntation solution
     */
    @Override
    public <T extends Representation<T>> T optimize(T initialSolution) {
        T temp;
        Cost cost = new Cost(1, 20, initialSolution.copy());
        Random rand = new Random();
        while (temperature > finalTemperature) {
            for (int i = 0; i < iterations; i++) {
                temp = initialSolution.copy();
                initialSolution.operations()[rand.nextInt(3)].run();
                double rateChange = cost.evaluate(initialSolution);
                double rateBefore = cost.evaluate(temp);
                if (rateChange >= rateBefore && rand.nextDouble() > Math.exp(-(rateChange - rateBefore)/temperature)) {/* it's backward - if it's true the change is bad*/
                    initialSolution = temp;
                }
            }
            temperatureProtocol();
        }
        return initialSolution;
    }
}

