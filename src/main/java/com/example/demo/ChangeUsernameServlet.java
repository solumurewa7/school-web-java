package com.example.demo;

import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;

public class ChangeUsernameServlet extends HttpServlet {

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader("Access-Control-Allow-Origin", "https://houses.westerduin.eu");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader("Access-Control-Allow-Origin", "https://houses.westerduin.eu");
        response.setHeader("Access-Control-Allow-Credentials", "true"); // 🔥 Required for session cookies
        response.setHeader("Access-Control-Allow-Methods", "POST");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setContentType("text/plain; charset=UTF-8");

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("isAdmin") == null) {
            response.getWriter().println("❌ Unauthorized. Admin login required.");
            return;
        }

        String rawUsername = request.getParameter("new-username");
        System.out.println("🔍 rawUsername: " + rawUsername);
        if (rawUsername == null || rawUsername.trim().isEmpty()) {
            response.getWriter().println("❌ New username cannot be empty.");
            return;
        }
        String newUsername = rawUsername.trim();

        // ✅ Updated Railway SQL credentials
        String url = "jdbc:mysql://tramway.proxy.rlwy.net:50944/railway";
        String user = "root";
        String pass = "UZgNvgdRBJsyFtShwlrldLEclQrURJZb";

        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            String updateSQL = "UPDATE admins SET username = ? LIMIT 1";
            try (PreparedStatement stmt = conn.prepareStatement(updateSQL)) {
                stmt.setString(1, newUsername);
                int rows = stmt.executeUpdate();
                if (rows > 0) {
                    response.getWriter().println("✅ Username updated successfully!");
                } else {
                    response.getWriter().println("⚠️ No admin user found to update.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("❌ Error updating username: " + e.getMessage());
        }
    }
}
