package com.fitness.tracker.app.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class MainController {
    @FXML
    private Label mainLabel;

    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void showWorkouts() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/MainDashboard.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Workouts");
        } catch (Exception e) {
            showError("Error loading workouts view: " + e.getMessage());
        }
    }

    @FXML
    private void showSessions() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/SessionsView.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Training Sessions");
        } catch (Exception e) {
            showError("Error loading sessions view: " + e.getMessage());
        }
    }

    @FXML
    private void showGoals() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/GoalSetting.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Fitness Goals");
        } catch (Exception e) {
            showError("Error loading goals view: " + e.getMessage());
        }
    }

    @FXML
    private void showProgress() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ProgressTracking.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Progress Tracking");
        } catch (Exception e) {
            showError("Error loading progress view: " + e.getMessage());
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}