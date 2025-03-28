package com.fitness.tracker.app.ui;

import com.fitness.tracker.core.services.ServiceFactory;
import com.fitness.tracker.core.services.TrainingSessionService;
import com.fitness.tracker.data.models.TrainingSession;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.util.List;

public class SessionsViewController {
    @FXML private DatePicker fromDatePicker;
    @FXML private DatePicker toDatePicker;
    @FXML private TableView<TrainingSession> sessionsTable;
    @FXML private TableColumn<TrainingSession, String> dateColumn;
    @FXML private TableColumn<TrainingSession, String> workoutColumn;
    @FXML private TableColumn<TrainingSession, String> exercisesColumn;

    private TrainingSessionService trainingSessionService;
    private Stage stage;

    public void initialize() {
        trainingSessionService = ServiceFactory.createTrainingSessionService();
        setupTable();
        loadSessions();
    }

    private void setupTable() {
        dateColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getDate().toString()));

        workoutColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getWorkout().getName()));

        exercisesColumn.setCellValueFactory(data ->
                new SimpleStringProperty(String.format("%d exercises",
                        data.getValue().getExerciseDetails().size())));
    }

    private void loadSessions() {
        List<TrainingSession> sessions = trainingSessionService.getAllSessions();
        sessionsTable.setItems(FXCollections.observableArrayList(sessions));
    }




    @FXML
    private void handleFilter() {
        LocalDate fromDate = fromDatePicker.getValue();
        LocalDate toDate = toDatePicker.getValue();

        if (fromDate == null || toDate == null) {
            showError("Please select both from and to dates");
            return;
        }

        if (fromDate.isAfter(toDate)) {
            showError("From date must be before to date");
            return;
        }

//        List<TrainingSession> filteredSessions =
//                trainingSessionService.getSessionsBetweenDates(fromDate, toDate);
//        sessionsTable.setItems(FXCollections.observableArrayList(filteredSessions));
    }

    @FXML
    private void handleViewDetails() {
        TrainingSession selectedSession = sessionsTable.getSelectionModel().getSelectedItem();
        if (selectedSession == null) {
            showError("Please select a session to view");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/SessionDetails.fxml"));
            Parent root = loader.load();

            SessionDetailsController controller = loader.getController();
            controller.setSession(selectedSession);

            Stage detailsStage = new Stage();
            detailsStage.setScene(new Scene(root));
            detailsStage.show();
        } catch (Exception e) {
            showError("Error opening session details: " + e.getMessage());
        }
    }

    @FXML
    private void handleDeleteSession() {
        TrainingSession selectedSession = sessionsTable.getSelectionModel().getSelectedItem();
        if (selectedSession == null) {
            showError("Please select a session to delete");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Delete Session");
        confirmation.setHeaderText("Delete Training Session");
        confirmation.setContentText("Are you sure you want to delete this session?");

        if (confirmation.showAndWait().get() == ButtonType.OK) {
            try {
                trainingSessionService.delete(selectedSession);
                loadSessions();
            } catch (Exception e) {
                showError("Error deleting session: " + e.getMessage());
            }
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