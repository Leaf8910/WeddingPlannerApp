package com;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import java.time.LocalDate;

public class HomePageController {
    @FXML private ImageView groomIcon;
    @FXML private TextField groomName;
    @FXML private TextField brideName;
    @FXML private DatePicker weddingDateFrom;
    @FXML private DatePicker weddingDateTo;
    @FXML private TextField attendantsCount;
    @FXML private ComboBox<String> budgetSelector;
    @FXML private Button confirmButton;


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
}