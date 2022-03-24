package com.example.chipfloorplanningoptimization.representation;

import com.example.chipfloorplanningoptimization.abstract_structures.CModule;

import java.io.IOException;
import java.util.HashMap;

public interface Representation<T> {

    /**
     * Get all possible operations on the representation, needed to perturb it
     * @return array of runnables (functions)
     */
    Runnable[] operations();

    /**
     * Make a random operation
     */
    void perturb();

    /**
     * Pack a given floorplan into the representation RANDOMLY! (packing is only used for initialization purposes)
     * @param floorplan floorplan (positions are considered)
     */
    void pack(Floorplan floorplan);

    /**
     * Unpack the representation to a floorplan
     * @return a new floorplan with meaningful positions
     */
    Floorplan unpack();

    /**
     * @return a copy of the representation
     */
    T copy ();

    /**
     * Saves the representation in a file
     */
    void save(String path) throws IOException;

    String serialize();

    T deserialize(String text, HashMap<String, CModule> modulesByNames);

    HashMap<String, CModule> createNameModuleMap();

}
