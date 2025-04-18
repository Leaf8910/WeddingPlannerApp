package com;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Utility class to initialize the database from SQL script
 */
public class DatabaseInitializer {
    // Database connection parameters
    private static final String BASE_URL = "jdbc:mysql://localhost:3306/";
    private static final String DB_NAME = "wedding_planner";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Alif8611891";

    /**
     * Main method to run the database initialization
     */
    public static void main(String[] args) {
        try {
            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("MySQL JDBC Driver loaded successfully");

            // Run the initialization
            initializeDatabase();

            System.out.println("Database initialization completed successfully!");

        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Initialize the database and tables
     */
    public static void initializeDatabase() throws SQLException, IOException {
        // First, ensure the database exists
        try (Connection conn = DriverManager.getConnection(BASE_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DB_NAME);
            System.out.println("Database created or already exists: " + DB_NAME);
        }

        // Now connect to the database and run the setup script
        try (Connection conn = DriverManager.getConnection(BASE_URL + DB_NAME, DB_USER, DB_PASSWORD)) {
            runSqlScript(conn, "setup_database.sql");
        }
    }

    /**
     * Run SQL statements from a script file
     */
    private static void runSqlScript(Connection conn, String scriptFilePath) throws IOException, SQLException {
        try (BufferedReader reader = new BufferedReader(new FileReader(scriptFilePath))) {
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                // Skip comments and empty lines
                if (line.trim().isEmpty() || line.trim().startsWith("--")) {
                    continue;
                }

                sb.append(line);

                // If the line ends with a semicolon, execute the statement
                if (line.trim().endsWith(";")) {
                    String sql = sb.toString();

                    try (Statement stmt = conn.createStatement()) {
                        stmt.executeUpdate(sql);
                        System.out.println("Executed SQL: " + sql.substring(0, Math.min(50, sql.length())) + "...");
                    } catch (SQLException e) {
                        System.err.println("Error executing SQL: " + sql);
                        throw e;
                    }

                    sb.setLength(0); // Clear the string builder
                }
            }
        }
    }
}