package com.example.chipfloorplanningoptimization;

public interface Painter {

    void draw(int index);
    default void onToggleWires() {}

}
