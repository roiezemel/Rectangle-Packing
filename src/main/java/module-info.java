module com.example.chipfloorplanningoptimization {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;


    opens com.example.chipfloorplanningoptimization to javafx.fxml;
    exports com.example.chipfloorplanningoptimization;
}