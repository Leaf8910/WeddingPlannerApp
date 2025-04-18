
package com;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Planning2Controller implements Initializable {
    @FXML
    private Label titleLabel;

    @FXML
    private Label venueLabel;

    @FXML
    private Label hallLabel;

    @FXML
    private Label cateringLabel;

    @FXML
    private Label monthLabel;

    @FXML
    private Label dayLabel;

    @FXML
    private Button homeButton;

    @FXML
    private Button planningButton;

    @FXML
    private Button checklistButton;

    @FXML
    private Button itineraryButton;

    @FXML
    private Button continueButton;

    @FXML
    private Button backButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Get the current user
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "No user logged in");
            return;
        }

        // Load planning details for the current user
        Planning planning = PlanningDAO.getPlanningByUserId(currentUser.getId());
        if (planning != null) {
            // Display the planning details
            venueLabel.setText(planning.getVenue());
            hallLabel.setText(planning.getHall());
            cateringLabel.setText(planning.getCatering());
            monthLabel.setText(planning.getMonth());
            dayLabel.setText(planning.getDay());
        } else {
            showAlert(Alert.AlertType.WARNING, "Planning Details",
                    "No planning details found. Please go back to Planning page.");
        }

        // Set up button handlers
        homeButton.setOnAction(this::navigateToHome);
        planningButton.setOnAction(this::navigateToPlanning);
        checklistButton.setOnAction(this::navigateToChecklist);
        itineraryButton.setOnAction(this::navigateToItinerary);
        backButton.setOnAction(this::navigateBackToPlanning1);
        continueButton.setOnAction(this::handleContinue);
    }

    @FXML
    private void navigateToHome(ActionEvent event) {
        navigateTo("/homepage.fxml");
    }

    @FXML
    private void navigateToPlanning(ActionEvent event) {
        navigateTo("/planning1.fxml");
    }

    @FXML
    private void navigateToChecklist(ActionEvent event) {
        navigateTo("/checklist.fxml");
    }

    @FXML
    private void navigateToItinerary(ActionEvent event) {
        navigateTo("/itinerary.fxml");
    }

    @FXML
    private void navigateBackToPlanning1(ActionEvent event) {
        navigateTo("/planning1.fxml");
    }

    @FXML
    private void handleContinue(ActionEvent event) {
        // For now, go back to home - you can extend this to go to another planning page
        showAlert(Alert.AlertType.INFORMATION, "Planning Complete",
                "Your planning selections have been saved!");
        navigateTo("/homepage.fxml");
    }

    private void navigateTo(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = (Stage) homeButton.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error",
                    "Could not navigate to " + fxmlFile);
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}