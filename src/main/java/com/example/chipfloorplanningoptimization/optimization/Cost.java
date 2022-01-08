package com.example.chipfloorplanningoptimization.optimization;

import com.example.chipfloorplanningoptimization.representation.Representation;

public class Cost {


    private double Anorm;
    private double Wnorm;
    private double alpha; // 0 < alpha < 1

    public Cost(double alpha) {
        if (alpha > 1 || alpha < 0)
            throw new IllegalArgumentException("0 < alpha < 1!");
        // TODO: calculate Anorm and Wnorm
    }

    public double evaluate(Representation r) {
        return alpha * r.unpack().area(); // alpha * (A / Anorm) + (1 - alpha) * (W / Wnorm)
    }

}
