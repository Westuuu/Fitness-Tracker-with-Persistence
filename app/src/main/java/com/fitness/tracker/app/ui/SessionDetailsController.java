package com.fitness.tracker.app.ui;

import com.fitness.tracker.core.services.ServiceFactory;
import com.fitness.tracker.core.services.TrainingSessionService;
import com.fitness.tracker.data.models.TrainingSession;
import com.fitness.tracker.data.models.ExerciseDetail;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;

public class SessionDetailsController {
    @FXML private Label workoutLabel;
    @FXML private DatePicker sessionDatePicker;
    @FXML private TableView<ExerciseDetail> exercisesTable;

    private TrainingSessionService trainingSessionService;
    private TrainingSession session;

    public void initialize() {
        trainingSessionService = ServiceFactory.createTrainingSessionService();
        setupTable();
    }

    private void setupTable() {
        exercisesTable.setEditable(true);
        
        TableColumn<ExerciseDetail, String> exerciseCol = new TableColumn<>("Exercise");
        exerciseCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getExercise().getName()));
        exerciseCol.setEditable(false);

        TableColumn<ExerciseDetail, String> setsCol = new TableColumn<>("Sets");
        setsCol.setCellValueFactory(data ->
                new SimpleStringProperty(String.valueOf(data.getValue().getSets())));
        setsCol.setCellFactory(TextFieldTableCell.forTableColumn());
        setsCol.setOnEditCommit(event -> {
            ExerciseDetail detail = event.getRowValue();
            try {
                int sets = Integer.parseInt(event.getNewValue());
                if (sets > 0) {
                    detail.setSets(sets);
                } else {
                    throw new IllegalArgumentException("Sets must be greater than 0");
                }
            } catch (NumberFormatException e) {
                showError("Please enter a valid number for sets");
                exercisesTable.refresh();
            } catch (IllegalArgumentException e) {
                showError(e.getMessage());
                exercisesTable.refresh();
            }
        });

        TableColumn<ExerciseDetail, String> weightCol = new TableColumn<>("Weight");
        weightCol.setCellValueFactory(data ->
                new SimpleStringProperty(String.valueOf(data.getValue().getValue())));
        weightCol.setCellFactory(TextFieldTableCell.forTableColumn());
        weightCol.setOnEditCommit(event -> {
            ExerciseDetail detail = event.getRowValue();
            try {
                float weight = Float.parseFloat(event.getNewValue());
                if (weight > 0) {
                    detail.setValue(weight);
                } else {
                    throw new IllegalArgumentException("Weight must be greater than 0");
                }
            } catch (NumberFormatException e) {
                showError("Please enter a valid number for weight");
                exercisesTable.refresh();
            } catch (IllegalArgumentException e) {
                showError(e.getMessage());
                exercisesTable.refresh();
            }
        });

        TableColumn<ExerciseDetail, String> unitCol = new TableColumn<>("Unit");
        unitCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getUnit()));
        unitCol.setCellFactory(TextFieldTableCell.forTableColumn());
        unitCol.setOnEditCommit(event -> {
            ExerciseDetail detail = event.getRowValue();
            String unit = event.getNewValue().trim();
            if (!unit.isEmpty()) {
                detail.setUnit(unit);
            } else {
                showError("Unit cannot be empty");
                exercisesTable.refresh();
            }
        });

        exercisesTable.getColumns().clear();
        exercisesTable.getColumns().addAll(exerciseCol, setsCol, weightCol, unitCol);
    }

    public void setSession(TrainingSession session) {
        this.session = session;
        workoutLabel.setText("Session - " + session.getWorkout().getName());
        sessionDatePicker.setValue(session.getDate());
        exercisesTable.setItems(FXCollections.observableArrayList(
                session.getExerciseDetails()));
    }

    @FXML
    private void handleSaveChanges() {
        try {
            session.setDate(sessionDatePicker.getValue());
            trainingSessionService.edit(session);
            Stage currentStage = (Stage) workoutLabel.getScene().getWindow();
            currentStage.close();
        } catch (Exception e) {
            showError("Error saving changes: " + e.getMessage());
        }
    }

    @FXML
    private void handleClose() {
        Stage currentStage = (Stage) workoutLabel.getScene().getWindow();
        currentStage.close();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}