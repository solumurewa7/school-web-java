package com.example.demo;

import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;

public class EditStudentParentServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setHeader("Access-Control-Allow-Origin", "https://houses.westerduin.eu");
        response.setHeader("Access-Control-Allow-Credentials", "true");



        String studentIdStr = request.getParameter("student-id"); // ✅ CHANGED from "edit-student-id"
        String studentFirst = request.getParameter("student-first-name"); // ✅ matches HTML
        String studentLast = request.getParameter("student-last-name");
        String parentFirst = request.getParameter("parent-first-name");
        String parentLast = request.getParameter("parent-last-name");
        String parentEmail = request.getParameter("parent-email");

        if (studentIdStr == null || studentIdStr.isEmpty()) {
            response.setContentType("text/plain");
            response.getWriter().println("❌ Student ID is missing.");
            return;
        }

        int studentId;
        try {
            studentId = Integer.parseInt(studentIdStr);
        } catch (NumberFormatException e) {
            response.setContentType("text/plain");
            response.getWriter().println("❌ Invalid student ID format.");
            return;
        }

        String url = "jdbc:mysql://nozomi.proxy.rlwy.net:20003/school";
        String user = "root";
        String password = "PcPRhDcYaVtsVhyDjLLUPyjxJhdqbeXI";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            // Update student
            String updateStudentSQL = "UPDATE students SET first_name = ?, last_name = ? WHERE student_id = ?";
            try (PreparedStatement studentStmt = conn.prepareStatement(updateStudentSQL)) {
                studentStmt.setString(1, studentFirst);
                studentStmt.setString(2, studentLast);
                studentStmt.setInt(3, studentId);
                studentStmt.executeUpdate();
            }

            // Update parent
            String updateParentSQL = "UPDATE parents SET first_name = ?, last_name = ?, email = ? WHERE student_id = ?";
            try (PreparedStatement parentStmt = conn.prepareStatement(updateParentSQL)) {
                parentStmt.setString(1, parentFirst);
                parentStmt.setString(2, parentLast);
                parentStmt.setString(3, parentEmail);
                parentStmt.setInt(4, studentId);
                parentStmt.executeUpdate();
            }

            response.setContentType("text/plain");
            response.getWriter().println("✅ Info updated successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            response.setContentType("text/plain");
            response.getWriter().println("❌ Failed to update info.");
        }
    }
}
