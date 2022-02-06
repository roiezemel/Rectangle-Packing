module com.example.chipfloorplanningoptimization {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires org.knowm.xchart;

    exports com.example.chipfloorplanningoptimization;
    opens com.example.chipfloorplanningoptimization to javafx.fxml;
    exports com.example.chipfloorplanningoptimization.gui;
    opens com.example.chipfloorplanningoptimization.gui to javafx.fxml;
    exports com.example.chipfloorplanningoptimization.optimization;
    opens com.example.chipfloorplanningoptimization.optimization to javafx.fxml;
    exports com.example.chipfloorplanningoptimization.optimization.cooling_schedule;
    opens com.example.chipfloorplanningoptimization.optimization.cooling_schedule to javafx.fxml;
}