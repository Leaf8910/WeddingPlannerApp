package com;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class LoginController {
    // Login form elements
    @FXML
    private VBox loginBox;
    @FXML
    private TextField loginUsername;
    @FXML
    private PasswordField loginPassword;
    @FXML
    private Label loginMessageLabel;

    // Register form elements
    @FXML
    private VBox registerBox;
    @FXML
    private TextField registerEmail;
    @FXML
    private TextField registerUsername;
    @FXML
    private PasswordField registerPassword;
    @FXML
    private PasswordField registerConfirmPassword;
    @FXML
    private Label registerMessageLabel;

    // Database connection parameters - using the same ones as in your existing code
    private static final String DB_URL = "jdbc:mysql://localhost:3306/wedding_planner";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Alif8611891";

    @FXML
    public void initialize() {
        // Ensure login form is visible on startup
        loginBox.setVisible(true);
        registerBox.setVisible(false);

        // Make sure the database and tables exist
        createDatabaseIfNeeded();
    }

    private void createDatabaseIfNeeded() {
        try {
            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // First, create the database if it doesn't exist
            try (Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/", DB_USER, DB_PASSWORD);
                 Statement stmt = conn.createStatement()) {

                stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS wedding_planner");
            }

            // Then create the users table if it doesn't exist
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 Statement stmt = conn.createStatement()) {

                String createTableSQL =
                        "CREATE TABLE IF NOT EXISTS users (" +
                                "id INT AUTO_INCREMENT PRIMARY KEY," +
                                "username VARCHAR(50) NOT NULL UNIQUE," +
                                "email VARCHAR(100) NOT NULL UNIQUE," +
                                "password VARCHAR(255) NOT NULL," +
                                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                                ")";

                stmt.executeUpdate(createTableSQL);
            }

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error",
                    "Failed to initialize database: " + e.getMessage());
        }
    }

    @FXML
    public void handleLogin(ActionEvent event) {
        String username = loginUsername.getText().trim();
        String password = loginPassword.getText();

        // Basic validation
        if (username.isEmpty() || password.isEmpty()) {
            loginMessageLabel.setText("Please enter both username and password");
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT * FROM users WHERE username = ? AND password = ?")) {

            pstmt.setString(1, username);
            pstmt.setString(2, password); // In a real app, use password hashing!

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // Login successful
                loginMessageLabel.setText("");

                // Create User object and store in SessionManager
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));

                // Store the user in session manager
                SessionManager.getInstance().setCurrentUser(user);

                // Navigate to home page
                navigateToHomePage();
            } else {
                // Login failed
                loginMessageLabel.setText("Invalid username or password");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            loginMessageLabel.setText("Database error: " + e.getMessage());
        }
    }

    @FXML
    public void handleRegister(ActionEvent event) {
        String email = registerEmail.getText().trim();
        String username = registerUsername.getText().trim();
        String password = registerPassword.getText();
        String confirmPassword = registerConfirmPassword.getText();

        // Validation
        if (email.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            registerMessageLabel.setText("Please fill out all fields");
            return;
        }

        if (!email.contains("@") || !email.contains(".")) {
            registerMessageLabel.setText("Please enter a valid email address");
            return;
        }

        if (username.length() < 4) {
            registerMessageLabel.setText("Username must be at least 4 characters long");
            return;
        }

        if (password.length() < 6) {
            registerMessageLabel.setText("Password must be at least 6 characters long");
            return;
        }

        if (!password.equals(confirmPassword)) {
            registerMessageLabel.setText("Passwords do not match");
            return;
        }

        // All validation passed, try to register the user
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT INTO users (username, email, password) VALUES (?, ?, ?)")) {

            pstmt.setString(1, username);
            pstmt.setString(2, email);
            pstmt.setString(3, password); // In a real app, use password hashing!

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                // Registration successful
                showAlert(Alert.AlertType.INFORMATION, "Registration Successful",
                        "You can now log in with your credentials");
                showLoginForm(null);
            } else {
                registerMessageLabel.setText("Failed to register. Please try again.");
            }

        } catch (SQLException e) {
            e.printStackTrace();

            if (e.getMessage().contains("Duplicate entry")) {
                if (e.getMessage().contains("username")) {
                    registerMessageLabel.setText("Username already exists");
                } else if (e.getMessage().contains("email")) {
                    registerMessageLabel.setText("Email already registered");
                } else {
                    registerMessageLabel.setText("Registration failed: " + e.getMessage());
                }
            } else {
                registerMessageLabel.setText("Database error: " + e.getMessage());
            }
        }
    }

    @FXML
    public void showLoginForm(ActionEvent event) {
        loginBox.setVisible(true);
        registerBox.setVisible(false);
        loginMessageLabel.setText("");
        loginUsername.clear();
        loginPassword.clear();
    }

    @FXML
    public void showRegisterForm(ActionEvent event) {
        loginBox.setVisible(false);
        registerBox.setVisible(true);
        registerMessageLabel.setText("");
        registerEmail.clear();
        registerUsername.clear();
        registerPassword.clear();
        registerConfirmPassword.clear();
    }

    private void navigateToHomePage() {
        try {
            // Load the homepage FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/homepage.fxml"));
            Parent root = loader.load();

            // Get controller and pass current user if needed
            HomePageController controller = loader.getController();
            // The controller can now access the current user via SessionManager

            // Get the current stage
            Stage stage = (Stage) loginUsername.getScene().getWindow();

            // Set the new scene
            Scene scene = new Scene(root, 800, 600);
            stage.setScene(scene);
            stage.setTitle("Wedding Planner - Home");

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error",
                    "Could not navigate to homepage: " + e.getMessage());
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