package com.fitness.tracker.app.ui;

import com.fitness.tracker.core.services.*;
import com.fitness.tracker.data.models.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.beans.property.SimpleStringProperty;
import java.util.List;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class DashboardController {
    @FXML private Label welcomeLabel;
    @FXML private TableView<Exercise> exercisesTable;
    @FXML private TableView<Workout> workoutsTable;
    @FXML private TableView<UserExercise> goalsTable;
    @FXML private TableView<TrainingSession> sessionsTable;

    private AppUser currentUser;
    private Stage stage;
    private ExerciseService exerciseService;
    private WorkoutService workoutService;
    private UserExerciseService userExerciseService;
    private TrainingSessionService trainingSessionService;
    private UserService userService;

    public void initialize() {
        setupServices();
        setupTables();
    }

    private void setupServices() {
        exerciseService = ServiceFactory.createExerciseService();
        workoutService = ServiceFactory.createWorkoutService();
        userExerciseService = ServiceFactory.createUserExerciseService();
        trainingSessionService = ServiceFactory.createTrainingSessionService();
        userService = ServiceFactory.createUserService();
    }

    private void setupTables() {
        setupWorkoutsTable();
        setupExercisesTable();
        setupGoalsTable();
        setupSessionsTable();
    }

    private void setupWorkoutsTable() {
        TableColumn<Workout, String> workoutNameCol = new TableColumn<>("Name");
        workoutNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        workoutNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        workoutNameCol.setOnEditCommit(event -> {
            Workout workout = event.getRowValue();
            String newName = event.getNewValue().trim();
            if (!newName.isEmpty()) {
                workout.setName(newName);
                try {
                    workoutService.edit(workout);
                    workoutsTable.refresh();
                    refreshData();
                } catch (Exception e) {
                    showError("Error updating workout name: " + e.getMessage());
                    refreshData();
                }
            } else {
                showError("Workout name cannot be empty");
                refreshData();
            }
        });
        
        TableColumn<Workout, String> sessionsCountCol = new TableColumn<>("Sessions");
        sessionsCountCol.setCellValueFactory(cellData -> {
            List<TrainingSession> sessions = trainingSessionService.findByWorkout(cellData.getValue());
            return new SimpleStringProperty(String.valueOf(sessions.size()));
        });
        
        workoutsTable.setEditable(true);
        workoutsTable.getColumns().addAll(workoutNameCol, sessionsCountCol);
    }

    private void setupExercisesTable() {
        TableColumn<Exercise, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        
        TableColumn<Exercise, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        
        exercisesTable.getColumns().addAll(nameCol, typeCol);
    }

    private void setupGoalsTable() {
        TableColumn<UserExercise, String> exerciseCol = new TableColumn<>("Exercise");
        exerciseCol.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getExercise().getName()));
        
        TableColumn<UserExercise, String> targetCol = new TableColumn<>("Target");
        targetCol.setCellValueFactory(cellData -> {
            UserExercise goal = cellData.getValue();
            return new SimpleStringProperty(String.format("%.1f", goal.getGoal()));
        });
        
        TableColumn<UserExercise, String> deadlineCol = new TableColumn<>("Deadline");
        deadlineCol.setCellValueFactory(cellData -> {
            LocalDate deadline = cellData.getValue().getGoalDeadline();
            return new SimpleStringProperty(deadline != null ? deadline.toString() : "No deadline");
        });
        
        TableColumn<UserExercise, String> daysLeftCol = new TableColumn<>("Days Left");
        daysLeftCol.setCellValueFactory(cellData -> {
            LocalDate deadline = cellData.getValue().getGoalDeadline();
            if (deadline == null) {
                return new SimpleStringProperty("No deadline");
            }
            long daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), deadline);
            if (daysLeft < 0) {
                return new SimpleStringProperty("Expired");
            }
            return new SimpleStringProperty(daysLeft + " days");
        });
        
        TableColumn<UserExercise, String> progressCol = new TableColumn<>("Progress");
        progressCol.setCellValueFactory(cellData -> {
            UserExercise goal = cellData.getValue();
            return new SimpleStringProperty(String.format("%.1f%%", calculateProgress(goal)));
        });
        
        goalsTable.getColumns().addAll(exerciseCol, targetCol, deadlineCol, daysLeftCol, progressCol);
    }

    private void setupSessionsTable() {
        TableColumn<TrainingSession, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getDate().toString()));
        
        TableColumn<TrainingSession, String> workoutCol = new TableColumn<>("Workout");
        workoutCol.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getWorkout().getName()));
        
        TableColumn<TrainingSession, String> exercisesCol = new TableColumn<>("Exercises");
        exercisesCol.setCellValueFactory(cellData -> {
            int count = cellData.getValue().getExerciseDetails().size();
            return new SimpleStringProperty(String.valueOf(count));
        });
        
        sessionsTable.getColumns().addAll(dateCol, workoutCol, exercisesCol);
    }

    public void setUser(AppUser user) {
        this.currentUser = user;
        welcomeLabel.setText("Welcome, " + user.getName() + "!");
        loadUserData();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private void loadUserData() {
        if (currentUser != null) {
            List<Workout> userWorkouts = workoutService.findByUser(currentUser);
            workoutsTable.setItems(FXCollections.observableArrayList(userWorkouts));
            
            loadExercises();
            loadGoals();
            loadSessions();
        }
    }

    @FXML
    private void handleCreateWorkout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/WorkoutCreation.fxml"));
            Parent root = loader.load();
            
            Stage workoutStage = new Stage();
            workoutStage.setTitle("Create New Workout");
            workoutStage.initModality(Modality.WINDOW_MODAL);
            workoutStage.initOwner(stage);
            
            WorkoutCreationController controller = loader.getController();
            controller.setUser(currentUser);
            controller.setStage(workoutStage);
            
            Scene scene = new Scene(root);
            workoutStage.setScene(scene);
            workoutStage.showAndWait();
            
            refreshData();
        } catch (Exception e) {
            showError("Error opening workout creation: " + e.getMessage());
        }
    }

    @FXML
    private void handleCreateGoal() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/GoalSetting.fxml"));
            Parent root = loader.load();
            
            GoalSettingController controller = loader.getController();
            controller.setUser(currentUser);
            controller.setDashboardController(this);
            
            Stage goalStage = new Stage();
            goalStage.setTitle("Create New Goal");
            goalStage.initModality(Modality.WINDOW_MODAL);
            goalStage.initOwner(stage);
            
            controller.setStage(goalStage);
            goalStage.setScene(new Scene(root));
            goalStage.showAndWait();
        } catch (Exception e) {
            showError("Error opening goal creation: " + e.getMessage());
        }
    }

    @FXML
    private void handleLogSession() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/TrainingSession.fxml"));
            Parent root = loader.load();
            
            TrainingSessionController controller = loader.getController();
            controller.setUser(currentUser);
            controller.setDashboardController(this);
            
            Stage sessionStage = new Stage();
            sessionStage.setScene(new Scene(root));
            sessionStage.show();
        } catch (Exception e) {
            showError("Error opening training session: " + e.getMessage());
        }
    }

    @FXML
    private void handleAddExercise() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ExerciseCreation.fxml"));
            Parent root = loader.load();
            
            Stage exerciseStage = new Stage();
            exerciseStage.setTitle("Add New Exercise");
            exerciseStage.initModality(Modality.WINDOW_MODAL);
            exerciseStage.initOwner(stage);
            
            ExerciseCreationController controller = loader.getController();
            controller.setStage(exerciseStage);
            controller.setDashboardController(this);
            
            Scene scene = new Scene(root);
            exerciseStage.setScene(scene);
            exerciseStage.showAndWait();
        } catch (Exception e) {
            showError("Error opening exercise creation window: " + e.getMessage());
        }
    }

    @FXML
    private void handleEditExercise() {
        Exercise selectedExercise = exercisesTable.getSelectionModel().getSelectedItem();
        if (selectedExercise == null) {
            showError("Please select an exercise to edit");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ExerciseCreation.fxml"));
            Parent root = loader.load();
            
            Stage exerciseStage = new Stage();
            exerciseStage.setTitle("Edit Exercise");
            exerciseStage.initModality(Modality.WINDOW_MODAL);
            exerciseStage.initOwner(stage);
            
            ExerciseCreationController controller = loader.getController();
            controller.setStage(exerciseStage);
            controller.setDashboardController(this);
            controller.setExercise(selectedExercise);
            
            Scene scene = new Scene(root);
            exerciseStage.setScene(scene);
            exerciseStage.showAndWait();
        } catch (Exception e) {
            showError("Error opening exercise edit window: " + e.getMessage());
        }
    }

    @FXML
    private void handleDeleteExercise() {
        Exercise selectedExercise = exercisesTable.getSelectionModel().getSelectedItem();
        if (selectedExercise == null) {
            showError("Please select an exercise to delete");
            return;
        }
        
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Delete Exercise");
        confirmation.setHeaderText("Delete " + selectedExercise.getName());
        confirmation.setContentText("Are you sure you want to delete this exercise?");
        
        if (confirmation.showAndWait().get() == ButtonType.OK) {
            try {
                exerciseService.delete(selectedExercise.getId());
                refreshData();
            } catch (Exception e) {
                showError("Error deleting exercise: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleEditWorkout() {
        Workout selectedWorkout = workoutsTable.getSelectionModel().getSelectedItem();
        if (selectedWorkout == null) {
            showError("Please select a workout to edit");
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/WorkoutEdit.fxml"));
            Parent root = loader.load();
            
            Stage workoutStage = new Stage();
            workoutStage.setTitle("Edit Workout");
            workoutStage.initModality(Modality.WINDOW_MODAL);
            workoutStage.initOwner(stage);
            
            WorkoutEditController controller = loader.getController();
            controller.setWorkout(selectedWorkout);
            controller.setDashboardController(this);
            controller.setStage(workoutStage);
            
            Scene scene = new Scene(root);
            workoutStage.setScene(scene);
            workoutStage.showAndWait();
        } catch (Exception e) {
            showError("Error opening workout editor: " + e.getMessage());
        }
    }

    @FXML
    private void handleDeleteWorkout() {
        Workout selectedWorkout = workoutsTable.getSelectionModel().getSelectedItem();
        if (selectedWorkout == null) {
            showError("Please select a workout to delete");
            return;
        }
        
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Delete Workout");
        confirmation.setHeaderText("Delete " + selectedWorkout.getName());
        confirmation.setContentText("Are you sure you want to delete this workout?");
        
        if (confirmation.showAndWait().get() == ButtonType.OK) {
            try {
                workoutService.delete(selectedWorkout);
                refreshData();
            } catch (Exception e) {
                showError("Error deleting workout: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleViewTrainingHistory() {
        Workout selectedWorkout = workoutsTable.getSelectionModel().getSelectedItem();
        if (selectedWorkout == null) {
            showError("Please select a workout to view history");
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/TrainingHistory.fxml"));
            Parent root = loader.load();
            
            Stage historyStage = new Stage();
            historyStage.setTitle("Training History");
            historyStage.initModality(Modality.WINDOW_MODAL);
            historyStage.initOwner(stage);
            
            TrainingHistoryController controller = loader.getController();
            controller.setWorkout(selectedWorkout);
            
            Scene scene = new Scene(root);
            historyStage.setScene(scene);
            historyStage.show();
        } catch (Exception e) {
            showError("Error opening training history: " + e.getMessage());
        }
    }

    @FXML
    private void handleViewProgress() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ProgressTracking.fxml"));
            Parent root = loader.load();
            
            Stage progressStage = new Stage();
            progressStage.setTitle("Progress Tracking");
            progressStage.initModality(Modality.WINDOW_MODAL);
            progressStage.initOwner(stage);
            
            Scene scene = new Scene(root);
            progressStage.setScene(scene);
            progressStage.show();
        } catch (Exception e) {
            showError("Error opening progress tracking: " + e.getMessage());
        }
    }

    @FXML
    private void handleEditGoal() {
        UserExercise selectedGoal = goalsTable.getSelectionModel().getSelectedItem();
        if (selectedGoal == null) {
            showError("Please select a goal to edit");
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/GoalSetting.fxml"));
            Parent root = loader.load();
            
            GoalSettingController controller = loader.getController();
            controller.setUser(currentUser);
            controller.setGoal(selectedGoal);
            controller.setDashboardController(this);
            
            Stage goalStage = new Stage();
            goalStage.setTitle("Edit Goal");
            goalStage.initModality(Modality.WINDOW_MODAL);
            goalStage.initOwner(stage);
            
            controller.setStage(goalStage);
            goalStage.setScene(new Scene(root));
            goalStage.showAndWait();
        } catch (Exception e) {
            showError("Error opening goal editor: " + e.getMessage());
        }
    }

    @FXML
    private void handleDeleteGoal() {
        UserExercise selectedGoal = goalsTable.getSelectionModel().getSelectedItem();
        if (selectedGoal == null) {
            showError("Please select a goal to delete");
            return;
        }
        
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Delete Goal");
        confirmation.setHeaderText("Delete Goal");
        confirmation.setContentText("Are you sure you want to delete this goal?");
        
        if (confirmation.showAndWait().get() == ButtonType.OK) {
            try {
                userExerciseService.delete(selectedGoal);
                refreshData();
            } catch (Exception e) {
                showError("Error deleting goal: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleViewSessionDetails() {
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
        confirmation.setContentText("Are you sure you want to delete this training session?");
        
        if (confirmation.showAndWait().get() == ButtonType.OK) {
            try {
                trainingSessionService.delete(selectedSession);
                refreshData();
            } catch (Exception e) {
                showError("Error deleting session: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleLogTrainingSession() {
        Workout selectedWorkout = workoutsTable.getSelectionModel().getSelectedItem();
        if (selectedWorkout == null) {
            showError("Please select a workout to log a training session");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/TrainingSession.fxml"));
            Parent root = loader.load();
            
            Stage sessionStage = new Stage();
            sessionStage.setTitle("Log Training Session");
            sessionStage.initModality(Modality.WINDOW_MODAL);
            sessionStage.initOwner(stage);
            
            TrainingSessionController controller = loader.getController();
            controller.setWorkout(selectedWorkout);
            controller.setUser(currentUser);
            controller.setDashboardController(this);
            controller.setStage(sessionStage);
            
            Scene scene = new Scene(root);
            sessionStage.setScene(scene);
            sessionStage.showAndWait();
            
            refreshData();
        } catch (Exception e) {
            showError("Error opening training session window: " + e.getMessage());
        }
    }

    public void refreshData() {
        if (currentUser != null) {
            List<Workout> userWorkouts = workoutService.findByUser(currentUser);
            workoutsTable.setItems(FXCollections.observableArrayList(userWorkouts));
            
            List<Exercise> exercises = exerciseService.getAllExercises();
            exercisesTable.setItems(FXCollections.observableArrayList(exercises));
            
            List<UserExercise> goals = userExerciseService.findByUser(currentUser);
            goalsTable.setItems(FXCollections.observableArrayList(goals));
            
            List<TrainingSession> sessions = trainingSessionService.getUserSessions(currentUser);
            sessionsTable.setItems(FXCollections.observableArrayList(sessions));
        }
    }

    public void refreshExercises() {
        exercisesTable.setItems(FXCollections.observableArrayList(exerciseService.getAllExercises()));
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private double calculateProgress(UserExercise goal) {
        List<TrainingSession> sessions = trainingSessionService.getUserSessions(goal.getUser());
        double maxValue = 0;
        
        for (TrainingSession session : sessions) {
            for (ExerciseDetail detail : session.getExerciseDetails()) {
                if (detail.getExercise().getId().equals(goal.getExercise().getId())) {
                    maxValue = Math.max(maxValue, detail.getValue());
                }
            }
        }
        
        return (maxValue / goal.getGoal()) * 100.0;
    }

    private void loadExercises() {
        List<Exercise> exercises = exerciseService.getAllExercises();
        exercisesTable.setItems(FXCollections.observableArrayList(exercises));
    }

    private void loadGoals() {
        List<UserExercise> goals = userExerciseService.findByUser(currentUser);
        goalsTable.setItems(FXCollections.observableArrayList(goals));
    }

    private void loadSessions() {
        List<TrainingSession> sessions = trainingSessionService.getUserSessions(currentUser);
        sessionsTable.setItems(FXCollections.observableArrayList(sessions));
    }

    public void refreshGoals() {
        if (currentUser != null) {
            List<UserExercise> goals = userExerciseService.findByUser(currentUser);
            goalsTable.getItems().clear();
            goalsTable.setItems(FXCollections.observableArrayList(goals));
        }
    }
}