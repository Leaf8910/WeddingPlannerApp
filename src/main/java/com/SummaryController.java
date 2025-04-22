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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class SummaryController implements Initializable {
    // Navigation buttons
    @FXML private Button homeButton;
    @FXML private Button planningButton;
    @FXML private Button checklistButton;
    @FXML private Button itineraryButton;
    @FXML private Button historyButton;
    @FXML private Button logoutButton;
    @FXML private Button saveToHistoryButton;

    // Summary labels
    @FXML private Label groomLabel;
    @FXML private Label brideLabel;
    @FXML private Label weddingDateLabel;
    @FXML private Label attendantsLabel;
    @FXML private Label budgetLabel;
    @FXML private Label venueLabel;
    @FXML private Label hallLabel;
    @FXML private Label photographerLabel;
    @FXML private Label vendorLabel;
    @FXML private Label dressLabel;
    @FXML private Label muaLabel;
    @FXML private Label catererLabel;
    @FXML private Label setLabel;
    @FXML private Label totalPricingLabel;

    // Planning data
    private Planning currentPlanning;
    private WeddingDetails weddingDetails;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set up navigation button handlers
        homeButton.setOnAction(this::navigateToHome);
        planningButton.setOnAction(this::navigateToPlanning);
        checklistButton.setOnAction(this::navigateToChecklist);
        itineraryButton.setOnAction(this::navigateToItinerary);
        historyButton.setOnAction(this::navigateToHistory);
        logoutButton.setOnAction(this::logout);
        saveToHistoryButton.setOnAction(this::saveToHistory);

        // Load planning and wedding details for the current user
        loadUserData();
    }

    private void loadUserData() {
        // Get the current user
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "No user logged in");
            return;
        }

        // Load planning data
        currentPlanning = PlanningDAO.getPlanningByUserId(currentUser.getId());

        try {
            // Try to load wedding details, but continue even if it fails
            weddingDetails = WeddingDetailsDAO.getWeddingDetailsByUserId(currentUser.getId());
        } catch (Exception e) {
            // If there's an error loading wedding details, just continue with null
            System.err.println("Error loading wedding details: " + e.getMessage());
            weddingDetails = null;
        }

        // Update UI with loaded data
        updateSummaryDisplay();
    }

    private void updateSummaryDisplay() {
        if (weddingDetails != null) {
            // Set couple information
            groomLabel.setText(weddingDetails.getGroomName());
            brideLabel.setText(weddingDetails.getBrideName());

            // Format date range
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
            if (weddingDetails.getWeddingDateFrom() != null) {
                if (weddingDetails.getWeddingDateTo() != null &&
                        !weddingDetails.getWeddingDateFrom().equals(weddingDetails.getWeddingDateTo())) {
                    weddingDateLabel.setText(
                            weddingDetails.getWeddingDateFrom().format(formatter) + " to " +
                                    weddingDetails.getWeddingDateTo().format(formatter)
                    );
                } else {
                    weddingDateLabel.setText(weddingDetails.getWeddingDateFrom().format(formatter));
                }
            } else {
                weddingDateLabel.setText("Not specified");
            }

            // Set attendants and budget
            attendantsLabel.setText(weddingDetails.getAttendantsCount() + " Pax");
            budgetLabel.setText(weddingDetails.getBudget());
        } else {
            // Set default values if no wedding details found
            groomLabel.setText("Not specified");
            brideLabel.setText("Not specified");
            weddingDateLabel.setText("Not specified");
            attendantsLabel.setText("Not specified");
            budgetLabel.setText("Not specified");
        }

        if (currentPlanning != null) {
            // Set venue information
            venueLabel.setText(getValueOrDefault(currentPlanning.getVenue()));
            hallLabel.setText(getValueOrDefault(currentPlanning.getHall()));

            // Set vendors and services
            photographerLabel.setText(getValueOrDefault(currentPlanning.getPhotographer()));
            vendorLabel.setText(getValueOrDefault(currentPlanning.getWed_vendor()));
            dressLabel.setText(getValueOrDefault(currentPlanning.getDress()));
            muaLabel.setText(getValueOrDefault(currentPlanning.getMUA()));
            catererLabel.setText(getValueOrDefault(currentPlanning.getCatering()));
            setLabel.setText(getValueOrDefault(currentPlanning.getSet()));

            // Calculate total price
            double totalPrice = DatabaseUtil.calculateTotalPrice(currentPlanning);
            totalPricingLabel.setText(String.format("$%.2f", totalPrice));
        } else {
            // Set default values if no planning found
            venueLabel.setText("Not specified");
            hallLabel.setText("Not specified");
            photographerLabel.setText("Not specified");
            vendorLabel.setText("Not specified");
            dressLabel.setText("Not specified");
            muaLabel.setText("Not specified");
            catererLabel.setText("Not specified");
            setLabel.setText("Not specified");
            totalPricingLabel.setText("$0.00");
        }
    }

    private String getValueOrDefault(String value) {
        return (value != null && !value.isEmpty()) ? value : "Not specified";
    }

    @FXML
    private void saveToHistory(ActionEvent event) {
        // Get current user
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null) {
            showAlert(Alert.AlertType.ERROR, "Authentication Error", "No user logged in");
            return;
        }

        if (currentPlanning == null) {
            showAlert(Alert.AlertType.ERROR, "Data Error", "No planning data to save");
            return;
        }

        // Create a new planning record for history
        // This ensures we have a separate entry in history, not just updating the existing one
        Planning historicalPlanning = new Planning(
                currentUser.getId(),
                currentPlanning.getVenue(),
                currentPlanning.getHall(),
                currentPlanning.getCatering(),
                currentPlanning.getSet(),
                currentPlanning.getMonth(),
                currentPlanning.getDay(),
                currentPlanning.getDecor(),
                currentPlanning.getMUA(),
                currentPlanning.getWed_vendor(),
                currentPlanning.getPhotographer(),
                currentPlanning.getDress()
        );

        // Set creation date to now
        historicalPlanning.setCreatedAt(LocalDate.now());

        // Save as a new record
        boolean success = PlanningDAO.savePlanning(historicalPlanning);

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Success",
                    "Planning saved to history successfully!");

            // Navigate to history page to see the new entry
            navigateTo("/history.fxml");
        } else {
            showAlert(Alert.AlertType.ERROR, "Error",
                    "Failed to save planning to history.");
        }
    }

    @FXML
    private void navigateToHome(ActionEvent event) {
        navigateTo("/homepage.fxml");
    }

    @FXML
    private void navigateToPlanning(ActionEvent event) {
        PlanningNavigationHandler.navigateToPlanning(event);
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
    private void navigateToHistory(ActionEvent event) {
        navigateTo("/history.fxml");
    }

    @FXML
    private void logout(ActionEvent event) {
        // Clear the current user session
        SessionManager.getInstance().logout();
        navigateTo("/login.fxml");
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

    // Add this method to SummaryController.java
    public void loadSpecificPlanning(int planningId) {
        System.out.println("Loading specific planning: " + planningId);

        // Get the current user
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "No user logged in");
            return;
        }

        // Load the specific planning by ID
        currentPlanning = PlanningDAO.getPlanningById(planningId);

        if (currentPlanning == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Planning not found");
            return;
        }

        System.out.println("Loaded planning: " + currentPlanning.getId());

        // Load wedding details
        weddingDetails = WeddingDetailsDAO.getWeddingDetailsByUserId(currentUser.getId());

        // Update display
        updateSummaryDisplay();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}