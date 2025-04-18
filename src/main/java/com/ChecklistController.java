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
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;

import java.io.IOException;

import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ChecklistController implements Initializable {

    @FXML
    private VBox checklistVBox;

    @FXML
    private Button confirmButton;

    @FXML
    private Button editButton;

    @FXML
    private Button addButton;

    @FXML
    private Menu checklistMenu;

    @FXML
    private Menu planninglistMenu;

    @FXML
    private Button checklistButton;

    @FXML
    private Button planninglistButton;

    private ObservableList<ChecklistItem> checklistItems = FXCollections.observableArrayList();
    private boolean isEditMode = false;

    // Database connection parameters
    private static final String DB_URL = "jdbc:mysql://localhost:3306/product_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Alif8611891";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Load checklist items from database
        loadChecklistItems();

        // Display checklist items
        displayChecklistItems();

        // Set up button handlers
        setupButtonHandlers();
    }

    private void loadChecklistItems() {
        checklistItems.clear();

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, description, is_completed FROM checklist_items")) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String description = rs.getString("description");
                boolean isCompleted = rs.getBoolean("is_completed");

                checklistItems.add(new ChecklistItem(id, description, isCompleted));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Database error while loading checklist items: " + e.getMessage());

            // If the table doesn't exist yet, create it and add default items
            createChecklistTableIfNeeded();
        }

        // If no items were loaded, add default checklist items
        if (checklistItems.isEmpty()) {
            addDefaultChecklistItems();
        }
    }

    private void createChecklistTableIfNeeded() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {

            // Create the checklist_items table if it doesn't exist
            String createTableSQL =
                    "CREATE TABLE IF NOT EXISTS checklist_items (" +
                            "id INT AUTO_INCREMENT PRIMARY KEY," +
                            "description VARCHAR(255) NOT NULL," +
                            "is_completed BOOLEAN DEFAULT FALSE," +
                            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                            ")";

            stmt.executeUpdate(createTableSQL);

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error creating checklist table: " + e.getMessage());
        }
    }

    private void addDefaultChecklistItems() {
        List<String> defaultItems = new ArrayList<>();
        defaultItems.add("Book Wedding Date");
        defaultItems.add("Enter number of Attendants");
        defaultItems.add("Choose Venue");
        defaultItems.add("Choose Caterer");
        defaultItems.add("Select Wedding Month");
        defaultItems.add("Select Wedding Day");
        defaultItems.add("Choose Vendor");
        defaultItems.add("Choose Wedding Dress");
        defaultItems.add("Choose Photographer");

        for (String item : defaultItems) {
            addChecklistItemToDatabase(item);
        }

        // Reload items from database
        loadChecklistItems();
    }

    private void displayChecklistItems() {
        checklistVBox.getChildren().clear();

        for (ChecklistItem item : checklistItems) {
            if (isEditMode) {
                // In edit mode, show text field and delete button
                HBox itemContainer = new HBox(10);
                itemContainer.setPadding(new Insets(5));

                TextField itemTextField = new TextField(item.getDescription());
                itemTextField.setPrefWidth(200);

                Button deleteButton = new Button("Delete");
                deleteButton.setOnAction(e -> {
                    deleteChecklistItem(item.getId());
                    checklistItems.remove(item);
                    displayChecklistItems();
                });

                // Update button to save changes
                Button updateButton = new Button("Update");
                updateButton.setOnAction(e -> {
                    updateChecklistItem(item.getId(), itemTextField.getText());
                    item.setDescription(itemTextField.getText());
                });

                itemContainer.getChildren().addAll(itemTextField, updateButton, deleteButton);
                checklistVBox.getChildren().add(itemContainer);
            } else {
                // In normal mode, show checkbox
                CheckBox checkBox = new CheckBox(item.getDescription());
                checkBox.setSelected(item.isCompleted());
                checkBox.setOnAction(e -> {
                    item.setCompleted(checkBox.isSelected());
                });

                checklistVBox.getChildren().add(checkBox);
            }
        }

        // In edit mode, add a "Add New Item" section
        if (isEditMode) {
            HBox addNewItemContainer = new HBox(10);
            addNewItemContainer.setPadding(new Insets(5));

            TextField newItemTextField = new TextField();
            newItemTextField.setPromptText("New checklist item");
            newItemTextField.setPrefWidth(200);

            Button addNewButton = new Button("Add");
            addNewButton.setOnAction(e -> {
                String newItemText = newItemTextField.getText().trim();
                if (!newItemText.isEmpty()) {
                    int newItemId = addChecklistItemToDatabase(newItemText);
                    checklistItems.add(new ChecklistItem(newItemId, newItemText, false));
                    newItemTextField.clear();
                    displayChecklistItems();
                }
            });

            addNewItemContainer.getChildren().addAll(newItemTextField, addNewButton);
            checklistVBox.getChildren().add(addNewItemContainer);
        }
    }

    private void setupButtonHandlers() {
        confirmButton.setOnAction(e -> saveChecklistState());

        editButton.setOnAction(e -> {
            isEditMode = !isEditMode;
            editButton.setText(isEditMode ? "Cancel" : "Edit");
            displayChecklistItems();
        });
    }

    private void saveChecklistState() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(
                     "UPDATE checklist_items SET is_completed = ? WHERE id = ?")) {

            for (ChecklistItem item : checklistItems) {
                pstmt.setBoolean(1, item.isCompleted());
                pstmt.setInt(2, item.getId());
                pstmt.executeUpdate();
            }

            System.out.println("Checklist saved successfully!");

            // If we were in edit mode, exit it
            if (isEditMode) {
                isEditMode = false;
                editButton.setText("Edit");
                displayChecklistItems();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error saving checklist: " + e.getMessage());
        }
    }

    private int addChecklistItemToDatabase(String description) {
        int newItemId = -1;

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT INTO checklist_items (description, is_completed) VALUES (?, false)",
                     Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, description);
            pstmt.executeUpdate();

            // Get the generated ID
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                newItemId = rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error adding checklist item: " + e.getMessage());
        }

        return newItemId;
    }

    private void updateChecklistItem(int id, String newDescription) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(
                     "UPDATE checklist_items SET description = ? WHERE id = ?")) {

            pstmt.setString(1, newDescription);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error updating checklist item: " + e.getMessage());
        }
    }

    private void deleteChecklistItem(int id) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(
                     "DELETE FROM checklist_items WHERE id = ?")) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error deleting checklist item: " + e.getMessage());
        }
    }

    @FXML
    public void navigateToPlanning(ActionEvent event) {
        // Use the common navigation handler
        PlanningNavigationHandler.navigateToPlanning(event);
    }

    public void navigateToHome(ActionEvent actionEvent) {
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

    // Helper class to represent a checklist item
    public static class ChecklistItem {
        private int id;
        private String description;
        private boolean completed;

        public ChecklistItem(int id, String description, boolean completed) {
            this.id = id;
            this.description = description;
            this.completed = completed;
        }

        public int getId() {
            return id;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public boolean isCompleted() {
            return completed;
        }

        public void setCompleted(boolean completed) {
            this.completed = completed;
        }
    }
}