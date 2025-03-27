package com.example.demo;

import org.json.JSONObject;

import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;

public class StudentInfoServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String studentIdParam = request.getParameter("id");

        if (studentIdParam == null || studentIdParam.isEmpty()) {
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Missing student ID\"}");
            return;
        }

        int studentId;
        try {
            studentId = Integer.parseInt(studentIdParam);
        } catch (NumberFormatException e) {
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Invalid student ID format\"}");
            return;
        }

        String url = "jdbc:mysql://nozomi.proxy.rlwy.net:20003/school";
        String user = "root";
        String password = "PcPRhDcYaVtsVhyDjLLUPyjxJhdqbeXI";

        JSONObject json = new JSONObject();

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            // 1. Get student
            String studentSQL = "SELECT first_name, last_name FROM students WHERE student_id = ?";
            try (PreparedStatement studentStmt = conn.prepareStatement(studentSQL)) {
                studentStmt.setInt(1, studentId);
                ResultSet studentRS = studentStmt.executeQuery();
                if (studentRS.next()) {
                    json.put("student_first_name", studentRS.getString("first_name"));
                    json.put("student_last_name", studentRS.getString("last_name"));
                }
            }

            // 2. Get parent
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

            response.setContentType("application/json");
            response.getWriter().write(json.toString());

        } catch (Exception e) {
            e.printStackTrace();
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Failed to fetch student and parent info\"}");
        }
    }
}
