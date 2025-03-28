package com.example.demo;

import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;

public class ChangeUsernameServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader("Access-Control-Allow-Origin", "https://houses.westerduin.eu");
        response.setHeader("Access-Control-Allow-Methods", "POST");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");

        String newUsername = request.getParameter("new-username");

        if (newUsername == null || newUsername.trim().isEmpty()) {
            response.getWriter().println("❌ New username cannot be empty.");
            return;
        }

        String url = "jdbc:mysql://nozomi.proxy.rlwy.net:20003/school";
        String user = "root";
        String pass = "PcPRhDcYaVtsVhyDjLLUPyjxJhdqbeXI";

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
