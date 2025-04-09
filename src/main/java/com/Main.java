package com;
//package com.example.weddingplanner1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {

        // Load the FXML file with proper path
        Parent root = FXMLLoader.load(getClass().getResource("/homepage.fxml"));
        // Optional: Get the controller instance if needed
//        HomePageController controller = loader.getController();
//        // Load the FXML file
//        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));

        // Set up the stage
        primaryStage.setTitle("Wedding Planner");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }

    public static void main(String[] args) {
        // Load MySQL JDBC driver
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found.");
            e.printStackTrace();
            return;
        }

        launch(args);
    }
}
//BorderPane root = (BorderPane)FXMLLoader.load(getClass().getResource("homepage.fxml"));