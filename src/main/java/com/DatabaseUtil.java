package com;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DatabaseUtil {
    private static final String URL = "jdbc:mysql://localhost:3306/wedding_planner";
    private static final String USER = "root";
    private static final String PASSWORD = "Alif8611891";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void saveWeddingDetails(WeddingDetails details) {
        String sql = "INSERT INTO wedding_details (groom_name, bride_name, wedding_date_from, " +
                "wedding_date_to, attendants_count, budget_range, user_id) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, details.getGroomName());
            stmt.setString(2, details.getBrideName());
            stmt.setDate(3, Date.valueOf(details.getWeddingDateFrom()));
            stmt.setDate(4, Date.valueOf(details.getWeddingDateTo()));
            stmt.setInt(5, details.getAttendantsCount());
            stmt.setString(6, details.getBudgetRange());

            // Get the current user from session manager and set user_id
            User currentUser = SessionManager.getInstance().getCurrentUser();
            if (currentUser != null) {
                stmt.setInt(7, currentUser.getId());
            } else {
                stmt.setNull(7, java.sql.Types.INTEGER);
            }

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<WeddingDetails> getAllWeddingDetails() {
        List<WeddingDetails> detailsList = new ArrayList<>();

        // Get current user ID
        User currentUser = SessionManager.getInstance().getCurrentUser();
        int userId = (currentUser != null) ? currentUser.getId() : -1;

        // Modified SQL to only retrieve wedding details for the current user
        String sql = "SELECT * FROM wedding_details WHERE user_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                WeddingDetails details = new WeddingDetails();
                details.setId(rs.getInt("id"));
                details.setGroomName(rs.getString("groom_name"));
                details.setBrideName(rs.getString("bride_name"));
                details.setWeddingDateFrom(rs.getDate("wedding_date_from").toLocalDate());
                details.setWeddingDateTo(rs.getDate("wedding_date_to").toLocalDate());
                details.setAttendantsCount(rs.getInt("attendants_count"));
                details.setBudgetRange(rs.getString("budget_range"));

                detailsList.add(details);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return detailsList;
    }

    // Add method to get a specific wedding detail by ID
    public static WeddingDetails getWeddingDetailsById(int id) {
        WeddingDetails details = null;
        String sql = "SELECT * FROM wedding_details WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                details = new WeddingDetails();
                details.setId(rs.getInt("id"));
                details.setGroomName(rs.getString("groom_name"));
                details.setBrideName(rs.getString("bride_name"));
                details.setWeddingDateFrom(rs.getDate("wedding_date_from").toLocalDate());
                details.setWeddingDateTo(rs.getDate("wedding_date_to").toLocalDate());
                details.setAttendantsCount(rs.getInt("attendants_count"));
                details.setBudgetRange(rs.getString("budget_range"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return details;
    }

    // Add method to update wedding details
    public static void updateWeddingDetails(WeddingDetails details) {
        String sql = "UPDATE wedding_details SET groom_name = ?, bride_name = ?, " +
                "wedding_date_from = ?, wedding_date_to = ?, attendants_count = ?, " +
                "budget_range = ? WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, details.getGroomName());
            stmt.setString(2, details.getBrideName());
            stmt.setDate(3, Date.valueOf(details.getWeddingDateFrom()));
            stmt.setDate(4, Date.valueOf(details.getWeddingDateTo()));
            stmt.setInt(5, details.getAttendantsCount());
            stmt.setString(6, details.getBudgetRange());
            stmt.setInt(7, details.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Add method to delete wedding details
    public static void deleteWeddingDetails(int id) {
        String sql = "DELETE FROM wedding_details WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}