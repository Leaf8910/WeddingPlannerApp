package com;

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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Callback;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;

public class Planning3Controller implements Initializable {
    // Top navigation buttons
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

    // Selection ComboBoxes
//    @FXML
//    private ComboBox<Product> decorComboBox;
//
//    @FXML
//    private ComboBox<Product> muaComboBox;

    @FXML
    private ComboBox<Product> vendorComboBox;

    @FXML
    private ComboBox<Product> dressComboBox;

    @FXML
    private ComboBox<Product> photographerComboBox;

    // Image Views
    @FXML
    private ImageView selectedImageView;

    @FXML
    private ImageView selectedImageView1;

    @FXML
    private ImageView selectedImageView2;

    @FXML
    private ImageView selectedImageView3;

    @FXML
    private ImageView selectedImageView4;

    // Action buttons
    @FXML
    private Button backButton;

    @FXML
    private Button confirmButton;

    // Lists to hold product data
//    private final ObservableList<Product> decorItems = FXCollections.observableArrayList();
//    private final ObservableList<Product> muaItems = FXCollections.observableArrayList();
    private final ObservableList<Product> vendorItems = FXCollections.observableArrayList();
    private final ObservableList<Product> dressItems = FXCollections.observableArrayList();
    private final ObservableList<Product> photographerItems = FXCollections.observableArrayList();

    // Database connection parameters - should match your existing configuration
    private static final String DB_URL = "jdbc:mysql://localhost:3306/product_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Alif8611891";

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

        // Load product data from database for each category
//        loadDecorFromDatabase();
//        loadMuaFromDatabase();
        loadVendorFromDatabase();
        loadDressFromDatabase();
        loadPhotographerFromDatabase();

        // Set up all ComboBoxes
//        setupComboBox(decorComboBox, decorItems);
//        setupComboBox(muaComboBox, muaItems);
        setupComboBox(vendorComboBox, vendorItems);
        setupComboBox(dressComboBox, dressItems);
        setupComboBox(photographerComboBox, photographerItems);

        // Add listeners for product selection changes
//        decorComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
//            if (newValue != null) {
//                selectedImageView.setImage(newValue.getImage());
//                // Adjust the image size as needed
//                selectedImageView.setFitWidth(120);
//                selectedImageView.setFitHeight(120);
//                selectedImageView.setPreserveRatio(true);
//            }
//        });
//
//        muaComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
//            if (newValue != null) {
//                selectedImageView1.setImage(newValue.getImage());
//                selectedImageView1.setFitWidth(120);
//                selectedImageView1.setFitHeight(120);
//                selectedImageView1.setPreserveRatio(true);
//            }
//        });

        vendorComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                selectedImageView2.setImage(newValue.getImage());
                selectedImageView2.setFitWidth(120);
                selectedImageView2.setFitHeight(120);
                selectedImageView2.setPreserveRatio(true);
            }
        });

        dressComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                selectedImageView3.setImage(newValue.getImage());
                selectedImageView3.setFitWidth(120);
                selectedImageView3.setFitHeight(120);
                selectedImageView3.setPreserveRatio(true);
            }
        });

        photographerComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                selectedImageView4.setImage(newValue.getImage());
                selectedImageView4.setFitWidth(120);
                selectedImageView4.setFitHeight(120);
                selectedImageView4.setPreserveRatio(true);
            }
        });

        // Select first items by default if available
