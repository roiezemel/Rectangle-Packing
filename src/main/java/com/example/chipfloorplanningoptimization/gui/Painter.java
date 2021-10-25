package com.example.chipfloorplanningoptimization.gui;

public interface Painter {

    void draw(int index);
    default void onToggleWires() {}

}
