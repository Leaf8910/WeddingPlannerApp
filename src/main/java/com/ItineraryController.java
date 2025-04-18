package com;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;

public class ItineraryController implements Initializable {

    @FXML
    private GridPane itineraryGridPane;

    @FXML
    private Button addEventButton;

    @FXML
    private Button editButton;

    @FXML
    private Button confirmButton;

    @FXML
    private Button homeButton;

    @FXML
    private Button planningButton;

    @FXML
    private Button checklistButton;

    @FXML
    private Button itineraryButton;

    @FXML
    private Button historyButton;

    @FXML
    private Button logoutButton;

    private ObservableList<ItineraryEvent> itineraryEvents = FXCollections.observableArrayList();
    private int weddingId; // This would come from your application context/user session

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // For demonstration purposes, hardcoded wedding ID (in a real app, this would come from user session)
        weddingId = 1;

        // Load itinerary events from database
        loadItineraryEvents();

        // Set up event handlers
        if (addEventButton == null) {
            System.err.println("Warning: addEventButton is null");
        } else {
            // Set up event handlers
            addEventButton.setOnAction(this::handleAddEvent);
        }
        if (editButton == null) {
            System.err.println("Warning: editButton is null");
        } else {
            // Set up event handlers
            editButton.setOnAction(this::handleAddEvent);
        }
        if (confirmButton == null) {
            System.err.println("Warning: confirmButton is null");
        } else {
            // Set up event handlers
            confirmButton.setOnAction(this::handleAddEvent);
        }


