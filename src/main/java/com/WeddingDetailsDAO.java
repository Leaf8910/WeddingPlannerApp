package com;

import java.sql.*;
import java.time.LocalDate;

/**
 * Data Access Object for WeddingDetails
 */
public class WeddingDetailsDAO {
    // Database connection parameters
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
     * Get wedding details for a specific user
     * @param userId The ID of the user
     * @return The wedding details or null if not found
     */
    public static WeddingDetails getWeddingDetailsByUserId(int userId) {
        String sql = "SELECT * FROM wedding_details WHERE user_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                WeddingDetails details = new WeddingDetails();
                details.setId(rs.getInt("id"));
                details.setUserId(rs.getInt("user_id"));
                details.setGroomName(rs.getString("groom_name"));
                details.setBrideName(rs.getString("bride_name"));

                // Get dates from database
                Date dateFrom = rs.getDate("wedding_date_from");
                if (dateFrom != null) {
                    details.setWeddingDateFrom(dateFrom.toLocalDate());
                }

                Date dateTo = rs.getDate("wedding_date_to");
                if (dateTo != null) {
                    details.setWeddingDateTo(dateTo.toLocalDate());
                }

                details.setAttendantsCount(rs.getInt("attendants_count"));
                details.setBudget(rs.getString("budget"));

                return details;
            }

            return null;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Save wedding details to the database
     * @param details The wedding details to save
     * @return true if successful, false otherwise
     */
    public static boolean saveWeddingDetails(WeddingDetails details) {
        String sql = "INSERT INTO wedding_details (id, user_id, groom_name, bride_name, wedding_date_from, " +
                "wedding_date_to, attendants_count, budget) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, details.getId()); // Set the ID explicitly
            stmt.setInt(2, details.getUserId());
            stmt.setString(3, details.getGroomName());
            stmt.setString(4, details.getBrideName());

            System.out.println("Attempting to save wedding details: " + details);
            System.out.println("SQL: " + sql);
            System.out.println("userId: " + details.getUserId());
            // Set dates
            if (details.getWeddingDateFrom() != null) {
                stmt.setDate(5, Date.valueOf(details.getWeddingDateFrom()));
            } else {
                stmt.setNull(5, Types.DATE);
            }

            if (details.getWeddingDateTo() != null) {
                stmt.setDate(6, Date.valueOf(details.getWeddingDateTo()));
            } else {
                stmt.setNull(6, Types.DATE);
            }



            stmt.setInt(7, details.getAttendantsCount());
            stmt.setString(8, details.getBudget());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                // Get the generated ID
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    details.setId(rs.getInt(1));
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
     * Update existing wedding details
     * @param details The wedding details to update
     * @return true if successful, false otherwise
     */
    public static boolean updateWeddingDetails(WeddingDetails details) {
        String sql = "UPDATE wedding_details SET groom_name = ?, bride_name = ?, wedding_date_from = ?, " +
                "wedding_date_to = ?, attendants_count = ?, budget = ? WHERE id = ? AND user_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, details.getGroomName());
            stmt.setString(2, details.getBrideName());

            // Set dates
            if (details.getWeddingDateFrom() != null) {
                stmt.setDate(3, Date.valueOf(details.getWeddingDateFrom()));
            } else {
                stmt.setNull(3, Types.DATE);
            }

            if (details.getWeddingDateTo() != null) {
                stmt.setDate(4, Date.valueOf(details.getWeddingDateTo()));
            } else {
                stmt.setNull(4, Types.DATE);
            }

            stmt.setInt(5, details.getAttendantsCount());
            stmt.setString(6, details.getBudget());
            stmt.setInt(7, details.getId());
            stmt.setInt(8, details.getUserId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static WeddingDetails getWeddingDetailsByPlanningId(int planningId) {
        String sql = "SELECT wd.* FROM wedding_details wd " +
                "JOIN planning p ON wd.id = p.wedding_details_id " +
                "WHERE p.id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, planningId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                WeddingDetails details = new WeddingDetails();
                details.setId(rs.getInt("id"));
                details.setUserId(rs.getInt("user_id"));
                details.setGroomName(rs.getString("groom_name"));
                details.setBrideName(rs.getString("bride_name"));

                // Get dates from database
                Date dateFrom = rs.getDate("wedding_date_from");
                if (dateFrom != null) {
                    details.setWeddingDateFrom(dateFrom.toLocalDate());
                }

                Date dateTo = rs.getDate("wedding_date_to");
                if (dateTo != null) {
                    details.setWeddingDateTo(dateTo.toLocalDate());
                }

                details.setAttendantsCount(rs.getInt("attendants_count"));
                details.setBudget(rs.getString("budget"));

                return details;
            }

            return null;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static int getNextAvailableId() {
        int maxId = 0;
        String sql = "SELECT MAX(id) FROM wedding_details";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                maxId = rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return maxId + 1;
    }
}