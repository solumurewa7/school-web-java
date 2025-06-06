package com.example.demo;

import java.io.IOException;
import java.sql.*;
import javax.servlet.http.*;

public class GivePointsServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // ✅ CORS headers
        response.setHeader("Access-Control-Allow-Origin", "https://houses.westerduin.eu");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setContentType("text/plain");

        String url = "jdbc:mysql://tramway.proxy.rlwy.net:50944/railway";
        String user = "root";
        String password = "UZgNvgdRBJsyFtShwlrldLEclQrURJZb";

        try {
            int studentId = Integer.parseInt(request.getParameter("student-id"));
            int extraPoints = Integer.parseInt(request.getParameter("points"));

            try (Connection conn = DriverManager.getConnection(url, user, password)) {
                // Step 1: Update student points
                String updateStudentSQL = "UPDATE students SET points = points + ? WHERE id = ?";
                PreparedStatement stmt = conn.prepareStatement(updateStudentSQL);
                stmt.setInt(1, extraPoints);
                stmt.setInt(2, studentId);
                stmt.executeUpdate();

                // Step 2: Get the student's house
                String getHouseSQL = "SELECT house FROM students WHERE id = ?";
                PreparedStatement houseStmt = conn.prepareStatement(getHouseSQL);
                houseStmt.setInt(1, studentId);
                ResultSet rs = houseStmt.executeQuery();

                if (rs.next()) {
                    String house = rs.getString("house");

                    // Step 3: Check if house is valid
                    String[] validHouses = {"Red", "Blue", "Yellow", "Purple", "Black"};
                    boolean isValidHouse = false;
                    for (String validHouse : validHouses) {
                        if (validHouse.equalsIgnoreCase(house)) {
                            isValidHouse = true;
                            break;
                        }
                    }

                    if (!isValidHouse) {
                        response.getWriter().println("❌ Invalid house name: " + house);
                        return;
                    }

                    // Step 4: Add points to house
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

    // ✅ Handle CORS preflight requests
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader("Access-Control-Allow-Origin", "https://houses.westerduin.eu");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
    }
}
