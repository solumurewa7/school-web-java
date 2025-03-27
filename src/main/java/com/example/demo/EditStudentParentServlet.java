package com.example.demo;

import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;

public class EditStudentParentServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String studentIdStr = request.getParameter("edit-student-id");

        if (studentIdStr == null || studentIdStr.isEmpty()) {
            response.getWriter().println("❌ Student ID is missing.");
            return;
        }

        int studentId = Integer.parseInt(studentIdStr);
        String studentFirst = request.getParameter("edit-student-first");
        String studentLast = request.getParameter("edit-student-last");
        String parentFirst = request.getParameter("edit-parent-first");
        String parentLast = request.getParameter("edit-parent-last");
        String parentEmail = request.getParameter("edit-parent-email");

        String url = "jdbc:mysql://nozomi.proxy.rlwy.net:20003/school";
        String user = "root";
        String password = "PcPRhDcYaVtsVhyDjLLUPyjxJhdqbeXI";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            // Update student
            String updateStudentSQL = "UPDATE students SET first_name = ?, last_name = ? WHERE student_id = ?";
            PreparedStatement studentStmt = conn.prepareStatement(updateStudentSQL);
            studentStmt.setString(1, studentFirst);
            studentStmt.setString(2, studentLast);
            studentStmt.setInt(3, studentId);
            studentStmt.executeUpdate();

            // Update parent
            String updateParentSQL = "UPDATE parents SET first_name = ?, last_name = ?, email = ? WHERE student_id = ?";
            PreparedStatement parentStmt = conn.prepareStatement(updateParentSQL);
            parentStmt.setString(1, parentFirst);
            parentStmt.setString(2, parentLast);
            parentStmt.setString(3, parentEmail);
            parentStmt.setInt(4, studentId);
            parentStmt.executeUpdate();

            response.getWriter().println("✅ Info updated successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("❌ Failed to update info.");
        }
    }
}