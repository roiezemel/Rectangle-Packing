module com.example.chipfloorplanningoptimization {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires org.knowm.xchart;
    requires java.desktop;

    exports com.example.chipfloorplanningoptimization;
    opens com.example.chipfloorplanningoptimization to javafx.fxml;
    exports com.example.chipfloorplanningoptimization.gui;
    opens com.example.chipfloorplanningoptimization.gui to javafx.fxml;
    exports com.example.chipfloorplanningoptimization.optimization;
    opens com.example.chipfloorplanningoptimization.optimization to javafx.fxml;
    exports com.example.chipfloorplanningoptimization.optimization.simulated_annealing.cooling_schedule;
    opens com.example.chipfloorplanningoptimization.optimization.simulated_annealing.cooling_schedule to javafx.fxml;
    exports com.example.chipfloorplanningoptimization.optimization.simulated_annealing;
    opens com.example.chipfloorplanningoptimization.optimization.simulated_annealing to javafx.fxml;
    exports com.example.chipfloorplanningoptimization.representation;
    opens com.example.chipfloorplanningoptimization.representation to javafx.fxml;
    exports com.example.chipfloorplanningoptimization.optimization.costs;
    opens com.example.chipfloorplanningoptimization.optimization.costs to javafx.fxml;
}