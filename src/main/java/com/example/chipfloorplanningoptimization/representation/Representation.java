package com.example.chipfloorplanningoptimization.representation;

public interface Representation {

    /**
     * Get all possible operations on the representation, needed to perturb it
     * @return array of runnables (functions)
     */
    Runnable[] operations();

    /**
     * Pack a given floorplan into the representation
     * @param floorplan floorplan (positions are considered)
     */
    void pack(Floorplan floorplan);

    /**
     * Unpack the representation to a floorplan
     * @return a new floorplan with meaningful positions
     */
    Floorplan unpack();

}
