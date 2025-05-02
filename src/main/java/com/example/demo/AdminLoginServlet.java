package com.example.demo;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.*;

@WebServlet("/admin-login")
public class AdminLoginServlet extends HttpServlet {

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
        response.setContentType("application/json; charset=UTF-8");

        // 🧠 Get username and password from request
        String username = request.getParameter("admin-username");
        String password = request.getParameter("admin-password");

        if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("{\"message\": \"❌ Please fill in both username and password.\"}");
            return;
        }

        // ✅ Check username and password
        try (Connection conn = DatabaseHelper.getConnection()) {
            String sql = "SELECT password_hash FROM admins WHERE username = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    String storedHash = rs.getString("password_hash");
                    String inputHash = hashPassword(password);

                    // ✅ Check if password is correct
                    if (storedHash.equals(inputHash)) {
                        HttpSession session = request.getSession();
                        session.setAttribute("isAdmin", true);

                        // 🔐 Set secure session cookie for cross-origin
                        response.setHeader("Set-Cookie", "JSESSIONID=" + session.getId() + "; SameSite=None; Secure; Path=/; HttpOnly");

                        response.getWriter().println("{\"message\": \"✅ Login successful\"}");
                    } else {
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.getWriter().println("{\"message\": \"❌ Incorrect password.\"}");
                    }
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().println("{\"message\": \"❌ Admin user not found.\"}");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("{\"message\": \"❌ Error during login: " + e.getMessage() + "\"}");
        }
    }
}
