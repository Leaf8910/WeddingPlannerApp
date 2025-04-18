package com;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;

import java.io.IOException;

public class PlanningNavigationHandler {

    /**
     * Navigates to the Planning page from any event source.
     *
     * @param event The ActionEvent that triggered the navigation
     */
    @FXML
    public static void navigateToPlanning(ActionEvent event) {
        try {
            // Load the Planning FXML
            FXMLLoader loader = new FXMLLoader(PlanningNavigationHandler.class.getResource("/planning1.fxml"));
            Parent planningRoot = loader.load();

            // Get the current stage from the event source
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Create and set the new scene
            Scene planningScene = new Scene(planningRoot, stage.getScene().getWidth(), stage.getScene().getHeight());
            stage.setScene(planningScene);
            stage.setTitle("Wedding Planning - Planning");

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading planning view: " + e.getMessage());
        }
    }
}