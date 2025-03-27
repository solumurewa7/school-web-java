package com.example.demo;

import java.io.IOException;
import java.sql.*;
import javax.servlet.http.*;

public class AddStudentServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("🔥 AddStudentServlet has been called!");

        // Get parameters
        String fullName = request.getParameter("student-name");
        String studentEmail = request.getParameter("student-email");
        String parentName = request.getParameter("parent-name");
        String parentEmail = request.getParameter("parent-email");
        String parentType = request.getParameter("parent-type");
        String house = request.getParameter("house");

        // Validation
        if (fullName == null || studentEmail == null || parentName == null ||
                parentEmail == null || parentType == null || house == null ||
                fullName.isEmpty() || studentEmail.isEmpty() || parentName.isEmpty() ||
                parentEmail.isEmpty() || parentType.isEmpty() || house.isEmpty()) {

            response.getWriter().println("❌ Missing required input fields.");
            return;
        }

        // Split names
        String[] nameParts = fullName.trim().split(" ");
        String firstName = nameParts[0];
        String lastName = nameParts.length > 1 ? nameParts[1] : "";

        String[] parentParts = parentName.trim().split(" ");
        String parentFirst = parentParts[0];
        String parentLast = parentParts.length > 1 ? parentParts[1] : "";

        // Default new students to 0 points
        int points = 0;

        String url = "jdbc:mysql://nozomi.proxy.rlwy.net:20003/school";
        String user = "root";
        String password = "PcPRhDcYaVtsVhyDjLLUPyjxJhdqbeXI";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {

            // 1. Insert student
            String insertStudentSQL = "INSERT INTO students (first_name, last_name, email, house, points) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement studentStmt = conn.prepareStatement(insertStudentSQL, Statement.RETURN_GENERATED_KEYS);
            studentStmt.setString(1, firstName);
            studentStmt.setString(2, lastName);
            studentStmt.setString(3, studentEmail);
            studentStmt.setString(4, house);
            studentStmt.setInt(5, points);
            studentStmt.executeUpdate();

            ResultSet rs = studentStmt.getGeneratedKeys();
            int studentId = -1;
            if (rs.next()) {
                studentId = rs.getInt(1);
            }

            // 2. Insert parent
            String insertParentSQL = "INSERT INTO parents (student_id, parent_type, first_name, last_name, email) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement parentStmt = conn.prepareStatement(insertParentSQL);
            parentStmt.setInt(1, studentId);
            parentStmt.setString(2, parentType);
            parentStmt.setString(3, parentFirst);
            parentStmt.setString(4, parentLast);
            parentStmt.setString(5, parentEmail);
            parentStmt.executeUpdate();

            response.getWriter().println("✅ Student and parent added successfully!");

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("❌ Error: " + e.getMessage());
        }
    }
}
