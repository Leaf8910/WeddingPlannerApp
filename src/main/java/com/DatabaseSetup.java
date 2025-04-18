package com;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DatabaseSetup {
    // MySQL connection parameters
    private static final String DB_URL = "jdbc:mysql://localhost:3306/";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Alif8611891";

    public static void main(String[] args) {
        try {
            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Create database if it doesn't exist
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 Statement stmt = conn.createStatement()) {

                stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS product_db");
                System.out.println("Database created or already exists.");
            }

            // Connect to the specific database and create table
            try (Connection conn = DriverManager.getConnection(DB_URL + "product_db", DB_USER, DB_PASSWORD);
                 Statement stmt = conn.createStatement()) {

                // Create table
                String createTableSQL = "CREATE TABLE IF NOT EXISTS products (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY," +
                        "name VARCHAR(100) NOT NULL," +
                        "image_path VARCHAR(255) NOT NULL)";
                stmt.executeUpdate(createTableSQL);

                // Clear existing data
                stmt.executeUpdate("DELETE FROM products");

                // Insert sample data
                String[] insertQueries = {
                        "INSERT INTO products (type, name, image_path) VALUES ('venue','Serikandi', '/images/serikandi.jpeg')",
                        "INSERT INTO products (type, name, image_path) VALUES ('venue','Peak', '/images/peak.jpg')",
                        "INSERT INTO products (type, name, image_path) VALUES ('venue','Empire', '/images/empire.jpg')",
                        "INSERT INTO products (type, name, image_path) VALUES ('venue','Yayasan', '/images/yayasan.jpeg')",
                        "INSERT INTO products (type, name, image_path) VALUES ('hall','SerikandiHall', '/images/town.jpg')",
                        "INSERT INTO products (type, name, image_path) VALUES ('hall','PeakHall', '/images/car.jpg')",
                        "INSERT INTO products (type, name, image_path) VALUES ('hall','EmpireHall', '/images/town2.jpg')",
                        "INSERT INTO products (type, name, image_path) VALUES ('hall','YayasanHall', '/images/town3.jpg')",
                        "INSERT INTO products (type, name, image_path) VALUES ('catering','SerikandiHall', '/images/car2.jpg')",
                        "INSERT INTO products (type, name, image_path) VALUES ('catering','PeakHall', '/images/car3.jpg')",
                        "INSERT INTO products (type, name, image_path) VALUES ('catering','EmpireHall', '/images/car4.jpg')",
                        "INSERT INTO products (type, name, image_path) VALUES ('catering','YayasanHall', '/images/hall1.jpg')"
                };

                for (String query : insertQueries) {
                    stmt.executeUpdate(query);
                }

                System.out.println("Database initialized with sample data.");
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.err.println("MySQL JDBC Driver not found: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to set up database: " + e.getMessage());
        }
    }
}