//        if (!decorItems.isEmpty()) decorComboBox.getSelectionModel().selectFirst();
//        if (!muaItems.isEmpty()) muaComboBox.getSelectionModel().selectFirst();
        if (!vendorItems.isEmpty()) vendorComboBox.getSelectionModel().selectFirst();
        if (!dressItems.isEmpty()) dressComboBox.getSelectionModel().selectFirst();
        if (!photographerItems.isEmpty()) photographerComboBox.getSelectionModel().selectFirst();

        // Set up navigation button handlers
        homeButton.setOnAction(this::navigateToHome);
        planningButton.setOnAction(this::navigateToPlanning);
        checklistButton.setOnAction(this::navigateToChecklist);
        itineraryButton.setOnAction(this::navigateToItinerary);
        historyButton.setOnAction(this::navigateToHistory);
        logoutButton.setOnAction(this::logout);

        // Set up action button handlers
        if (backButton != null) {
            backButton.setOnAction(this::navigateBackToPlanning2);
        }

        if (confirmButton != null) {
            confirmButton.setOnAction(this::handleConfirm);
        }

        // Load existing planning data if available
        loadExistingPlanningData(planning);
    }

    private void logout(ActionEvent actionEvent) {
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

    private void loadExistingPlanningData(Planning planning) {
        if (planning != null) {
            // Try to match the selections to the combobox items
//            if (planning.getDecor() != null) {
//                for (Product product : decorItems) {
//                    if (product.getName().equals(planning.getDecor())) {
//                        decorComboBox.getSelectionModel().select(product);
//                        break;
//                    }
//                }
//            }
//
//            if (planning.getMUA() != null) {
//                for (Product product : muaItems) {
//                    if (product.getName().equals(planning.getMUA())) {
//                        muaComboBox.getSelectionModel().select(product);
//                        break;
//                    }
//                }
//            }

            if (planning.getWed_vendor() != null) {
                for (Product product : vendorItems) {
                    if (product.getName().equals(planning.getWed_vendor())) {
                        vendorComboBox.getSelectionModel().select(product);
                        break;
                    }
                }
            }

            // Dress needs to be implemented in the Planning class
            // if (planning.getDress() != null) { ... }

            if (planning.getPhotographer() != null) {
                for (Product product : photographerItems) {
                    if (product.getName().equals(planning.getPhotographer())) {
                        photographerComboBox.getSelectionModel().select(product);
                        break;
                    }
                }
            }

            if (planning.getDress() != null) {
                for (Product product : dressItems) {
                    if (product.getName().equals(planning.getDress())) {
                        dressComboBox.getSelectionModel().select(product);
                        break;
                    }
                }
            }
        }
    }

    @FXML
    private void navigateToHome(ActionEvent event) {
        try {
            // Load the homepage FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/homepage.fxml"));
            Parent homeRoot = loader.load();

            // Get the current stage
            Stage stage = (Stage) homeButton.getScene().getWindow();

            // Replace the current scene
            Scene homeScene = new Scene(homeRoot, stage.getScene().getWidth(), stage.getScene().getHeight());
            stage.setScene(homeScene);
            stage.setTitle("Wedding Planning - Home");

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not navigate to home page: " + e.getMessage());
        }
    }

    @FXML
    private void navigateToPlanning(ActionEvent event) {
        PlanningNavigationHandler.navigateToPlanning(event);
    }

    @FXML
    private void navigateToChecklist(ActionEvent event) {
        try {
            // Load the Checklist FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/checklist.fxml"));
            Parent checklistRoot = loader.load();

            // Get the current stage
            Stage stage = (Stage) checklistButton.getScene().getWindow();

            // Replace the current scene with the checklist scene
            Scene checklistScene = new Scene(checklistRoot, stage.getScene().getWidth(), stage.getScene().getHeight());
            stage.setScene(checklistScene);
            stage.setTitle("Wedding Planning - Checklist");

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not navigate to checklist page: " + e.getMessage());
        }
    }

    @FXML
    private void navigateToItinerary(ActionEvent event) {
        try {
            // Load the Itinerary FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/itinerary.fxml"));
            Parent itineraryRoot = loader.load();

            // Get the current stage
            Stage stage = (Stage) itineraryButton.getScene().getWindow();

            // Replace the current scene
            Scene itineraryScene = new Scene(itineraryRoot, stage.getScene().getWidth(), stage.getScene().getHeight());
            stage.setScene(itineraryScene);
            stage.setTitle("Wedding Planning - Itinerary");

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not navigate to itinerary page: " + e.getMessage());
        }
    }

    @FXML
    private void navigateToHistory(ActionEvent event) {
        try {
            // Load the History FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/history.fxml"));
            Parent historyRoot = loader.load();

            // Get the current stage
            Stage stage = (Stage) historyButton.getScene().getWindow();

            // Replace the current scene
            Scene historyScene = new Scene(historyRoot, stage.getScene().getWidth(), stage.getScene().getHeight());
            stage.setScene(historyScene);
            stage.setTitle("Wedding Planning - History");

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not navigate to history page: " + e.getMessage());
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

    @FXML
    private void navigateBackToPlanning2(ActionEvent event) {
        navigateTo("/planning2.fxml");
    }

    @FXML
    private void handleConfirm(ActionEvent event) {
        // Validate that all selections have been made
//        if (decorComboBox.getSelectionModel().isEmpty() ||
//                muaComboBox.getSelectionModel().isEmpty()) {
        if (
                vendorComboBox.getSelectionModel().isEmpty() ||
                dressComboBox.getSelectionModel().isEmpty() ||
                photographerComboBox.getSelectionModel().isEmpty()) {

            showAlert(Alert.AlertType.ERROR, "Incomplete Selections",
                    "Please make all selections before continuing");
            return;
        }

        // Get the current user
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null) {
            showAlert(Alert.AlertType.ERROR, "Authentication Error",
                    "You must be logged in to save planning details");
            return;
        }

        // Get the existing planning or create a new one
        Planning planning = PlanningDAO.getPlanningByUserId(currentUser.getId());

        if (planning == null) {
            // This should not normally happen, but handle it gracefully
            planning = new Planning();
            planning.setUserId(currentUser.getId());
        }

        // Update planning with additional details
//        planning.setDecor(decorComboBox.getSelectionModel().getSelectedItem().getName());
//        planning.setMUA(muaComboBox.getSelectionModel().getSelectedItem().getName());
        planning.setWed_vendor(vendorComboBox.getSelectionModel().getSelectedItem().getName());
        planning.setDress(dressComboBox.getSelectionModel().getSelectedItem().getName());
        planning.setPhotographer(photographerComboBox.getSelectionModel().getSelectedItem().getName());

        // Update the planning in the database
        boolean success = PlanningDAO.updatePlanning(planning);

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Planning Updated",
                    "Your planning details have been updated successfully!");
            // Navigate to the next page or home
            navigateTo("/itinerary.fxml");  // Or wherever you want to go next
        } else {
            showAlert(Alert.AlertType.ERROR, "Update Error",
                    "Failed to update planning details. Please try again.");
        }
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
                            imageView.setFitHeight(50);  // Smaller for combobox cells
                            imageView.setFitWidth(50);
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

    // Methods to load products from database
//    private void loadDecorFromDatabase() {
//        decorItems.clear();
//
//        try {
//            // Load MySQL JDBC driver if not already loaded
//            try {
//                Class.forName("com.mysql.cj.jdbc.Driver");
//            } catch (ClassNotFoundException e) {
//                System.err.println("MySQL JDBC Driver not found.");
//                e.printStackTrace();
//                return;
//            }
//
//            // Connect to database
//            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
//                 Statement stmt = conn.createStatement()) {
//
//                String query = "SELECT id, name, image_path FROM products WHERE type = 'decor'";
//                try (ResultSet rs = stmt.executeQuery(query)) {
//                    while (rs.next()) {
//                        int id = rs.getInt("id");
//                        String name = rs.getString("name");
//                        String imagePath = rs.getString("image_path");
//
//                        // Get input stream for image
//                        InputStream is = getClass().getResourceAsStream(imagePath);
//                        if (is == null) {
//                            System.err.println("Cannot find image: " + imagePath);
//                            // Try with a different path structure
//                            is = getClass().getResourceAsStream("/images/default.png");
//                            if (is == null) {
//                                System.err.println("Cannot find default image either!");
//                                continue; // Skip this product
//                            }
//                        }
//
//                        // Create image and product objects
//                        Image image = new Image(is);
//                        Product product = new Product(id, name, image);
//                        decorItems.add(product);
//
//                        // Close the input stream
//                        is.close();
//
//                        System.out.println("Loaded decor product: " + name);
//                    }
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            System.err.println("Error loading decor products: " + e.getMessage());
//        }
//
//        // If no products were loaded, add some default products for testing
//        if (decorItems.isEmpty()) {
//            System.out.println("No decor products found in database. Adding defaults.");
//            addDefaultProducts(decorItems, "decor");
//        }
//    }

//    private void loadMuaFromDatabase() {
//        muaItems.clear();
//
//        try {
//            // Load MySQL JDBC driver if not already loaded
//            try {
//                Class.forName("com.mysql.cj.jdbc.Driver");
//            } catch (ClassNotFoundException e) {
//                System.err.println("MySQL JDBC Driver not found.");
//                e.printStackTrace();
//                return;
//            }
//
//            // Connect to database
//            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
//                 Statement stmt = conn.createStatement()) {
//
//                String query = "SELECT id, name, image_path FROM products WHERE type = 'MUA'";
//                try (ResultSet rs = stmt.executeQuery(query)) {
//                    while (rs.next()) {
//                        int id = rs.getInt("id");
//                        String name = rs.getString("name");
//                        String imagePath = rs.getString("image_path");
//
//                        // Get input stream for image
//                        InputStream is = getClass().getResourceAsStream(imagePath);
//                        if (is == null) {
//                            System.err.println("Cannot find image: " + imagePath);
//                            // Try with a different path structure
//                            is = getClass().getResourceAsStream("/images/default.png");
//                            if (is == null) {
//                                System.err.println("Cannot find default image either!");
//                                continue; // Skip this product
//                            }
//                        }
//
//                        // Create image and product objects
//                        Image image = new Image(is);
//                        Product product = new Product(id, name, image);
//                        muaItems.add(product);
//
//                        // Close the input stream
//                        is.close();
//
//                        System.out.println("Loaded MUA product: " + name);
//                    }
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            System.err.println("Error loading MUA products: " + e.getMessage());
//        }
//
//        // If no products were loaded, add some default products for testing
//        if (muaItems.isEmpty()) {
//            System.out.println("No MUA products found in database. Adding defaults.");
//            addDefaultProducts(muaItems, "MUA");
//        }
//    }


    private void loadVendorFromDatabase() {
        vendorItems.clear();

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

                String query = "SELECT id, name, image_path FROM products WHERE type = 'vendor'";
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
                        vendorItems.add(product);

                        // Close the input stream
                        is.close();

                        System.out.println("Loaded vendor product: " + name);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading vendor products: " + e.getMessage());
        }

        // If no products were loaded, add some default products for testing
        if (vendorItems.isEmpty()) {
            System.out.println("No vendor products found in database. Adding defaults.");
            addDefaultProducts(vendorItems, "vendor");
        }
    }


    private void loadDressFromDatabase() {
        dressItems.clear();

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

                String query = "SELECT id, name, image_path FROM products WHERE type = 'dress'";
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
                        dressItems.add(product);

                        // Close the input stream
                        is.close();
                        System.out.println("Loaded dress product: " + name);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading dress products: " + e.getMessage());
        }

        // If no products were loaded, add some default products for testing
        if (dressItems.isEmpty()) {
            System.out.println("No dress products found in database. Adding defaults.");
            addDefaultProducts(dressItems, "dress");
        }
    }


    private void loadPhotographerFromDatabase() {
        photographerItems.clear();

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

                String query = "SELECT id, name, image_path FROM products WHERE type = 'photographer'";
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
                        photographerItems.add(product);

                        // Close the input stream
                        is.close();

                        System.out.println("Loaded photographer product: " + name);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading photographer products: " + e.getMessage());
        }

        // If no products were loaded, add some default products for testing
        if (photographerItems.isEmpty()) {
            System.out.println("No photographer products found in database. Adding defaults.");
            addDefaultProducts(photographerItems, "photographer");
        }
    }

    private void loadProductsFromDatabase(ObservableList<Product> itemsList, String productType) {
        itemsList.clear();

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

                String query = "SELECT id, name, image_path FROM products WHERE type = '" + productType + "'";
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
                        itemsList.add(product);

                        // Close the input stream
                        is.close();

                        System.out.println("Loaded " + productType + " product: " + name);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading " + productType + ": " + e.getMessage());
        }

        // If no products were loaded, add some default products for testing
        if (itemsList.isEmpty()) {
            System.out.println("No " + productType + " products found in database. Adding defaults.");
            addDefaultProducts(itemsList, productType);
        }
    }

    private void addDefaultProducts(ObservableList<Product> itemsList, String productType) {
        try {
            // Create a default image
            InputStream is = getClass().getResourceAsStream("/images/default.png");
            Image defaultImage;

            if (is == null) {
                // If default image isn't found, create a placeholder image
                defaultImage = new Image(getClass().getResourceAsStream("/images/placeholder.png"));
                if (defaultImage == null) {
                    throw new IOException("Could not load placeholder image");
                }
            } else {
                defaultImage = new Image(is);
                is.close();
            }

            // Add default products based on type
            switch (productType) {
//                case "decor":
//                    itemsList.add(new Product(101, "Classic Decor", defaultImage));
//                    itemsList.add(new Product(102, "Modern Decor", defaultImage));
//                    itemsList.add(new Product(103, "Rustic Decor", defaultImage));
//                    break;
//                case "mua":
//                    itemsList.add(new Product(201, "Professional MUA", defaultImage));
//                    itemsList.add(new Product(202, "Celebrity MUA", defaultImage));
//                    itemsList.add(new Product(203, "Freelance MUA", defaultImage));
//                    break;
                case "vendor":
                    itemsList.add(new Product(301, "Premium Vendor", defaultImage));
                    itemsList.add(new Product(302, "Budget Vendor", defaultImage));
                    itemsList.add(new Product(303, "Specialty Vendor", defaultImage));
                    break;

                case "dress":
                    itemsList.add(new Product(401, "Designer Dress", defaultImage));
                    itemsList.add(new Product(402, "Custom Dress", defaultImage));
                    itemsList.add(new Product(403, "Rental Dress", defaultImage));
                    break;
                case "photographer":
                    itemsList.add(new Product(501, "Professional Photographer", defaultImage));
                    itemsList.add(new Product(502, "Video & Photo Package", defaultImage));
                    itemsList.add(new Product(503, "Wedding Specialist", defaultImage));
                    break;
                default:
                    itemsList.add(new Product(999, "Default Item", defaultImage));
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error creating default products for " + productType);
        }
    }
}