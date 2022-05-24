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

    /**
     * This is the main method of this project.
     * It is recommended to perform experiments inside this method.
     * @return an array of Floorplans to be sent to the GUI.
     */
    public static Floorplan[] lab() throws IOException {

        BTree tree1 = createInitialSolution();
        tree1.perturb();

        BTree tree2 = createInitialSolution();
        tree2.perturb();

        Floorplan loaded = BTree.packFloorplan(getFloorplanFromFile("n30.txt", "nets30.txt")).unpack();

        return new Floorplan[] {tree1.unpack(), tree2.unpack(), loaded};
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
        NormCost<BTree> normCost = new NormCost<>(0.5, 200, initialSolution);

        // 1.2 Creating a DeadAreaCost function
        Cost<BTree> deadAreaCost = new DeadAreaCost<>(initialSolution);

        // 2.1 Creating a Simulated Annealing optimizer
        Optimizer<BTree> SA = new SimulatedAnnealing<>(100,
                0.99, 0.001, 0.99, normCost);

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
