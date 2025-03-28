package com.fitness.tracker.app.ui;

import com.fitness.tracker.core.services.ExerciseService;
import com.fitness.tracker.core.services.ServiceFactory;
import com.fitness.tracker.core.services.TrainingSessionService;
import com.fitness.tracker.data.models.Exercise;
import com.fitness.tracker.data.models.TrainingSession;
import com.fitness.tracker.data.models.ExerciseDetail;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.collections.FXCollections;
import javafx.util.StringConverter;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.HashMap;

public class ProgressTrackingController {
    @FXML private ComboBox<Exercise> exerciseComboBox;
    @FXML private Label progressLabel;
    @FXML private LineChart<String, Number> progressChart;
    
    private ExerciseService exerciseService;
    private TrainingSessionService trainingSessionService;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public void initialize() {
        exerciseService = ServiceFactory.createExerciseService();
        trainingSessionService = ServiceFactory.createTrainingSessionService();
        
        setupExerciseComboBox();
        exerciseComboBox.setOnAction(e -> updateChart());
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

    private void updateChart() {
        Exercise selectedExercise = exerciseComboBox.getValue();
        if (selectedExercise == null) return;

        progressChart.getData().clear();

        List<TrainingSession> sessions = trainingSessionService.getAllSessions();

        TreeMap<LocalDate, Double> progressData = new TreeMap<>();
        Map<String, Integer> unitCounts = new HashMap<>();
        String mostCommonUnit = "kg";

        for (TrainingSession session : sessions) {
            for (ExerciseDetail detail : session.getExerciseDetails()) {
                if (detail.getExercise().getId().equals(selectedExercise.getId())) {
                    LocalDate date = session.getDate();
                    double weight = detail.getValue();
                    progressData.merge(date, weight, Math::max);
                    
                    String unit = detail.getUnit();
                    unitCounts.merge(unit, 1, Integer::sum);
                    
                    if (unitCounts.get(unit) > unitCounts.getOrDefault(mostCommonUnit, 0)) {
                        mostCommonUnit = unit;
                    }
                }
            }
        }

        progressChart.getYAxis().setLabel("Weight (" + mostCommonUnit + ")");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy");
        
        progressData.forEach((date, weight) -> {
            series.getData().add(new XYChart.Data<>(date.format(displayFormatter), weight));
        });

        progressChart.setAnimated(false);
        progressChart.getXAxis().setLabel("Date");
        progressChart.getXAxis().setTickLabelRotation(45);
        
        series.setName(selectedExercise.getName());
        progressChart.getData().add(series);

        updateProgressLabel(progressData, mostCommonUnit);
    }

    private void updateProgressLabel(Map<LocalDate, Double> progressData, String unit) {
        if (progressData.isEmpty()) {
            progressLabel.setText("No data available for selected exercise");
            return;
        }

        double firstWeight = progressData.values().iterator().next();
        double lastWeight = progressData.values().stream().reduce((first, second) -> second).get();
        double improvement = lastWeight - firstWeight;
        double percentageChange = (improvement / firstWeight) * 100;

        progressLabel.setText(String.format("Progress: %.1f%% (%.1f → %.1f %s)",
            percentageChange,
            firstWeight,
            lastWeight,
            unit));
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) exerciseComboBox.getScene().getWindow();
        stage.close();
    }
} 