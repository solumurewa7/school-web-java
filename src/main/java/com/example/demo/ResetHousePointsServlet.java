package com.example.demo;

import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;

public class ResetHousePointsServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // ✅ CORS headers
        response.setHeader("Access-Control-Allow-Origin", "https://houses.westerduin.eu");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "POST");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setContentType("text/plain");

        // ✅ Railway SQL
        String url = "jdbc:mysql://tramway.proxy.rlwy.net:50944/railway";
        String user = "root";
        String password = "UZgNvgdRBJsyFtShwlrldLEclQrURJZb";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            // Reset all student points
            PreparedStatement resetStudentPoints = conn.prepareStatement("UPDATE students SET points = 0");
            resetStudentPoints.executeUpdate();

            // Reset house points
            PreparedStatement resetHousePoints = conn.prepareStatement("UPDATE houses SET points = 0");
            resetHousePoints.executeUpdate();

            response.getWriter().println("✅ All house and student points have been reset.");
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("❌ Failed to reset points: " + e.getMessage());
        }
    }

    // ✅ Handle CORS preflight
    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader("Access-Control-Allow-Origin", "https://houses.westerduin.eu");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
    }
}
