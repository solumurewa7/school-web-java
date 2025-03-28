package com.example.demo;

import javax.servlet.http.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.*;

public class ChangePasswordServlet extends HttpServlet {

    private String hashPassword(String password) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashedBytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : hashedBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader("Access-Control-Allow-Origin", "https://houses.westerduin.eu");
        response.setHeader("Access-Control-Allow-Methods", "POST");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");

        String currentPassword = request.getParameter("current-password");
        String newPassword = request.getParameter("new-password");
        String confirmPassword = request.getParameter("confirm-password");

        if (currentPassword == null || newPassword == null || confirmPassword == null ||
                currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            response.getWriter().println("❌ All password fields are required.");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            response.getWriter().println("❌ New passwords do not match.");
            return;
        }

        String url = "jdbc:mysql://nozomi.proxy.rlwy.net:20003/school";
        String user = "root";
        String pass = "PcPRhDcYaVtsVhyDjLLUPyjxJhdqbeXI";

        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            String getAdmin = "SELECT username, password_hash FROM admins LIMIT 1";
            try (PreparedStatement stmt = conn.prepareStatement(getAdmin)) {
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    String username = rs.getString("username");
                    String storedHash = rs.getString("password_hash");

                    String currentHash = hashPassword(currentPassword);
                    if (!currentHash.equals(storedHash)) {
                        response.getWriter().println("❌ Current password is incorrect.");
                        return;
                    }

                    String newHash = hashPassword(newPassword);
                    String updateSQL = "UPDATE admins SET password_hash = ? WHERE username = ?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSQL)) {
                        updateStmt.setString(1, newHash);
                        updateStmt.setString(2, username);
                        updateStmt.executeUpdate();
                        response.getWriter().println("✅ Password updated successfully!");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("❌ Error updating password: " + e.getMessage());
        }
    }
}
