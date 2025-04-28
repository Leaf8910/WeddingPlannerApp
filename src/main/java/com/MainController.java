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
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
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
    private ImageView selectedImageView3;

    @FXML
    private Menu checklistMenu;

    @FXML
    private Button checklistButton;

    @FXML
    private Button planninglistButton;

    @FXML
    private ComboBox<Product> setComboBox;

    @FXML
    private ComboBox<String> monthComboBox;

    @FXML
    private ComboBox<String> dayComboBox;

    @FXML
    private Button confirmButton;

    @FXML
    private Label errorMessageLabel;

    @FXML private Label venueDescription;

    @FXML private Label totalPriceLabel;

    @FXML private Label venuePriceLabel;


    @FXML private Label hallDescription;
    @FXML private Label hallPriceLabel;

    @FXML private Label cateringDescription;
    @FXML private Label cateringPriceLabel;

    @FXML private Label setDescription;
    @FXML private Label setPriceLabel;

    // Add a new label to show the per-pax calculation
    @FXML private Label setPaxCalculationLabel;

    private double totalPrice = 0.0;
    private double venuePrice = 0.0;
    private double hallPrice = 0.0;
    private double cateringPrice = 0.0;
    private double setPrice = 0.0;
    private double setPaxPrice = 0.0; // New field for set * pax calculation
    @FXML
    private Button historyButton;
    @FXML private Button logoutButton;

    // Variable to store attendants count from wedding details
    private int attendantsCount = 0;

    private ObservableList<Product> products = FXCollections.observableArrayList();
    private ObservableList<Product> halls = FXCollections.observableArrayList();
    private ObservableList<Product> catering = FXCollections.observableArrayList();
    private ObservableList<Product> set = FXCollections.observableArrayList();
    private final ObservableList<String> months = FXCollections.observableArrayList(
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
    );
    private final ObservableList<String> days = FXCollections.observableArrayList(
            "1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
            "11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
            "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"
    );

    private static final String DB_URL = "jdbc:mysql://localhost:3306/wedding_planner";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Alif8611891";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        loadAttendantsCount();
        products = FXCollections.observableArrayList(DatabaseUtil.loadProductsFromDatabase("venue"));
        halls = FXCollections.observableArrayList(DatabaseUtil.loadHallsFromDatabase("halls"));
        catering = FXCollections.observableArrayList(DatabaseUtil.loadCateringFromDatabase("catering"));
        set = FXCollections.observableArrayList(DatabaseUtil.loadSetFromDatabase("set"));

        setupComboBox(productComboBox, products);
        setupComboBox(hallComboBox, halls);
        setupComboBox(cateringComboBox, catering);
        setupComboBox(setComboBox, catering);

        if (setComboBox != null) {
            setupComboBox(setComboBox, set);
        }

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

                if (venueDescription != null) {
                    venueDescription.setText(newValue.getDescription());
                }

                venuePrice = newValue.getPrice();
                venuePriceLabel.setText(String.format("$%.2f", venuePrice));
                updateTotalPrice();
            }
        });

        hallComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                selectedImageView1.setImage(newValue.getImage());
                selectedImageView1.setFitWidth(200);
                selectedImageView1.setFitHeight(200);
                selectedImageView1.setPreserveRatio(true);

                if (hallDescription != null) {
                    hallDescription.setText(newValue.getDescription());
                }
                hallPrice = newValue.getPrice();
                hallPriceLabel.setText(String.format("$%.2f", hallPrice));
                updateTotalPrice();
            }
        });
        cateringComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                selectedImageView2.setImage(newValue.getImage());
                selectedImageView2.setFitWidth(200);
                selectedImageView2.setFitHeight(200);
                selectedImageView2.setPreserveRatio(true);

                if (cateringDescription != null) {
                    cateringDescription.setText(newValue.getDescription());
                }
                cateringPrice = newValue.getPrice();
                cateringPriceLabel.setText(String.format("$%.2f", cateringPrice));
                updateTotalPrice();
            }
        });

        setComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                selectedImageView3.setImage(newValue.getImage());
                // Adjust the image size as needed
                selectedImageView3.setFitWidth(200);
                selectedImageView3.setFitHeight(200);
                selectedImageView3.setPreserveRatio(true);

                if (setDescription != null) {
                    setDescription.setText(newValue.getDescription());
                }
                setPrice = newValue.getPrice();
                setPriceLabel.setText(String.format("$%.2f", setPrice));
                calculateSetPaxPrice();
                updateTotalPrice();
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

        if (!set.isEmpty()) {
            setComboBox.getSelectionModel().selectFirst();
        }

        // Set up confirm button handler
        if (confirmButton != null) {
            confirmButton.setOnAction(this::handleConfirm);
        }
        loadExistingPlanningData();
    }

    private void loadAttendantsCount() {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null) return;

        WeddingDetails weddingDetails = WeddingDetailsDAO.getWeddingDetailsByUserId(currentUser.getId());

        if (weddingDetails != null) {
            attendantsCount = weddingDetails.getAttendantsCount();
            System.out.println("Loaded attendants count: " + attendantsCount);

            // If a set is already selected, recalculate its price
            if (setComboBox != null && setComboBox.getSelectionModel().getSelectedItem() != null) {
                calculateSetPaxPrice();
            }
        }
    }

    /**
     * Calculate the set price based on pax count
     */
    private void calculateSetPaxPrice() {
        if (attendantsCount > 0) {
            // Calculate set price per person
            setPaxPrice = setPrice * attendantsCount;
            if (setPaxCalculationLabel != null) {
                setPaxCalculationLabel.setText(String.format("Set × %d pax = $%.2f",
                        attendantsCount, setPaxPrice));
            }
            System.out.println("Set price per person calculation: " + setPrice + " × " +
                    attendantsCount + " = " + setPaxPrice);
        } else {
            // No pax information, use the base price
            setPaxPrice = setPrice;

            if (setPaxCalculationLabel != null) {
                setPaxCalculationLabel.setText("No pax information available");
            }
        }
    }

    private void loadExistingPlanningData() {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null) return;

        Planning existingPlanning = PlanningDAO.getPlanningByUserId(currentUser.getId());
        if (existingPlanning != null) {
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

            // Fix this part
            for (Product setItem : set) {
                if (setItem.getName().equals(existingPlanning.getSet())) {
                    setComboBox.getSelectionModel().select(setItem);  // Select the Product object, not ID
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/checklist.fxml"));
            Parent checklistRoot = loader.load();
            Stage stage;
            if (checklistButton != null) {
                stage = (Stage) checklistButton.getScene().getWindow();
            } else {
                MenuItem checklistMenuItem = null;
                stage = (Stage) checklistMenuItem.getParentPopup().getOwnerWindow();
            }
            Scene checklistScene = new Scene(checklistRoot, stage.getScene().getWidth(), stage.getScene().getHeight());
            stage.setScene(checklistScene);
            stage.setTitle("Wedding Planning - Checklist");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading checklist view: " + e.getMessage());
        }
    }

    @FXML
    public void navigateToPlanning(ActionEvent event) {
        PlanningNavigationHandler.navigateToPlanning(event);
    }

    @FXML
    private void handleConfirm(ActionEvent event) {
        if (productComboBox.getSelectionModel().isEmpty() ||
                hallComboBox.getSelectionModel().isEmpty() ||
                cateringComboBox.getSelectionModel().isEmpty() ||
                setComboBox.getSelectionModel().isEmpty() ||
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

        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null) {
            showAlert(Alert.AlertType.ERROR, "Authentication Error",
                    "You must be logged in to save planning details");
            return;
        }

        String venue = productComboBox.getSelectionModel().getSelectedItem().getName();
        String hall = hallComboBox.getSelectionModel().getSelectedItem().getName();
        String cateringChoice = cateringComboBox.getSelectionModel().getSelectedItem().getName();
        String setChoice = setComboBox.getSelectionModel().getSelectedItem().getName();
        String month = monthComboBox != null ? monthComboBox.getValue() : "";
        String day = dayComboBox != null ? dayComboBox.getValue() : "";

        String decor = "";
        String MUA = "";
        String wed_vendor = "";
        String photographer = "";
        String dress = "";

        Planning existingPlanning = PlanningDAO.getPlanningByUserId(currentUser.getId());
        Planning planning;

        if (existingPlanning != null) {
            planning = existingPlanning;
            planning.setVenue(venue);
            planning.setHall(hall);
            planning.setCatering(cateringChoice);
            planning.setSet(setChoice);
            planning.setMonth(month);
            planning.setDay(day);
        } else {
            planning = new Planning(
                    currentUser.getId(),
                    venue,
                    hall,
                    cateringChoice,
                    setChoice,
                    month,
                    day,
                    decor,
                    MUA,
                    wed_vendor,
                    photographer,
                    dress
            );
        }

        planning.setVenuePrice(venuePrice);
        planning.setHallPrice(hallPrice);
        planning.setCateringPrice(cateringPrice);
        planning.setSetPrice(setPrice);
        planning.setSetPaxPrice(setPaxPrice);

        // Add set_pax_price to the total
        double calculatedTotal = venuePrice + hallPrice + cateringPrice + setPaxPrice;
        planning.setTotalPrice(calculatedTotal);

        // Other prices will be set in planning2 and planning3
        planning.setDecorPrice(0.0);
        planning.setMuaPrice(0.0);
        planning.setVendorPrice(0.0);
        planning.setPhotographerPrice(0.0);
        planning.setDressPrice(0.0);
        SessionManager.getInstance().setAttribute("currentPlanning", planning);

        // Debug output to verify prices
        System.out.println("Storing planning with prices:");
        System.out.println("  Venue: " + venue + " - $" + venuePrice);
        System.out.println("  Hall: " + hall + " - $" + hallPrice);
        System.out.println("  Catering: " + cateringChoice + " - $" + cateringPrice);
        System.out.println("  Set: " + setChoice + " - $" + setPrice);
        System.out.println("  Set × Pax (" + attendantsCount + "): $" + setPaxPrice);
        System.out.println("  Total: $" + calculatedTotal);
        navigateToPlanning2();
    }

    private void navigateToPlanning2() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/planning2.fxml"));
            Parent planning2Root = loader.load();

            Stage stage = (Stage) confirmButton.getScene().getWindow();
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
                            setText(product.getName() + " - $" + String.format("%.2f", product.getPrice()));
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
                    // Show name and price in the selected value
                    setText(product.getName() + " - $" + String.format("%.2f", product.getPrice()));
                    imageView.setImage(product.getImage());
                    imageView.setFitHeight(24);
                    imageView.setFitWidth(24);
                    imageView.setPreserveRatio(true);
                    setGraphic(imageView);
                }
            }
        });
        comboBox.setItems(items);
    }

    private void updateTotalPrice() {
        // Calculate total using setPaxPrice instead of setPrice
        totalPrice = venuePrice + hallPrice + cateringPrice + setPaxPrice;

        // Update the total price display
        if (totalPriceLabel != null) {
            totalPriceLabel.setText(String.format("$%.2f", totalPrice));
        }

        // For debugging
        System.out.println("Total price updated: $" + String.format("%.2f", totalPrice));
        System.out.println("  Venue: $" + String.format("%.2f", venuePrice));
        System.out.println("  Hall: $" + String.format("%.2f", hallPrice));
        System.out.println("  Catering: $" + String.format("%.2f", cateringPrice));
        System.out.println("  Set: $" + String.format("%.2f", setPrice));
        System.out.println("  Set × Pax (" + attendantsCount + "): $" + String.format("%.2f", setPaxPrice));
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

    @FXML
    private void navigateToHistory(ActionEvent event) {
        try {
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

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void logout() {
        SessionManager.getInstance().logout();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
            Parent loginRoot = loader.load();
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            Scene loginScene = new Scene(loginRoot, stage.getScene().getWidth(), stage.getScene().getHeight());
            stage.setScene(loginScene);
            stage.setTitle("Wedding Planner - Login");

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading login view: " + e.getMessage());
        }
    }
}