module com.example.chipfloorplanningoptimization {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    exports com.example.chipfloorplanningoptimization;
    opens com.example.chipfloorplanningoptimization to javafx.fxml;
    exports com.example.chipfloorplanningoptimization.gui;
    opens com.example.chipfloorplanningoptimization.gui to javafx.fxml;
}