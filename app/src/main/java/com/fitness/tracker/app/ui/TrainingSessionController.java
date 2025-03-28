package com.fitness.tracker.app.ui;

import com.fitness.tracker.core.services.ExerciseService;
import com.fitness.tracker.core.services.TrainingSessionService;
import com.fitness.tracker.data.models.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class TrainingSessionController {
    @FXML private Label workoutLabel;
    @FXML private DatePicker sessionDatePicker;
    @FXML private ComboBox<Exercise> exerciseComboBox;
    @FXML private TextField setsField;
    @FXML private TextField weightField;
    @FXML private TextField unitField;
    @FXML private TableView<ExerciseDetail> exercisesTable;

    private Workout currentWorkout;
    private TrainingSession session;
    private ObservableList<ExerciseDetail> exerciseDetails;
    private ExerciseService exerciseService;
    private TrainingSessionService trainingSessionService;
    private Stage stage;
    private DashboardController dashboardController;
    private AppUser currentUser;

    public void initialize() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("fitness_tracker");
        EntityManager em = emf.createEntityManager();
        exerciseService = new ExerciseService(em);
        trainingSessionService = new TrainingSessionService(em);
        
        exerciseDetails = FXCollections.observableArrayList();
        setupDatePicker();
        setupExerciseComboBox();
        setupTable();
    }

    private void setupDatePicker() {
        sessionDatePicker.setValue(LocalDate.now());
    }

    private void setupExerciseComboBox() {
        List<Exercise> exercises = exerciseService.getAllExercises();
        exerciseComboBox.setItems(FXCollections.observableArrayList(exercises));
        exerciseComboBox.setConverter(new StringConverter<Exercise>() {
            @Override
            public String toString(Exercise exercise) {
                return exercise != null ? exercise.getName() : "";
            }

            @Override
            public Exercise fromString(String string) {
                return null;
            }
        });
    }

    private void setupTable() {
        TableColumn<ExerciseDetail, String> exerciseCol = new TableColumn<>("Exercise");
        exerciseCol.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getExercise().getName()));

        TableColumn<ExerciseDetail, Integer> setsCol = new TableColumn<>("Sets");
        setsCol.setCellValueFactory(new PropertyValueFactory<>("sets"));

        TableColumn<ExerciseDetail, Float> weightCol = new TableColumn<>("Weight");
        weightCol.setCellValueFactory(new PropertyValueFactory<>("value"));

        TableColumn<ExerciseDetail, String> unitCol = new TableColumn<>("Unit");
        unitCol.setCellValueFactory(new PropertyValueFactory<>("unit"));

        exercisesTable.getColumns().clear();
        exercisesTable.getColumns().addAll(exerciseCol, setsCol, weightCol, unitCol);
        exercisesTable.setItems(exerciseDetails);
    }

    public void setWorkout(Workout workout) {
        this.currentWorkout = workout;
        workoutLabel.setText("Workout: " + workout.getName());
    }

    public void setDashboardController(DashboardController controller) {
        this.dashboardController = controller;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setUser(AppUser user) {
        this.currentUser = user;
    }

    @FXML
    private void handleAddExercise() {
        try {
            Exercise selectedExercise = exerciseComboBox.getValue();
            if (selectedExercise == null) {
                showError("Please select an exercise");
                return;
            }

            int sets = Integer.parseInt(setsField.getText());
            float weight = Float.parseFloat(weightField.getText());
            String unit = unitField.getText();

            if (sets <= 0 || weight <= 0 || unit.isEmpty()) {
                showError("Please enter valid values for sets, weight, and unit");
                return;
            }

            ExerciseDetail detail = new ExerciseDetail();
            detail.setExercise(selectedExercise);
            detail.setSets(sets);
            detail.setValue(weight);
            detail.setUnit(unit);

            exerciseDetails.add(detail);
            clearExerciseInputs();
        } catch (NumberFormatException e) {
            showError("Please enter valid numbers for sets and weight");
        }
    }

    @FXML
    private void handleSave() {
        if (exerciseDetails.isEmpty()) {
            showError("Please add at least one exercise");
            return;
        }

        try {
            TrainingSession session = new TrainingSession();
            session.setDate(sessionDatePicker.getValue());
            session.setWorkout(currentWorkout);

            List<ExerciseDetail> details = new ArrayList<>(exerciseDetails);
            details.forEach(detail -> detail.setTrainingSession(session));
            session.setExerciseDetails(details);

            trainingSessionService.save(session);

            if (dashboardController != null) {
                dashboardController.refreshData();
            }

            stage.close();
        } catch (Exception e) {
            showError("Error saving training session: " + e.getMessage());
        }
    }

    @FXML
    private void handleLogSession() {
        if (exerciseDetails.isEmpty()) {
            showError("Please add at least one exercise");
            return;
        }

        if (currentWorkout == null) {
            showError("No workout selected");
            return;
        }

        if (currentUser == null) {
            showError("No user logged in");
            return;
        }

        try {
            TrainingSession session = new TrainingSession();
            session.setDate(sessionDatePicker.getValue());
            session.setWorkout(currentWorkout);

            List<ExerciseDetail> details = new ArrayList<>(exerciseDetails);
            details.forEach(detail -> detail.setTrainingSession(session));
            session.setExerciseDetails(details);

            trainingSessionService.save(session);

            if (dashboardController != null) {
                dashboardController.refreshData();
            }

            showSuccess("Training session logged successfully!");
            stage.close();
        } catch (Exception e) {
            showError("Error logging training session: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        stage.close();
    }

    private void clearExerciseInputs() {
        exerciseComboBox.setValue(null);
        setsField.clear();
        weightField.clear();
        unitField.clear();
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