package com.example.demo;

import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;

public class ResetAllServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // ✅ CORS Headers
        response.setHeader("Access-Control-Allow-Origin", "https://houses.westerduin.eu");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "POST");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setContentType("text/plain");

        // ✅ Railway SQL credentials
        String url = "jdbc:mysql://tramway.proxy.rlwy.net:50944/railway";
        String user = "root";
        String password = "UZgNvgdRBJsyFtShwlrldLEclQrURJZb";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            // Delete parents first (foreign key dependency)
            PreparedStatement deleteParents = conn.prepareStatement("DELETE FROM parents");
            deleteParents.executeUpdate();

            // Then delete students
            PreparedStatement deleteStudents = conn.prepareStatement("DELETE FROM students");
            deleteStudents.executeUpdate();

            // Reset house points
            PreparedStatement resetHouses = conn.prepareStatement("UPDATE houses SET points = 0");
            resetHouses.executeUpdate();

            response.getWriter().println("✅ All student, parent, and house data reset successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("❌ Failed to reset all data: " + e.getMessage());
        }
    }

    // Handle CORS preflight
    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader("Access-Control-Allow-Origin", "https://houses.westerduin.eu");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
    }
}
