package com.fitness.tracker.app.ui;

import com.fitness.tracker.core.services.ServiceFactory;
import com.fitness.tracker.core.services.WorkoutService;
import com.fitness.tracker.data.models.Workout;
import com.fitness.tracker.data.models.AppUser;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class WorkoutCreationController {
    @FXML
    private TextField workoutNameField;
    
    private WorkoutService workoutService;
    private AppUser currentUser;
    private Stage stage;
    
    public void initialize() {
        workoutService = ServiceFactory.createWorkoutService();
    }
    
    public void setUser(AppUser user) {
        this.currentUser = user;
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
            Workout workout = new Workout();
            workout.setName(name);
            workout.setUser(currentUser);
            
            workoutService.create(workout);
            stage.close();
        } catch (Exception e) {
            showError("Error creating workout: " + e.getMessage());
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
