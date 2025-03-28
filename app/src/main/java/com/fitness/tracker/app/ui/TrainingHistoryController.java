package com.fitness.tracker.app.ui;

import com.fitness.tracker.core.services.ServiceFactory;
import com.fitness.tracker.core.services.TrainingSessionService;
import com.fitness.tracker.data.models.ExerciseDetail;
import com.fitness.tracker.data.models.TrainingSession;
import com.fitness.tracker.data.models.Workout;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.converter.IntegerStringConverter;

import java.time.LocalDate;

public class TrainingHistoryController {
    @FXML private Label workoutNameLabel;
    @FXML private TableView<TrainingSession> sessionsTable;
    
    private TrainingSessionService trainingSessionService;
    private Workout workout;
    private Stage stage;
    
    public void initialize() {
        trainingSessionService = ServiceFactory.createTrainingSessionService();
        setupTable();
    }
    
    private void setupTable() {
        sessionsTable.setEditable(true);
        
        TableColumn<TrainingSession, String> dateCol = new TableColumn<>("Date");
        dateCol.setPrefWidth(120);
        dateCol.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getDate().toString()));
        dateCol.setCellFactory(TextFieldTableCell.forTableColumn());
        dateCol.setOnEditCommit(event -> {
            TrainingSession session = event.getRowValue();
            try {
                LocalDate newDate = LocalDate.parse(event.getNewValue());
                session.setDate(newDate);
                trainingSessionService.edit(session);
            } catch (Exception e) {
                showError("Invalid date format. Please use YYYY-MM-DD");
                loadSessions();
            }
        });
        
        TableColumn<TrainingSession, String> exercisesCol = new TableColumn<>("Exercises");
        exercisesCol.setPrefWidth(280);
        exercisesCol.setCellValueFactory(data -> {
            StringBuilder details = new StringBuilder();
            for (ExerciseDetail detail : data.getValue().getExerciseDetails()) {
                if (details.length() > 0) {
                    details.append("\n");
                }
                details.append(String.format("%s: %d sets × %.1f %s", 
                    detail.getExercise().getName(),
                    detail.getSets(),
                    detail.getValue(),
                    detail.getUnit()));
            }
            return new SimpleStringProperty(details.toString());
        });
        exercisesCol.setCellFactory(tc -> {
            TextFieldTableCell<TrainingSession, String> cell = new TextFieldTableCell<>();
            cell.setWrapText(true);
            return cell;
        });
        exercisesCol.setOnEditCommit(event -> {
            TrainingSession session = event.getRowValue();
            try {
                String[] lines = event.getNewValue().split("\n");
                for (int i = 0; i < lines.length; i++) {
                    if (i < session.getExerciseDetails().size()) {
                        ExerciseDetail detail = session.getExerciseDetails().get(i);
                        parseAndUpdateExerciseDetail(detail, lines[i]);
                    }
                }
                trainingSessionService.edit(session);
            } catch (Exception e) {
                showError("Invalid format. Please use: 'Exercise: sets × value unit'");
                loadSessions();
            }
        });
        
        sessionsTable.getColumns().clear();
        sessionsTable.getColumns().addAll(dateCol, exercisesCol);
    }
    
    private void parseAndUpdateExerciseDetail(ExerciseDetail detail, String line) {
        try {
            String[] parts = line.split(":");
            if (parts.length == 2) {
                String[] valuesPart = parts[1].trim().split("×");
                if (valuesPart.length == 2) {
                    String setsStr = valuesPart[0].trim().split("\\s+")[0];
                    int sets = Integer.parseInt(setsStr);
                    
                    String[] valueUnit = valuesPart[1].trim().split("\\s+");
                    float value = Float.parseFloat(valueUnit[0]);
                    String unit = valueUnit[1];
                    
                    detail.setSets(sets);
                    detail.setValue(value);
                    detail.setUnit(unit);
                }
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid format");
        }
    }
    
    public void setWorkout(Workout workout) {
        this.workout = workout;
        workoutNameLabel.setText("Training History - " + workout.getName());
        loadSessions();
    }
    
    private void loadSessions() {
        if (workout != null) {
            sessionsTable.setItems(FXCollections.observableArrayList(
                trainingSessionService.findByWorkout(workout)));
        }
    }
    
    @FXML
    private void handleSaveChanges() {
        try {
            for (TrainingSession session : sessionsTable.getItems()) {
                trainingSessionService.edit(session);
            }
            showSuccess("Changes saved successfully!");
        } catch (Exception e) {
            showError("Error saving changes: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleClose() {
        Stage currentStage = (Stage) workoutNameLabel.getScene().getWindow();
        currentStage.close();
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
    
    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}