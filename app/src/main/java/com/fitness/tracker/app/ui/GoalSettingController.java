package com.fitness.tracker.app.ui;

import com.fitness.tracker.core.services.ExerciseService;
import com.fitness.tracker.core.services.ServiceFactory;
import com.fitness.tracker.core.services.UserExerciseService;
import com.fitness.tracker.data.models.Exercise;
import com.fitness.tracker.data.models.UserExercise;
import com.fitness.tracker.data.models.AppUser;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import java.time.LocalDate;

public class GoalSettingController {
    @FXML private ComboBox<Exercise> exerciseTypeComboBox;
    @FXML private ComboBox<String> goalTypeComboBox;
    @FXML private TextField targetValueField;
    @FXML private DatePicker deadlinePicker;

    private ExerciseService exerciseService;
    private UserExerciseService userExerciseService;
    private AppUser currentUser;
    private Stage stage;
    private DashboardController dashboardController;
    private UserExercise existingGoal;

    public void initialize() {
        exerciseService = ServiceFactory.createExerciseService();
        userExerciseService = ServiceFactory.createUserExerciseService();
        setupComboBoxes();
        deadlinePicker.setValue(LocalDate.now().plusMonths(1));
    }

    private void setupComboBoxes() {
        exerciseTypeComboBox.setItems(FXCollections.observableArrayList(
                exerciseService.getAllExercises()));
        exerciseTypeComboBox.setConverter(new StringConverter<Exercise>() {
            @Override
            public String toString(Exercise exercise) {
                return exercise != null ? exercise.getName() : "";
            }

            @Override
            public Exercise fromString(String string) {
                return null;
            }
        });

        goalTypeComboBox.setItems(FXCollections.observableArrayList(
                "Weight"));
    }

    public void setUser(AppUser user) {
        this.currentUser = user;
    }

    public void setGoal(UserExercise goal) {
        this.existingGoal = goal;
        exerciseTypeComboBox.setValue(goal.getExercise());
        targetValueField.setText(String.valueOf(goal.getGoal()));
        deadlinePicker.setValue(goal.getGoalDeadline());
    }

    public void setDashboardController(DashboardController controller) {
        this.dashboardController = controller;
    }

    @FXML
    private void handleCreateGoal() {
        Exercise selectedExercise = exerciseTypeComboBox.getValue();
        String targetValue = targetValueField.getText();
        LocalDate deadline = deadlinePicker.getValue();

        if (selectedExercise == null || targetValue.isEmpty() || deadline == null) {
            showError("Please fill in all fields");
            return;
        }

        if (deadline.isBefore(LocalDate.now())) {
            showError("Deadline cannot be in the past");
            return;
        }

        try {
            float goal = Float.parseFloat(targetValue);

            UserExercise userExercise = existingGoal != null ?
                    existingGoal : new UserExercise();
            userExercise.setUser(currentUser);
            userExercise.setExercise(selectedExercise);
            userExercise.setGoal(goal);
            userExercise.setGoalDeadline(deadline);

            if (existingGoal != null) {
                userExerciseService.edit(userExercise);
            } else {
                userExerciseService.create(userExercise);
            }

            if (stage == null) {
                stage = (Stage) exerciseTypeComboBox.getScene().getWindow();
            }
            
            if (dashboardController != null) {
                dashboardController.refreshGoals();
            }
            
            if (stage != null) {
                stage.close();
            }
        } catch (NumberFormatException e) {
            showError("Please enter a valid number for the target value");
        } catch (Exception e) {
            showError("Error saving goal: " + e.getMessage());
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}