        // Navigation buttons
        homeButton.setOnAction(event -> navigateTo("homepage.fxml"));
        planningButton.setOnAction(event -> navigateTo("planning1.fxml"));
        checklistButton.setOnAction(event -> navigateTo("checklist.fxml"));
        itineraryButton.setOnAction(event -> navigateTo("itinerary.fxml"));
        historyButton.setOnAction(event -> navigateTo("history.fxml"));
        logoutButton.setOnAction(event -> handleLogout());
    }

    private void loadItineraryEvents() {
        // Clear existing events
        // Clear existing events
        if (itineraryGridPane == null) {
            System.err.println("Warning: itineraryGridPane is null");
            return;
        }

        itineraryEvents.clear();
        itineraryGridPane.getChildren().clear();

        itineraryEvents.clear();
        itineraryGridPane.getChildren().clear();

        // Reset row constraints to accommodate the dynamic number of events
        itineraryGridPane.getRowConstraints().clear();

        try (Connection conn = DatabaseUtil.getConnection()) {
            String query = "SELECT id, time, event_description FROM itinerary_events WHERE wedding_id = ? ORDER BY time ASC";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, weddingId);

            ResultSet rs = stmt.executeQuery();

            int rowIndex = 0;
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

            while (rs.next()) {
                int id = rs.getInt("id");
                Time sqlTime = rs.getTime("time");
                String eventDescription = rs.getString("event_description");

                // Convert SQL Time to LocalTime
                LocalTime localTime = sqlTime.toLocalTime();
                String formattedTime = localTime.format(timeFormatter);

                // Add to our collection
                itineraryEvents.add(new ItineraryEvent(id, localTime, eventDescription));

                // Create and add time label
                Label timeLabel = new Label(formattedTime);
                timeLabel.getStyleClass().add("time-label");
                timeLabel.setFont(new javafx.scene.text.Font(14.0));

                // Create and add event description label
                Label eventLabel = new Label(eventDescription);
                eventLabel.getStyleClass().add("event-label");
                eventLabel.setFont(new javafx.scene.text.Font(14.0));

                // Add delete button for edit mode (initially invisible)
                Button deleteButton = new Button("Remove");
                deleteButton.setVisible(false);
                deleteButton.setUserData(id); // Store the event ID
                deleteButton.setOnAction(this::handleDeleteEvent);

                // Add to grid
                itineraryGridPane.add(timeLabel, 0, rowIndex);
                itineraryGridPane.add(eventLabel, 1, rowIndex);
                itineraryGridPane.add(deleteButton, 2, rowIndex);

                rowIndex++;
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load itinerary events: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddEvent(ActionEvent event) {
        // Create dialog
        Dialog<ItineraryEvent> dialog = new Dialog<>();
        dialog.setTitle("Add Itinerary Event");
        dialog.setHeaderText("Enter the details for the new event");

        // Set the button types
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Create the form fields
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        // Time picker
        Spinner<Integer> hourSpinner = new Spinner<>(0, 23, 8); // Default to 8:00 AM
        hourSpinner.setEditable(true);
        hourSpinner.setPrefWidth(70);

        Spinner<Integer> minuteSpinner = new Spinner<>(0, 59, 0); // Default to 00 minutes
        minuteSpinner.setEditable(true);
        minuteSpinner.setPrefWidth(70);

        ComboBox<String> amPmComboBox = new ComboBox<>();
        amPmComboBox.getItems().addAll("AM", "PM");
        amPmComboBox.setValue("AM");

        // Description field
        TextField eventDescription = new TextField();
        eventDescription.setPromptText("Event Description");

        // Add labels and fields to grid
        grid.add(new Label("Time:"), 0, 0);
        grid.add(hourSpinner, 1, 0);
        grid.add(new Label(":"), 2, 0);
        grid.add(minuteSpinner, 3, 0);
        grid.add(amPmComboBox, 4, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(eventDescription, 1, 1, 4, 1);

        dialog.getDialogPane().setContent(grid);

        // Convert the result to ItineraryEvent when the save button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                int hour = hourSpinner.getValue();
                int minute = minuteSpinner.getValue();
                String amPm = amPmComboBox.getValue();

                // Convert to 24-hour format if PM is selected
                if (amPm.equals("PM") && hour < 12) {
                    hour += 12;
                } else if (amPm.equals("AM") && hour == 12) {
                    hour = 0;
                }

                LocalTime time = LocalTime.of(hour, minute);
                String description = eventDescription.getText();

                if (description != null && !description.trim().isEmpty()) {
                    return new ItineraryEvent(0, time, description); // ID will be assigned by database
                }
            }
            return null;
        });

        // Show the dialog and process the result
        Optional<ItineraryEvent> result = dialog.showAndWait();

        result.ifPresent(itineraryEvent -> {
            // Save to database
            saveEventToDatabase(itineraryEvent);

            // Reload the events
            loadItineraryEvents();
        });
    }

    private void saveEventToDatabase(ItineraryEvent event) {
        String sql = "INSERT INTO itinerary_events (wedding_id, time, event_description) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, weddingId);
            stmt.setTime(2, Time.valueOf(event.getTime()));
            stmt.setString(3, event.getDescription());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Event added successfully!");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to add event to database.");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to save event: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEditMode(ActionEvent event) {
        // Toggle visibility of delete buttons
        boolean isEditMode = !itineraryGridPane.lookup("Button[text=\"Remove\"]").isVisible();

        // Find all delete buttons and toggle their visibility
        itineraryGridPane.getChildren().stream()
                .filter(node -> node instanceof Button && "Remove".equals(((Button) node).getText()))
                .forEach(node -> node.setVisible(isEditMode));

        // Change the button text to reflect current mode
        editButton.setText(isEditMode ? "Done" : "Edit");
    }

    @FXML
    private void handleDeleteEvent(ActionEvent event) {
        Button deleteButton = (Button) event.getSource();
        int eventId = (int) deleteButton.getUserData();

        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirm Deletion");
        confirmDialog.setHeaderText("Delete Event");
        confirmDialog.setContentText("Are you sure you want to delete this event?");

        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            deleteEventFromDatabase(eventId);
        }
    }

    private void deleteEventFromDatabase(int eventId) {
        String sql = "DELETE FROM itinerary_events WHERE id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, eventId);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Event deleted successfully!");
                loadItineraryEvents(); // Reload the grid
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete event from database.");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to delete event: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleConfirm(ActionEvent event) {
        showAlert(Alert.AlertType.INFORMATION, "Confirmation",
                "Your itinerary has been saved and confirmed!");
    }

    private void handleLogout() {
        // Implement logout functionality
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not navigate to login page.");
        }
    }

    private void navigateTo(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/" + fxmlFile));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = (Stage) homeButton.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not navigate to " + fxmlFile);
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void navigateToHome(ActionEvent actionEvent) {
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

    public void navigateToPlanning(ActionEvent event) {
        PlanningNavigationHandler.navigateToPlanning(event);
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

    // Inner class to represent an itinerary event
    public static class ItineraryEvent {
        private int id;
        private LocalTime time;
        private String description;

        public ItineraryEvent(int id, LocalTime time, String description) {
            this.id = id;
            this.time = time;
            this.description = description;
        }

        public int getId() {
            return id;
        }

        public LocalTime getTime() {
            return time;
        }

        public String getDescription() {
            return description;
        }
    }
}