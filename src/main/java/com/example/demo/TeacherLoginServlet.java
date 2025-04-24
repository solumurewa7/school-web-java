/*package com.example.demo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;

@WebServlet("/teacher-login")
public class TeacherLoginServlet extends HttpServlet {

    // ✅ Updated to Railway SQL connection
    private static final String DB_URL = "jdbc:mysql://tramway.proxy.rlwy.net:50944/railway";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "UZgNvgdRBJsyFtShwlrldLEclQrURJZb";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // ✅ CORS headers
        response.setHeader("Access-Control-Allow-Origin", "https://houses.westerduin.eu");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setContentType("application/json");

        String username = request.getParameter("teacher-username");
        String password = request.getParameter("teacher-password");

        if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
            response.getWriter().write("{\"status\": \"❌ Missing username or password\"}");
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {

            // ✅ First: Check if admin
            String adminQuery = "SELECT * FROM admins WHERE username = ? AND password_hash = SHA2(?, 256)";
            try (PreparedStatement adminStmt = conn.prepareStatement(adminQuery)) {
                adminStmt.setString(1, username);
                adminStmt.setString(2, password);

                ResultSet adminRs = adminStmt.executeQuery();
                if (adminRs.next()) {
                    HttpSession session = request.getSession(true);
                    session.setAttribute("role", "admin");
                    session.setAttribute("username", username);

                    // ✅ Set cross-origin session cookie manually
                    response.setHeader("Set-Cookie", "JSESSIONID=" + session.getId() + "; SameSite=None; Secure; Path=/; HttpOnly");
                    response.getWriter().write("{\"status\": \"admin\"}");
                    return;
                }
            }

            // ✅ Then: Check if teacher
            String teacherQuery = "SELECT * FROM teachers WHERE username = ? AND password_hash = SHA2(?, 256)";
            try (PreparedStatement teacherStmt = conn.prepareStatement(teacherQuery)) {
                teacherStmt.setString(1, username);
                teacherStmt.setString(2, password);

                ResultSet teacherRs = teacherStmt.executeQuery();
                if (teacherRs.next()) {
                    HttpSession session = request.getSession(true);
                    session.setAttribute("role", "teacher");
                    session.setAttribute("username", username);

                    response.setHeader("Set-Cookie", "JSESSIONID=" + session.getId() + "; SameSite=None; Secure; Path=/; HttpOnly");
                    response.getWriter().write("{\"status\": \"teacher\"}");
                } else {
                    response.getWriter().write("{\"status\": \"❌ Invalid username or password\"}");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().write("{\"status\": \"❌ Server error\"}");
        }
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setHeader("Access-Control-Allow-Origin", "https://houses.westerduin.eu");
        resp.setHeader("Access-Control-Allow-Credentials", "true");
        resp.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
*/