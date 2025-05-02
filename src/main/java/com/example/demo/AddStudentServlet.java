package com.example.demo;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet("/add-student")
public class AddStudentServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // ✅ Enable CORS
        response.setHeader("Access-Control-Allow-Origin", "https://houses.westerduin.eu");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");

        response.setContentType("application/json");

        // 🧠 Get all parameters from the form
        String studentName = request.getParameter("student-name");
        String studentEmail = request.getParameter("student-email");
        String parentName = request.getParameter("parent-name");
        String parentEmail = request.getParameter("parent-email");
        String parentType = request.getParameter("parent-type");
        String house = request.getParameter("house");

        // 🚨 Check for missing fields
        if (studentName == null || studentEmail == null || parentName == null || parentEmail == null || parentType == null || house == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("{\"message\": \"❌ Missing required field(s).\"}");
            return;
        }

        // 🧩 Split names into first and last
        String[] studentParts = studentName.trim().split(" ");
        String[] parentParts = parentName.trim().split(" ");

        if (studentParts.length < 2 || parentParts.length < 2) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("{\"message\": \"❌ Please provide full names.\"}");
            return;
        }

        String studentFirst = studentParts[0];
        String studentLast = studentParts[1];
        String parentFirst = parentParts[0];
        String parentLast = parentParts[1];

        // ✅ Connect to the database and handle the insertion
        try (Connection conn = DatabaseHelper.getConnection()) {
            // ✅ Insert student
            String studentSQL = "INSERT INTO students (student_first_name, student_last_name, student_email, house, points) VALUES (?, ?, ?, ?, 0)";
            try (PreparedStatement studentStmt = conn.prepareStatement(studentSQL, Statement.RETURN_GENERATED_KEYS)) {
                studentStmt.setString(1, studentFirst);
                studentStmt.setString(2, studentLast);
                studentStmt.setString(3, studentEmail);
                studentStmt.setString(4, house);
                studentStmt.executeUpdate();

                ResultSet generatedKeys = studentStmt.getGeneratedKeys();
                int studentId = -1;
                if (generatedKeys.next()) {
                    studentId = generatedKeys.getInt(1);
                }

                // ✅ Insert parent if student insert was successful
                if (studentId != -1) {
                    String parentSQL = "INSERT INTO parents (student_id, parent_first_name, parent_last_name, parent_email, relationship) VALUES (?, ?, ?, ?, ?)";
                    try (PreparedStatement parentStmt = conn.prepareStatement(parentSQL)) {
                        parentStmt.setInt(1, studentId);
                        parentStmt.setString(2, parentFirst);
                        parentStmt.setString(3, parentLast);
                        parentStmt.setString(4, parentEmail);
                        parentStmt.setString(5, parentType);
                        parentStmt.executeUpdate();
                    }
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().println("{\"message\": \"✅ Student and parent added successfully!\"}");
                } else {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.getWriter().println("{\"message\": \"❌ Failed to add student.\"}");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("{\"message\": \"❌ Error adding student: " + e.getMessage() + "\"}");
        }
    }

    // ✅ Preflight request handler for CORS
    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader("Access-Control-Allow-Origin", "https://houses.westerduin.eu");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
    }
}
