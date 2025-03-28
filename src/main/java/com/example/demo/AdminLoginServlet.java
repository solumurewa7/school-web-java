package com.example.demo;

import javax.servlet.http.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.*;

public class AdminLoginServlet extends HttpServlet {

    private String hashPassword(String password) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashedBytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : hashedBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) {
        // ✅ CORS preflight support
        response.setHeader("Access-Control-Allow-Origin", "https://houses.westerduin.eu");
        response.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // ✅ Set proper encoding
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain; charset=UTF-8");

        // ✅ CORS headers
        response.setHeader("Access-Control-Allow-Origin", "https://houses.westerduin.eu");
        response.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");

        String username = request.getParameter("admin-username");
        String password = request.getParameter("admin-password");

        if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
            response.getWriter().println("❌ Please fill in both username and password.");
            return;
        }

        String url = "jdbc:mysql://nozomi.proxy.rlwy.net:20003/school";
        String dbUser = "root";
        String dbPass = "PcPRhDcYaVtsVhyDjLLUPyjxJhdqbeXI";

        try (Connection conn = DriverManager.getConnection(url, dbUser, dbPass)) {
            String sql = "SELECT password_hash FROM admins WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password_hash");
                String inputHash = hashPassword(password);

                if (storedHash.equals(inputHash)) {
                    HttpSession session = request.getSession();
                    session.setAttribute("isAdmin", true);
                    response.getWriter().println("✅ Login successful");
                } else {
                    response.getWriter().println("❌ Incorrect password.");
                }
            } else {
                response.getWriter().println("❌ Admin user not found.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("❌ Error during login: " + e.getMessage());
        }
    }
}
