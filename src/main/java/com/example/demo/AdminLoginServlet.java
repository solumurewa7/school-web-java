package com.example.demo;

import javax.servlet.http.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.*;

public class AdminLoginServlet extends HttpServlet {

    // ✅ Railway DB credentials
    private static final String DB_URL = "jdbc:mysql://tramway.proxy.rlwy.net:50944/railway";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "UZgNvgdRBJsyFtShwlrldLEclQrURJZb";

    // 🔐 Password hashing (SHA-256)
    private String hashPassword(String password) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashedBytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : hashedBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    // ✅ Handle CORS preflight (OPTIONS request)
    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "https://houses.westerduin.eu");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setStatus(HttpServletResponse.SC_OK);
    }

    // ✅ Handle actual POST login request
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 🔓 CORS headers
        response.setHeader("Access-Control-Allow-Origin", "https://houses.westerduin.eu");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain; charset=UTF-8");

        String username = request.getParameter("admin-username");
        String password = request.getParameter("admin-password");

        if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
            response.getWriter().println("❌ Please fill in both username and password.");
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
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

                    // 🔐 Set secure session cookie for cross-origin
                    response.setHeader("Set-Cookie", "JSESSIONID=" + session.getId() + "; SameSite=None; Secure; Path=/; HttpOnly");

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
