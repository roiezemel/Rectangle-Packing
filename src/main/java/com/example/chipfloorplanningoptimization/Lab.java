package com.example.chipfloorplanningoptimization;

import com.example.chipfloorplanningoptimization.optimization.Cost;
import com.example.chipfloorplanningoptimization.optimization.DeadAreaCost;
import com.example.chipfloorplanningoptimization.optimization.Experiment;
import com.example.chipfloorplanningoptimization.optimization.genetic_algorithm.GeneticAlgorithm;
import com.example.chipfloorplanningoptimization.optimization.genetic_algorithm.fitness_functions.RegularFitness;
import com.example.chipfloorplanningoptimization.representation.BTree;
import com.example.chipfloorplanningoptimization.representation.Floorplan;
import com.example.chipfloorplanningoptimization.representation.Representation;

import java.io.IOException;

import static com.example.chipfloorplanningoptimization.Utils.getFloorplanFromFile;

public class Lab {

    public static Floorplan[] lab() throws IOException {
        Floorplan fileFloorplan = getFloorplanFromFile("n30.txt", "nets30.txt");
        BTree original = BTree.packFloorplan(fileFloorplan);

        Cost<BTree> cost = new DeadAreaCost<>(original);
        GeneticAlgorithm<BTree> GA = new GeneticAlgorithm<>(1000, 0.001, 500, cost, new RegularFitness<>());

//        originalFloorplan.setNet(Objects.requireNonNull(fileFloorplan));

        Experiment<BTree> ex = new Experiment<>(GA);
        BTree optimized = ex.run(original, "n30");

//        Floorplan optimizedFloorplan = optimized.unpack();
//        optimizedFloorplan.setNet(Objects.requireNonNull(fileFloorplan));

//        return new Floorplan[] {originalFloorplan, optimizedFloorplan};
        return new Floorplan[] {original.unpack(), optimized.unpack()};

    }

}
