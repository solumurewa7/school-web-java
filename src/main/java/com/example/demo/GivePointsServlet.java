package com.example.demo;

import java.io.IOException;
import java.sql.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class GivePointsServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String url = "jdbc:mysql://nozomi.proxy.rlwy.net:20003/school";
        String user = "root";
        String password = "PcPRhDcYaVtsVhyDjLLUPyjxJhdqbeXI";

        try {
            int studentId = Integer.parseInt(request.getParameter("student-id"));
            int extraPoints = Integer.parseInt(request.getParameter("points"));

            try (Connection conn = DriverManager.getConnection(url, user, password)) {

                // Step 1: Update student points
                String updateStudentSQL = "UPDATE students SET points = points + ? WHERE student_id = ?";
                PreparedStatement stmt = conn.prepareStatement(updateStudentSQL);
                stmt.setInt(1, extraPoints);
                stmt.setInt(2, studentId);
                stmt.executeUpdate();

                // Step 2: Get the student's house
                String getHouseSQL = "SELECT house FROM students WHERE student_id = ?";
                PreparedStatement houseStmt = conn.prepareStatement(getHouseSQL);
                houseStmt.setInt(1, studentId);
                ResultSet rs = houseStmt.executeQuery();

                if (rs.next()) {
                    String house = rs.getString("house");

                    // Step 3: Add points to house
                    String updateHouseSQL = "UPDATE houses SET points = points + ? WHERE house_name = ?";
                    PreparedStatement houseUpdateStmt = conn.prepareStatement(updateHouseSQL);
                    houseUpdateStmt.setInt(1, extraPoints);
                    houseUpdateStmt.setString(2, house);
                    houseUpdateStmt.executeUpdate();
                }

                response.getWriter().println("✅ Points successfully added to student and house!");

            }

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("❌ Error adding points: " + e.getMessage());
        }
    }
}