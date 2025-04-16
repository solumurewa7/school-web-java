package com.example.demo;

import org.json.JSONObject;

import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;

public class StudentInfoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // ✅ CORS headers
        response.setHeader("Access-Control-Allow-Origin", "https://houses.westerduin.eu");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setContentType("application/json");

        String studentIdParam = request.getParameter("student-id");

        if (studentIdParam == null || studentIdParam.isEmpty()) {
            response.getWriter().write("{\"error\":\"Missing student ID\"}");
            return;
        }

        int studentId;
        try {
            studentId = Integer.parseInt(studentIdParam);
        } catch (NumberFormatException e) {
            response.getWriter().write("{\"error\":\"Invalid student ID format\"}");
            return;
        }

        // ✅ Railway SQL connection
        String url = "jdbc:mysql://tramway.proxy.rlwy.net:50944/railway";
        String user = "root";
        String password = "UZgNvgdRBJsyFtShwlrldLEclQrURJZb";

        JSONObject json = new JSONObject();

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            // Student info
            String studentSQL = "SELECT first_name, last_name FROM students WHERE student_id = ?";
            try (PreparedStatement studentStmt = conn.prepareStatement(studentSQL)) {
                studentStmt.setInt(1, studentId);
                ResultSet studentRS = studentStmt.executeQuery();
                if (studentRS.next()) {
                    json.put("student_first_name", studentRS.getString("first_name"));
                    json.put("student_last_name", studentRS.getString("last_name"));
                }
            }

            // Parent info
            String parentSQL = "SELECT first_name, last_name, email FROM parents WHERE student_id = ?";
            try (PreparedStatement parentStmt = conn.prepareStatement(parentSQL)) {
                parentStmt.setInt(1, studentId);
                ResultSet parentRS = parentStmt.executeQuery();
                if (parentRS.next()) {
                    json.put("parent_first_name", parentRS.getString("first_name"));
                    json.put("parent_last_name", parentRS.getString("last_name"));
                    json.put("parent_email", parentRS.getString("email"));
                }
            }

            response.getWriter().write(json.toString());

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("{\"error\":\"Failed to fetch student and parent info\"}");
        }
    }

    // ✅ Handle preflight request
    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader("Access-Control-Allow-Origin", "https://houses.westerduin.eu");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "GET, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
