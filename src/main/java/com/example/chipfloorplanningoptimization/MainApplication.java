package com.example.chipfloorplanningoptimization;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class MainApplication extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main-view.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 800, 700, Color.SKYBLUE);
        stage.setTitle("Floor Planning GUI");
        stage.setScene(scene);

        ((MainController)loader.getController()).start(stage);
        stage.show();
    }

}