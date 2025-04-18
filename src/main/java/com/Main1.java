package com;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main1 extends Application {

    public static void loadPlanningScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(Main1.class.getResource("/planning1.fxml"));
            Parent root = loader.load();

            Stage planninglistStage = new Stage();
            planninglistStage.setTitle("Planning Page");
            planninglistStage.setScene(new Scene(root));
            planninglistStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading checklist FXML: " + e.getMessage());
        }
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            // Load the main planning screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/planning1.fxml"));
            Parent root = loader.load();

            // Set up the primary stage
            primaryStage.setTitle("DreamDay Wedding Planner");
            primaryStage.setScene(new Scene(root));
            primaryStage.setMaximized(true);
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading FXML: " + e.getMessage());
        }
    }

//    public static void loadChecklistScreen() {
//        try {
//            FXMLLoader loader = new FXMLLoader(Main1.class.getResource("/checklist.fxml"));
//            Parent root = loader.load();
//
//            Stage checklistStage = new Stage();
//            checklistStage.setTitle("Wedding Checklist");
//            checklistStage.setScene(new Scene(root));
//            checklistStage.show();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            System.err.println("Error loading checklist FXML: " + e.getMessage());
//        }
//    }



    public static void main(String[] args) {
        // Make sure MySQL JDBC driver is loaded
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("MySQL JDBC Driver loaded successfully");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found: " + e.getMessage());
            return; // Exit if driver not found
        }

        // Launch the JavaFX application
        launch(args);
    }
}