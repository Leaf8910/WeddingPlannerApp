package com;

import com.Product;
import com.PlanningNavigationHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML
    private ComboBox<Product> productComboBox;

    @FXML
    private ComboBox<Product> hallComboBox;

    @FXML
    private ComboBox<Product> cateringComboBox;

    @FXML
    private ImageView selectedImageView;

    @FXML
    private ImageView selectedImageView1;

    @FXML
    private ImageView selectedImageView2;

    @FXML
    private Menu checklistMenu;

    @FXML
    private Button checklistButton;

    @FXML
    private Button planninglistButton;

    @FXML
    private ComboBox<String> monthComboBox;

    @FXML
    private ComboBox<String> dayComboBox;

    @FXML
    private Button confirmButton;

    @FXML
    private Label errorMessageLabel;

    private final ObservableList<Product> products = FXCollections.observableArrayList();
    private final ObservableList<Product> halls = FXCollections.observableArrayList();
    private final ObservableList<Product> catering = FXCollections.observableArrayList();
    private final ObservableList<String> months = FXCollections.observableArrayList(
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
    );
    private final ObservableList<String> days = FXCollections.observableArrayList(
            "1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
            "11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
            "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"
    );

    // MySQL database connection parameters
    private static final String DB_URL = "jdbc:mysql://localhost:3306/product_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Alif8611891";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Load data from database
        loadProductsFromDatabase();
        loadHallsFromDatabase();
        loadCateringFromDatabase();

        // Set up product combo box
        setupComboBox(productComboBox, products);

        // Set up hall combo box
        setupComboBox(hallComboBox, halls);

        // Set up catering combo box
        setupComboBox(cateringComboBox, catering);

        // Set up month and day combo boxes if they exist
        if (monthComboBox != null) {
            monthComboBox.setItems(months);
        }

        if (dayComboBox != null) {
            dayComboBox.setItems(days);
        }

        // Add listener for product selection change
        productComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                selectedImageView.setImage(newValue.getImage());
                // Adjust the image size as needed
                selectedImageView.setFitWidth(200);
                selectedImageView.setFitHeight(200);
                selectedImageView.setPreserveRatio(true);
            }
        });

        // Add listener for hall selection change
        hallComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                selectedImageView1.setImage(newValue.getImage());
                // Adjust the image size as needed
                selectedImageView1.setFitWidth(200);
                selectedImageView1.setFitHeight(200);
                selectedImageView1.setPreserveRatio(true);
            }
        });

        // Add listener for catering selection change
        cateringComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                selectedImageView2.setImage(newValue.getImage());
                // Adjust the image size as needed
                selectedImageView2.setFitWidth(200);
                selectedImageView2.setFitHeight(200);
                selectedImageView2.setPreserveRatio(true);
            }
        });

        // Select first items by default if available
        if (!products.isEmpty()) {
            productComboBox.getSelectionModel().selectFirst();
        }

        if (!halls.isEmpty()) {
            hallComboBox.getSelectionModel().selectFirst();
        }

        if (!catering.isEmpty()) {
            cateringComboBox.getSelectionModel().selectFirst();
        }

        // Set up confirm button handler
        if (confirmButton != null) {
            confirmButton.setOnAction(this::handleConfirm);
        }

        // Load existing planning data if available
        loadExistingPlanningData();
    }

    private void loadExistingPlanningData() {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null) return;

        Planning existingPlanning = PlanningDAO.getPlanningByUserId(currentUser.getId());
        if (existingPlanning != null) {
            // Try to match the venue, hall, and catering to the combobox items
            for (Product product : products) {
                if (product.getName().equals(existingPlanning.getVenue())) {
                    productComboBox.getSelectionModel().select(product);
                    break;
                }
            }

            for (Product hall : halls) {
                if (hall.getName().equals(existingPlanning.getHall())) {
                    hallComboBox.getSelectionModel().select(hall);
                    break;
                }
            }

            for (Product cateringItem : catering) {
                if (cateringItem.getName().equals(existingPlanning.getCatering())) {
                    cateringComboBox.getSelectionModel().select(cateringItem);
                    break;
                }
            }

            // Set month and day if they exist
            if (monthComboBox != null && existingPlanning.getMonth() != null) {
                monthComboBox.setValue(existingPlanning.getMonth());
            }

            if (dayComboBox != null && existingPlanning.getDay() != null) {
                dayComboBox.setValue(existingPlanning.getDay());
            }
        }
    }

    @FXML
    public void navigateToChecklist() {
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

    @FXML
    public void navigateToPlanning(ActionEvent event) {
        // Use the common navigation handler
        PlanningNavigationHandler.navigateToPlanning(event);
    }

    @FXML
    public void handleConfirm(ActionEvent event) {
        // Validate that all selections have been made
        if (productComboBox.getSelectionModel().isEmpty() ||
                hallComboBox.getSelectionModel().isEmpty() ||
                cateringComboBox.getSelectionModel().isEmpty() ||
                (monthComboBox != null && monthComboBox.getSelectionModel().isEmpty()) ||
                (dayComboBox != null && dayComboBox.getSelectionModel().isEmpty())) {

            if (errorMessageLabel != null) {
                errorMessageLabel.setText("Please make all selections before continuing");
            } else {
                showAlert(Alert.AlertType.ERROR, "Incomplete Selections",
                        "Please make all selections before continuing");
            }
            return;
        }

        // Get the current user
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null) {
            showAlert(Alert.AlertType.ERROR, "Authentication Error",
                    "You must be logged in to save planning details");
            return;
        }

        // Get selected items
        String venue = productComboBox.getSelectionModel().getSelectedItem().getName();
        String hall = hallComboBox.getSelectionModel().getSelectedItem().getName();
        String cateringChoice = cateringComboBox.getSelectionModel().getSelectedItem().getName();
        String month = monthComboBox != null ? monthComboBox.getValue() : "";
        String day = dayComboBox != null ? dayComboBox.getValue() : "";

        // Create planning object
        Planning planning = new Planning(
                currentUser.getId(),
                venue,
                hall,
                cateringChoice,
                month,
                day
        );

        // Save to database
        boolean success = PlanningDAO.savePlanning(planning);

        if (success) {
            // Navigate to planning2.fxml
            navigateToPlanning2();
        } else {
            showAlert(Alert.AlertType.ERROR, "Save Error",
                    "Failed to save planning details. Please try again.");
        }
    }

    private void navigateToPlanning2() {
        try {
            // Load the Planning2 FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/planning2.fxml"));
            Parent planning2Root = loader.load();

            // Get the current stage
            Stage stage = (Stage) confirmButton.getScene().getWindow();

            // Replace the current scene with the planning2 scene
            Scene planning2Scene = new Scene(planning2Root, stage.getScene().getWidth(), stage.getScene().getHeight());
            stage.setScene(planning2Scene);
            stage.setTitle("Wedding Planning - Planning Details");

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error",
                    "Could not navigate to Planning Details page: " + e.getMessage());
        }
    }

    private void setupComboBox(ComboBox<Product> comboBox, ObservableList<Product> items) {
        // Set custom cell factory for ComboBox
        comboBox.setCellFactory(new Callback<ListView<Product>, ListCell<Product>>() {
            @Override
            public ListCell<Product> call(ListView<Product> param) {
                return new ListCell<Product>() {
                    private final ImageView imageView = new ImageView();

                    @Override
                    protected void updateItem(Product product, boolean empty) {
                        super.updateItem(product, empty);

                        if (empty || product == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            setText(product.getName());
                            imageView.setImage(product.getImage());
                            imageView.setFitHeight(90);
                            imageView.setFitWidth(90);
                            imageView.setPreserveRatio(true);
                            setGraphic(imageView);
                        }
                    }
                };
            }
        });

        // Set button cell (what's shown when the combobox is closed)
        comboBox.setButtonCell(new ListCell<Product>() {
            private final ImageView imageView = new ImageView();

            @Override
            protected void updateItem(Product product, boolean empty) {
                super.updateItem(product, empty);

                if (empty || product == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(product.getName());
                    imageView.setImage(product.getImage());
                    imageView.setFitHeight(24);
                    imageView.setFitWidth(24);
                    imageView.setPreserveRatio(true);
                    setGraphic(imageView);
                }
            }
        });

        // Set items to ComboBox
        comboBox.setItems(items);
    }

    private void loadProductsFromDatabase() {
        products.clear();

        try {
            // Load MySQL JDBC driver if not already loaded
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                System.err.println("MySQL JDBC Driver not found.");
                e.printStackTrace();
                return;
            }

            // Connect to database
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 Statement stmt = conn.createStatement()) {

                String query = "SELECT id, name, image_path FROM products WHERE type = 'venue' OR type IS NULL";
                try (ResultSet rs = stmt.executeQuery(query)) {
                    while (rs.next()) {
                        int id = rs.getInt("id");
                        String name = rs.getString("name");
                        String imagePath = rs.getString("image_path");

                        // Get input stream for image
                        InputStream is = getClass().getResourceAsStream(imagePath);
                        if (is == null) {
                            System.err.println("Cannot find image: " + imagePath);
                            // Try with a different path structure
                            is = getClass().getResourceAsStream("/images/default.png");
                            if (is == null) {
                                System.err.println("Cannot find default image either!");
                                continue; // Skip this product
                            }
                        }

                        // Create image and product objects
                        Image image = new Image(is);
                        Product product = new Product(id, name, image);
                        products.add(product);

                        // Close the input stream
                        is.close();

                        System.out.println("Loaded venue product: " + name);
                    }
                }
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            System.err.println("Error loading products: " + e.getMessage());
        }

        // If no products were loaded, add some default products for testing
        if (products.isEmpty()) {
            System.out.println("No products found in database. Adding default products.");
            addDefaultProducts();
        }
    }

    private void loadHallsFromDatabase() {
        halls.clear();

        try {
            // Connect to database
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 Statement stmt = conn.createStatement()) {

                String query = "SELECT id, name, image_path FROM products WHERE type = 'hall'";
                try (ResultSet rs = stmt.executeQuery(query)) {
                    while (rs.next()) {
                        int id = rs.getInt("id");
                        String name = rs.getString("name");
                        String imagePath = rs.getString("image_path");

                        // Get input stream for image
                        InputStream is = getClass().getResourceAsStream(imagePath);
                        if (is == null) {
                            System.err.println("Cannot find image: " + imagePath);
                            is = getClass().getResourceAsStream("/images/default.png");
                            if (is == null) {
                                System.err.println("Cannot find default image either!");
                                continue; // Skip this hall
                            }
                        }

                        // Create image and product objects
                        Image image = new Image(is);
                        Product hall = new Product(id, name, image);
                        halls.add(hall);

                        // Close the input stream
                        is.close();

                        System.out.println("Loaded hall product: " + name);
                    }
                }
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            System.err.println("Error loading halls: " + e.getMessage());
        }

        // If no halls were loaded, add some default halls for testing
        if (halls.isEmpty()) {
            System.out.println("No halls found in database. Adding default halls.");
            addDefaultHalls();
        }
    }

    private void loadCateringFromDatabase() {
        catering.clear();

        try {
            // Connect to database
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 Statement stmt = conn.createStatement()) {

                String query = "SELECT id, name, image_path FROM products WHERE type = 'catering'";
                try (ResultSet rs = stmt.executeQuery(query)) {
                    while (rs.next()) {
                        int id = rs.getInt("id");
                        String name = rs.getString("name");
                        String imagePath = rs.getString("image_path");

                        // Get input stream for image
                        InputStream is = getClass().getResourceAsStream(imagePath);
                        if (is == null) {
                            System.err.println("Cannot find image: " + imagePath);
                            is = getClass().getResourceAsStream("/images/default.png");
                            if (is == null) {
                                System.err.println("Cannot find default image either!");
                                continue; // Skip this catering option
                            }
                        }

                        // Create image and product objects
                        Image image = new Image(is);
                        Product cateringOption = new Product(id, name, image);
                        catering.add(cateringOption);

                        // Close the input stream
                        is.close();

                        System.out.println("Loaded catering product: " + name);
                    }
                }
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            System.err.println("Error loading catering options: " + e.getMessage());
        }

        // If no catering options were loaded, add some default options for testing
        if (catering.isEmpty()) {
            System.out.println("No catering options found in database. Adding default options.");
            addDefaultCatering();
        }
    }

    // Helper method to add default products if database is empty
    private void addDefaultProducts() {
        try {
            // Create a default image
            InputStream is = getClass().getResourceAsStream("/images/default.png");
            if (is == null) {
                // If default image isn't found, create a blank image
                Image blankImage = new Image(new ByteArrayInputStream(new byte[0]));
                products.add(new Product(1, "Venue 1", blankImage));
                products.add(new Product(2, "Venue 2", blankImage));
                products.add(new Product(3, "Venue 3", blankImage));
            } else {
                Image defaultImage = new Image(is);
                products.add(new Product(1, "Empire Hotel", defaultImage));
                products.add(new Product(2, "Serikandi Hall", defaultImage));
                products.add(new Product(3, "Peak View Resort", defaultImage));
                is.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Helper method to add default halls if database is empty
    private void addDefaultHalls() {
        try {
            // Create a default image
            InputStream is = getClass().getResourceAsStream("/images/default.png");
            if (is == null) {
                // If default image isn't found, create a blank image
                Image blankImage = new Image(new ByteArrayInputStream(new byte[0]));
                halls.add(new Product(4, "Hall 1", blankImage));
                halls.add(new Product(5, "Hall 2", blankImage));
                halls.add(new Product(6, "Hall 3", blankImage));
            } else {
                Image defaultImage = new Image(is);
                halls.add(new Product(4, "Diamond Hall", defaultImage));
                halls.add(new Product(5, "Grand Ballroom", defaultImage));
                halls.add(new Product(6, "Garden Pavilion", defaultImage));
                is.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Helper method to add default catering options if database is empty
    private void addDefaultCatering() {
        try {
            // Create a default image
            InputStream is = getClass().getResourceAsStream("/images/default.png");
            if (is == null) {
                // If default image isn't found, create a blank image
                Image blankImage = new Image(new ByteArrayInputStream(new byte[0]));
                catering.add(new Product(7, "Catering 1", blankImage));
                catering.add(new Product(8, "Catering 2", blankImage));
                catering.add(new Product(9, "Catering 3", blankImage));
            } else {
                Image defaultImage = new Image(is);
                catering.add(new Product(7, "Luxury Catering", defaultImage));
                catering.add(new Product(8, "Traditional Catering", defaultImage));
                catering.add(new Product(9, "International Cuisine", defaultImage));
                is.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void navigateToHome(ActionEvent actionEvent) {
        try {
            // Load the homepage FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/homepage.fxml"));
            Parent homeRoot = loader.load();

            // Get the current stage
            Stage stage = (Stage) checklistButton.getScene().getWindow();

            // Replace the current scene
            Scene homeScene = new Scene(homeRoot, stage.getScene().getWidth(), stage.getScene().getHeight());
            stage.setScene(homeScene);
            stage.setTitle("Wedding Planning - Home");

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading home view: " + e.getMessage());
        }
    }

    public void navigateToItinerary(ActionEvent actionEvent) {
        try {
            // Load the Itinerary FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/itinerary.fxml"));
            Parent itineraryRoot = loader.load();

            // Get the current stage
            Stage stage = (Stage) checklistButton.getScene().getWindow();

            // Replace the current scene
            Scene itineraryScene = new Scene(itineraryRoot, stage.getScene().getWidth(), stage.getScene().getHeight());
            stage.setScene(itineraryScene);
            stage.setTitle("Wedding Planning - Itinerary");

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading itinerary view: " + e.getMessage());
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