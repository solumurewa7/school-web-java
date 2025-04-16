package com.example.demo;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import javax.servlet.http.*;

public class AddStudentServlet extends HttpServlet {

    private static final String DB_URL = "jdbc:mysql://tramway.proxy.rlwy.net:50944/railway";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "UZgNvgdRBJsyFtShwlrldLEclQrURJZb";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // ✅ Enable CORS
        response.setHeader("Access-Control-Allow-Origin", "https://houses.westerduin.eu");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");

        response.setContentType("text/plain");

        // 🧠 Get all parameters from the form
        String studentName = request.getParameter("student-name");
        String studentEmail = request.getParameter("student-email");
        String parentName = request.getParameter("parent-name");
        String parentEmail = request.getParameter("parent-email");
        String parentType = request.getParameter("parent-type");
        String house = request.getParameter("house");

        if (studentName == null || studentEmail == null || parentName == null || parentEmail == null || parentType == null || house == null) {
            response.getWriter().println("❌ Missing required field(s).");
            return;
        }

        String[] studentParts = studentName.trim().split(" ");
        String[] parentParts = parentName.trim().split(" ");

        if (studentParts.length < 2 || parentParts.length < 2) {
            response.getWriter().println("❌ Please provide full names.");
            return;
        }

        String studentFirst = studentParts[0];
        String studentLast = studentParts[1];
        String parentFirst = parentParts[0];
        String parentLast = parentParts[1];

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // ✅ Insert student
            String studentSQL = "INSERT INTO students (student_first_name, student_last_name, student_email, house, points) VALUES (?, ?, ?, ?, 0)";
            PreparedStatement studentStmt = conn.prepareStatement(studentSQL, Statement.RETURN_GENERATED_KEYS);
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
                PreparedStatement parentStmt = conn.prepareStatement(parentSQL);
                parentStmt.setInt(1, studentId);
                parentStmt.setString(2, parentFirst);
                parentStmt.setString(3, parentLast);
                parentStmt.setString(4, parentEmail);
                parentStmt.setString(5, parentType);
                parentStmt.executeUpdate();
            }

            response.getWriter().println("✅ Student and parent added successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("❌ Error adding student: " + e.getMessage());
        }
    }

    // ✅ Preflight request handler
    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader("Access-Control-Allow-Origin", "https://houses.westerduin.eu");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
    }
}
