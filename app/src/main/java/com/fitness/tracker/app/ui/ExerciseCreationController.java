package com.fitness.tracker.app.ui;

import com.fitness.tracker.core.services.ExerciseService;
import com.fitness.tracker.data.models.Exercise;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class ExerciseCreationController {
    @FXML private TextField exerciseNameField;
    @FXML private TextField exerciseTypeField;

    private ExerciseService exerciseService;
    private Stage stage;
    private DashboardController dashboardController;
    private Exercise selectedExercise;

    public void initialize() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("fitness_tracker");
        EntityManager em = emf.createEntityManager();
        exerciseService = new ExerciseService(em);
    }

    public void setDashboardController(DashboardController controller) {
        this.dashboardController = controller;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setExercise(Exercise exercise) {
        this.selectedExercise = exercise;
        if (exercise != null) {
            exerciseNameField.setText(exercise.getName());
            exerciseTypeField.setText(exercise.getType());
        }
    }

    @FXML
    private void handleSaveExercise() {
        String name = exerciseNameField.getText().trim();
        String type = exerciseTypeField.getText().trim();

        if (name.isEmpty() || type.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Invalid Input");
            alert.setContentText("Please fill in all fields.");
            alert.showAndWait();
            return;
        }

        try {
            Exercise exercise = selectedExercise != null ? selectedExercise : new Exercise();
            exercise.setName(name);
            exercise.setType(type);

            exerciseService.save(exercise);

            if (dashboardController != null) {
                dashboardController.refreshExercises();
            }

            stage.close();
        } catch (Exception e) {
            showError("Error creating exercise: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        stage.close();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}