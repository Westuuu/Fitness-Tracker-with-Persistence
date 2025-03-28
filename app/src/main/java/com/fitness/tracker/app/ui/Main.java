package com.fitness.tracker.app.ui;

public class Main {
    public static void main(String[] args) {
        config.EnvironmentConfig.load();
        FitnessTracker.launch(FitnessTracker.class, args);
    }
}
