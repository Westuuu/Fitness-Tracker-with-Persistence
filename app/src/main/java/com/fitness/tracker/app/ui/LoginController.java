package com.fitness.tracker.app.ui;

import com.fitness.tracker.core.services.ServiceFactory;
import com.fitness.tracker.core.services.UserService;
import com.fitness.tracker.data.models.AppUser;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {
    @FXML
    private TextField nameField;
    @FXML
    private TextField emailField;

    private UserService userService;
    private Stage stage;

    public void initialize() {
        ServiceFactory.init();
        userService = ServiceFactory.createUserService();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        
        if (email.isEmpty()) {
            showError("Email is required");
            return;
        }

        try {
            AppUser user = userService.authenticate(email);
            if (user != null) {
                openDashboard(user);
            } else {
                showError("User not found. Please register first.");
            }
        } catch (Exception e) {
            showError("Error during login: " + e.getMessage());
        }
    }

    @FXML
    private void handleRegister() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();

        if (name.isEmpty() || email.isEmpty()) {
            showError("Name and email are required");
            return;
        }

        try {
            AppUser newUser = userService.register(name, email);
            openDashboard(newUser);
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        } catch (Exception e) {
            showError("Error during registration: " + e.getMessage());
        }
    }

    private void openDashboard(AppUser user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/MainDashboard.fxml"));
            Parent root = loader.load();

            DashboardController dashboardController = loader.getController();
            dashboardController.setUser(user);
            dashboardController.setStage(stage);

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Fitness Tracker - Dashboard");
            stage.show();
        } catch (Exception e) {
            showError("Error opening dashboard: " + e.getMessage());
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}