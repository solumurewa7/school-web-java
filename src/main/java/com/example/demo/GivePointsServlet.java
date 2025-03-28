package com.example.demo;

import java.io.IOException;
import java.sql.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class GivePointsServlet extends HttpServlet {

    private final String DB_URL = "jdbc:mysql://nozomi.proxy.rlwy.net:20003/school";
    private final String DB_USER = "root";
    private final String DB_PASSWORD = "PcPRhDcYaVtsVhyDjLLUPyjxJhdqbeXI";

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws IOException {
        setCorsHeaders(response);
        response.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        setCorsHeaders(response);

        try {
            int studentId = Integer.parseInt(request.getParameter("student-id"));
            int extraPoints = Integer.parseInt(request.getParameter("points"));

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {

                // Step 1: Update student points
                String updateStudentSQL = "UPDATE students SET points = points + ? WHERE student_id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(updateStudentSQL)) {
                    stmt.setInt(1, extraPoints);
                    stmt.setInt(2, studentId);
                    stmt.executeUpdate();
                }

                // Step 2: Get student's house
                String getHouseSQL = "SELECT house FROM students WHERE student_id = ?";
                try (PreparedStatement houseStmt = conn.prepareStatement(getHouseSQL)) {
                    houseStmt.setInt(1, studentId);
                    try (ResultSet rs = houseStmt.executeQuery()) {
                        if (rs.next()) {
                            String house = rs.getString("house");

                            // Step 3: Add points to house
                            String updateHouseSQL = "UPDATE houses SET points = points + ? WHERE house_name = ?";
                            try (PreparedStatement houseUpdateStmt = conn.prepareStatement(updateHouseSQL)) {
                                houseUpdateStmt.setInt(1, extraPoints);
                                houseUpdateStmt.setString(2, house);
                                houseUpdateStmt.executeUpdate();
                            }
                        }
                    }
                }

                response.setContentType("text/plain");
                response.getWriter().println("✅ Points successfully added to student and house!");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.setContentType("text/plain");
            response.getWriter().println("❌ Error adding points: " + e.getMessage());
        }
    }

    private void setCorsHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "https://houses.westerduin.eu");
        response.setHeader("Access-Control-Allow-Credentials", "true");
    }
}
