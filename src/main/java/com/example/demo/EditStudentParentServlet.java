package com.example.demo;

import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;

public class EditStudentParentServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setHeader("Access-Control-Allow-Origin", "https://houses.westerduin.eu");
        response.setHeader("Access-Control-Allow-Credentials", "true");

        String studentIdStr = request.getParameter("student-id");
        String studentFirst = request.getParameter("student-first-name");
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

        String url = "jdbc:mysql://tramway.proxy.rlwy.net:50944/railway";
        String user = "root";
        String password = "UZgNvgdRBJsyFtShwlrldLEclQrURJZb";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            // ✅ Update student info
            String updateStudentSQL = "UPDATE students SET student_first_name = ?, student_last_name = ? WHERE id = ?";
            try (PreparedStatement studentStmt = conn.prepareStatement(updateStudentSQL)) {
                studentStmt.setString(1, studentFirst);
                studentStmt.setString(2, studentLast);
                studentStmt.setInt(3, studentId);
                studentStmt.executeUpdate();
            }

            // ✅ Update parent info
            String updateParentSQL = "UPDATE parents SET parent_first_name = ?, parent_last_name = ?, parent_email = ? WHERE student_id = ?";
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
