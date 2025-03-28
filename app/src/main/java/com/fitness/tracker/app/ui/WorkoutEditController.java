package com.fitness.tracker.app.ui;

import com.fitness.tracker.core.services.ServiceFactory;
import com.fitness.tracker.core.services.WorkoutService;
import com.fitness.tracker.data.models.Workout;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class WorkoutEditController {
    @FXML
    private TextField workoutNameField;
    
    private WorkoutService workoutService;
    private Workout workout;
    private Stage stage;
    private DashboardController dashboardController;
    
    public void initialize() {
        workoutService = ServiceFactory.createWorkoutService();
    }
    
    public void setWorkout(Workout workout) {
        this.workout = workout;
        workoutNameField.setText(workout.getName());
    }
    
    public void setDashboardController(DashboardController controller) {
        this.dashboardController = controller;
    }
    
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
    @FXML
    private void handleSave() {
        String name = workoutNameField.getText().trim();
        
        if (name.isEmpty()) {
            showError("Workout name is required");
            return;
        }
        
        try {
            workout.setName(name);
            workoutService.edit(workout);
            
            if (dashboardController != null) {
                dashboardController.refreshData();
            }
            
            stage.close();
        } catch (Exception e) {
            showError("Error updating workout: " + e.getMessage());
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
}