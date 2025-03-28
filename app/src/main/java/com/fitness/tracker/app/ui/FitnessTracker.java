package com.fitness.tracker.app.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class FitnessTracker extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Login.fxml"));
        Parent loginView = loader.load();

        LoginController loginController = loader.getController();
        loginController.setStage(primaryStage);

        primaryStage.setTitle("Fitness Tracker - Login");
        primaryStage.setScene(new Scene(loginView));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
