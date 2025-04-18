package com;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;

public class HomePageController {
    @FXML private ImageView groomIcon;
    @FXML private TextField groomName;
    @FXML private TextField brideName;
    @FXML private DatePicker weddingDateFrom;
    @FXML private DatePicker weddingDateTo;
    @FXML private TextField attendantsCount;
    @FXML private ComboBox<String> budgetSelector;
    @FXML private Button confirmButton;
    @FXML private Button checklistButton;
    @FXML private Button logoutButton;
    @FXML private Label welcomeLabel;

    @FXML
    public void initialize() {

//    	  System.out.println("Initializing controller..."); // Debug line
        // Initialize budget options
        budgetSelector.getItems().addAll(
                "Under $5,000",
                "$5,000 - $10,000",
                "$10,000 - $20,000",
                "$20,000 - $50,000",
                "Over $50,000"
        );
        if (confirmButton != null) {
            confirmButton.setOnAction(event -> {
                System.out.println("Confirm button clicked!");
                saveWeddingDetails();
                // Add your button handling logic here
            });
        } else {
            System.err.println("Confirm button not found in FXML!");
        }
        // Setup logout button if it exists
        if (logoutButton != null) {
            logoutButton.setOnAction(event -> logout());
        }

        // Display welcome message with username if available
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser != null && welcomeLabel != null) {
            welcomeLabel.setText("Welcome, " + currentUser.getUsername() + "!");
        }
//        // Set up confirm button action
//        confirmButton.setOnAction(event -> saveWeddingDetails());
    }

    private void saveWeddingDetails() {
        try {
            // Parse attendants count (remove "pax" if present)
            String attendantsText = attendantsCount.getText().replaceAll("[^0-9]", "");
            int attendants = Integer.parseInt(attendantsText);

            // Create wedding details object
            WeddingDetails details = new WeddingDetails(
                    groomName.getText(),
                    brideName.getText(),
                    weddingDateFrom.getValue(),
                    weddingDateTo.getValue(),
                    attendants,
                    budgetSelector.getValue()
            );

            // Save to database
            DatabaseUtil.saveWeddingDetails(details);

            // Show success message
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText("Wedding details saved successfully!");
            alert.showAndWait();

        } catch (NumberFormatException e) {
            // Show error message for invalid attendants count
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Invalid Attendants Count");
            alert.setContentText("Please enter a valid number for attendants.");
            alert.showAndWait();
        } catch (Exception e) {
            // Show generic error message
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to Save Details");
            alert.setContentText("An error occurred while saving the details.");
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    public void navigateToChecklist(ActionEvent actionEvent) {
        try {
            // Load the Checklist FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/checklist.fxml"));
            Parent checklistRoot = loader.load();

            // Get the current stage
            Stage stage;
            if (checklistButton != null) {
                stage = (Stage) checklistButton.getScene().getWindow();
            } else {
                // We're using the menu approach
                MenuItem checklistMenuItem = null;
                stage = (Stage) checklistMenuItem.getParentPopup().getOwnerWindow();
            }

            // Replace the current scene with the checklist scene
            Scene checklistScene = new Scene(checklistRoot, stage.getScene().getWidth(), stage.getScene().getHeight());
            stage.setScene(checklistScene);
            stage.setTitle("Wedding Planning - Checklist");

            // No need to call stage.show() as it's already showing

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading checklist view: " + e.getMessage());
        }
    }

    public void navigateToPlanning(ActionEvent event) {
        PlanningNavigationHandler.navigateToPlanning(event);
    }

    public void navigateToHome(ActionEvent actionEvent) {
    }

    public void navigateToItinerary(ActionEvent actionEvent) {
        try {
            // Load the Checklist FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/itinerary.fxml"));
            Parent checklistRoot = loader.load();

            // Get the current stage
            Stage stage;
            if (checklistButton != null) {
                stage = (Stage) checklistButton.getScene().getWindow();
            } else {
                // We're using the menu approach
                MenuItem checklistMenuItem = null;
                stage = (Stage) checklistMenuItem.getParentPopup().getOwnerWindow();
            }

            // Replace the current scene with the checklist scene
            Scene checklistScene = new Scene(checklistRoot, stage.getScene().getWidth(), stage.getScene().getHeight());
            stage.setScene(checklistScene);
            stage.setTitle("Wedding Planning - Checklist");

            // No need to call stage.show() as it's already showing

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading checklist view: " + e.getMessage());
        }
    }
    public void logout() {
        // Clear the current user session
        SessionManager.getInstance().logout();

        try {
            // Load the login FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
            Parent loginRoot = loader.load();

            // Get the current stage
            Stage stage = (Stage) logoutButton.getScene().getWindow();

            // Replace the current scene with the login scene
            Scene loginScene = new Scene(loginRoot, stage.getScene().getWidth(), stage.getScene().getHeight());
            stage.setScene(loginScene);
            stage.setTitle("Wedding Planner - Login");

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading login view: " + e.getMessage());
        }
    }
}