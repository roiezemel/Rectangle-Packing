package com.example.chipfloorplanningoptimization;

import com.example.chipfloorplanningoptimization.optimization.*;
import com.example.chipfloorplanningoptimization.optimization.costs.Cost;
import com.example.chipfloorplanningoptimization.optimization.costs.DeadAreaCost;
import com.example.chipfloorplanningoptimization.optimization.costs.NormCost;
import com.example.chipfloorplanningoptimization.optimization.costs.SquareCost;
import com.example.chipfloorplanningoptimization.optimization.genetic_algorithm.GeneticAlgorithm;
import com.example.chipfloorplanningoptimization.optimization.genetic_algorithm.fitness_functions.ReciprocalFitness;
import com.example.chipfloorplanningoptimization.optimization.genetic_algorithm.mutation.AdaptingMutation;
import com.example.chipfloorplanningoptimization.optimization.genetic_algorithm.mutation.Mutation;
import com.example.chipfloorplanningoptimization.optimization.genetic_algorithm.mutation.RegularMutation;
import com.example.chipfloorplanningoptimization.optimization.simulated_annealing.SimulatedAnnealing;
import com.example.chipfloorplanningoptimization.representation.BTree;
import com.example.chipfloorplanningoptimization.representation.Floorplan;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.example.chipfloorplanningoptimization.Utils.*;

public class Lab {

    public static Floorplan[] lab() throws IOException {
        Floorplan fileFloorplan = getFloorplanFromFile("n30.txt", "nets30.txt");
        BTree original = BTree.packFloorplan(fileFloorplan);
//        BTree original = createInitialSolution();

        Cost<BTree> squareCost = new SquareCost<>(0.6, 200);
        Mutation<BTree> mutation = new AdaptingMutation<>(0.001, 0.001, 2);
        GeneticAlgorithm<BTree> GA = new GeneticAlgorithm<>(5000,
                4000, squareCost, new ReciprocalFitness<>(), mutation);

        NormCost<BTree> normCost = new SquareCost<>(0.6, 200);
        SimulatedAnnealing<BTree> SA = new SimulatedAnnealing<>(100000,
                0.999,0.000001,0.9999, normCost);
//        SA.setDefaultCoolingSchedule(0.9);

        Cost<BTree> deadAreaCost = new DeadAreaCost<>();
        Cost<BTree> dsCost = new SquareCost<>(0.6, 200);
        DumbSearch<BTree> DS1 = new DumbSearch<>(dsCost);

        DumbSearch<BTree> DS2 = new DumbSearch<>(deadAreaCost, false);

        Experiment<BTree> ex = new Experiment<>(GA, DS1, DS2);

        BTree optimized = ex.run(original, "n30");

        return ex.progress();
//        return getProgress("src/main/data/Genetic Algorithm-Dumb Search/2022-03-26/ex #0/progress");
//        return new Floorplan[] {loadSolution("src/main/data/Genetic Algorithm/2022-03-25/ex #1/n100-result.txt")};
    }

    private static void example1() {
        /*
        *    Example 1:
        *  - Loading a Floorplan from resources
        *  - Creating a BTree representation
        *  - Unpacking a BTree to a Floorplan containing the network information
        * */

        // 1. Loading a floorplan from resources folder
        Floorplan fileFloorplan = getFloorplanFromFile("n50.txt", "nets50.txt");

        // 2. Randomly packing the Floorplan into a BTree
        BTree tree = BTree.packFloorplan(fileFloorplan);

        // 3. Unpacking a BTree representation to a Floorplan, and setting its network information
        Floorplan treeFloorplan = tree.unpack(); // Now the Floorplan doesn't have the network information
        treeFloorplan.setNet(Objects.requireNonNull(fileFloorplan)); // And now it does
    }

    private static void example2() throws IOException {
        /*
         *    Example 2:
         *  - Creating a cost function
         *  - Setting up an optimizer
         *  - Running an experiment
         * */

        // 0. Creating an initial solution
        BTree initialSolution = createInitialSolution();

        // 1.1 Creating a NormCost function
        Cost<BTree> normCost = new NormCost<>(0.5, 200, initialSolution);

        // 1.2 Creating a DeadAreaCost function
        Cost<BTree> deadAreaCost = new DeadAreaCost<>(initialSolution);

        // 2.1 Creating a Simulated Annealing optimizer
        Optimizer<BTree> SA = new SimulatedAnnealing<>(100,
                0.99, 0.001, 0.99, (NormCost<BTree>) normCost);

        // 2.2 Creating a Genetic Algorithm optimizer
        GeneticAlgorithm<BTree> GA = new GeneticAlgorithm<>(10000,
                500, deadAreaCost, new ReciprocalFitness<>(), new RegularMutation<>(0.001));

        // 3. Creating a new Experiment and running it for each optimizer
        Experiment<BTree> ex = new Experiment<>(SA);
        BTree optimizedBySA = ex.run(initialSolution, "custom");
        ex.setOptimizer(GA);
        BTree optimizedByGA = ex.run(initialSolution, "custom");
    }

}
