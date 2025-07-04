package com;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Planning entities
 */
public class PlanningDAO {
    // Using the same database connection parameters as in the rest of the app
    private static final String DB_URL = "jdbc:mysql://localhost:3306/wedding_planner";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Alif8611891";

    /**
     * Get a database connection
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    /**
     * Save planning details to the database
     * @param planning The planning details to save
     * @return true if successful, false otherwise
     */
    public static boolean savePlanning(Planning planning) {
        String sql = "INSERT INTO planning (user_id, venue, hall, catering, set1, month, day) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, planning.getUserId());
            stmt.setString(2, planning.getVenue());
            stmt.setString(3, planning.getHall());
            stmt.setString(4, planning.getCatering());
            stmt.setString(5, planning.getSet());
            stmt.setString(6, planning.getMonth());
            stmt.setString(7, planning.getDay());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                // Get the generated ID
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    planning.setId(rs.getInt(1));
                }
                return true;
            }

            return false;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Update existing planning details
     * @param planning The planning details to update
     * @return true if successful, false otherwise
     */
    public static boolean updatePlanning(Planning planning) {
        String sql = "UPDATE planning SET venue = ?, hall = ?, catering = ?, set1 = ?, month = ?, day = ? WHERE id = ? AND user_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, planning.getVenue());
            stmt.setString(2, planning.getHall());
            stmt.setString(3, planning.getCatering());
            stmt.setString(4, planning.getSet());
            stmt.setString(5, planning.getMonth());
            stmt.setString(6, planning.getDay());
            stmt.setInt(7, planning.getId());
            stmt.setInt(8, planning.getUserId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get planning details for a specific user
     * @param userId The ID of the user
     * @return The planning details or null if not found
     */
    public static Planning getPlanningByUserId(int userId) {
        String sql = "SELECT * FROM planning WHERE user_id = ? ORDER BY created_at DESC LIMIT 1";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Planning planning = new Planning();
                planning.setId(rs.getInt("id"));
                planning.setUserId(rs.getInt("user_id"));
                planning.setVenue(rs.getString("venue"));
                planning.setHall(rs.getString("hall"));
                planning.setCatering(rs.getString("catering"));
                planning.setSet(rs.getString("set1"));
                planning.setMonth(rs.getString("month"));
                planning.setDay(rs.getString("day"));

                // Set default values for the missing fields
                planning.setDecor("Not specified");
                planning.setMUA("Not specified");
                planning.setWed_vendor("Not specified");
                planning.setPhotographer("Not specified");

                return planning;
            }

            return null;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Delete planning details
     * @param id The ID of the planning details to delete
     * @param userId The ID of the user (for security)
     * @return true if successful, false otherwise
     */
    public static boolean deletePlanning(int id, int userId) {
        String sql = "DELETE FROM planning WHERE id = ? AND user_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.setInt(2, userId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get all planning details for a user
     * @param userId The ID of the user
     * @return List of planning details
     */
    public static List<Planning> getAllPlanningsByUserId(int userId) {
        List<Planning> planningList = new ArrayList<>();
        String sql = "SELECT * FROM planning WHERE user_id = ? ORDER BY created_at DESC";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Planning planning = new Planning();
                planning.setId(rs.getInt("id"));
                planning.setUserId(rs.getInt("user_id"));
                planning.setVenue(rs.getString("venue"));
                planning.setHall(rs.getString("hall"));
                planning.setCatering(rs.getString("catering"));
                planning.setSet(rs.getString("set1"));
                planning.setMonth(rs.getString("month"));
                planning.setDay(rs.getString("day"));
                planning.setDecor(rs.getString("decor"));
                planning.setMUA(rs.getString("MUA"));
                planning.setWed_vendor(rs.getString("wed_vendor"));
                planning.setPhotographer(rs.getString("photographer"));

                planningList.add(planning);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return planningList;
    }
}