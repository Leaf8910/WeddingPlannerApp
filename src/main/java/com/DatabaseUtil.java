package com;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DatabaseUtil {
    private static final String URL = "jdbc:mysql://localhost:3306/wedding_planner";
    private static final String USER = "root";
    private static final String PASSWORD = "password";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void saveWeddingDetails(WeddingDetails details) {
        String sql = "INSERT INTO wedding_details (groom_name, bride_name, wedding_date_from, " +
                "wedding_date_to, attendants_count, budget_range) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, details.getGroomName());
            stmt.setString(2, details.getBrideName());
            stmt.setDate(3, Date.valueOf(details.getWeddingDateFrom()));
            stmt.setDate(4, Date.valueOf(details.getWeddingDateTo()));
            stmt.setInt(5, details.getAttendantsCount());
            stmt.setString(6, details.getBudgetRange());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<WeddingDetails> getAllWeddingDetails() {
        List<WeddingDetails> detailsList = new ArrayList<>();
        String sql = "SELECT * FROM wedding_details";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

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
}