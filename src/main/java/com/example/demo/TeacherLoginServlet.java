package com.example.demo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet("/teacher-login")
public class TeacherLoginServlet extends HttpServlet {

    // Replace with your actual DB connection info
    private static final String DB_URL = "jdbc:mysql://localhost:20003/school";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "seyolu7X";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Enable CORS
        response.setHeader("Access-Control-Allow-Origin", "https://houses.westerduin.eu");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setContentType("application/json");

        String username = request.getParameter("teacher-username");
        String password = request.getParameter("teacher-password");

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {

            // First, check if the user is an admin
            String adminQuery = "SELECT * FROM admins WHERE username = ? AND password_hash = SHA2(?, 256)";
            try (PreparedStatement adminStmt = conn.prepareStatement(adminQuery)) {
                adminStmt.setString(1, username);
                adminStmt.setString(2, password);

                ResultSet adminRs = adminStmt.executeQuery();
                if (adminRs.next()) {
                    HttpSession session = request.getSession(true);
                    session.setAttribute("role", "admin");
                    session.setAttribute("username", username);
                    response.getWriter().write("{\"status\": \"admin\"}");
                    return;
                }
            }

            // Otherwise, check if the user is a teacher
            String teacherQuery = "SELECT * FROM teachers WHERE username = ? AND password_hash = SHA2(?, 256)";
            try (PreparedStatement teacherStmt = conn.prepareStatement(teacherQuery)) {
                teacherStmt.setString(1, username);
                teacherStmt.setString(2, password);

                ResultSet teacherRs = teacherStmt.executeQuery();
                if (teacherRs.next()) {
                    HttpSession session = request.getSession(true);
                    session.setAttribute("role", "teacher");
                    session.setAttribute("username", username);
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
    }
}
