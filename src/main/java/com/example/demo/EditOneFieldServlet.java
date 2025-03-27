package com.example.demo;

import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;

public class EditOneFieldServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String studentIdStr = request.getParameter("student-id");
        String field = request.getParameter("field-to-edit");
        String newValue = request.getParameter("new-value");

        response.setContentType("text/plain");

        if (studentIdStr == null || field == null || newValue == null ||
                studentIdStr.isEmpty() || field.isEmpty() || newValue.isEmpty()) {
            response.getWriter().println("❌ Missing input. Please fill all fields.");
            return;
        }

        int studentId;
        try {
            studentId = Integer.parseInt(studentIdStr);
        } catch (NumberFormatException e) {
            response.getWriter().println("❌ Invalid student ID.");
            return;
        }

        String url = "jdbc:mysql://nozomi.proxy.rlwy.net:20003/school";
        String user = "root";
        String password = "PcPRhDcYaVtsVhyDjLLUPyjxJhdqbeXI";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            String sql = null;

            switch (field) {
                case "student_first_name":
                    sql = "UPDATE students SET first_name = ? WHERE student_id = ?";
                    break;
                case "student_last_name":
                    sql = "UPDATE students SET last_name = ? WHERE student_id = ?";
                    break;
                case "student_email":
                    sql = "UPDATE students SET email = ? WHERE student_id = ?";
                    break;
                case "parent_first_name":
                    sql = "UPDATE parents SET first_name = ? WHERE student_id = ?";
                    break;
                case "parent_last_name":
                    sql = "UPDATE parents SET last_name = ? WHERE student_id = ?";
                    break;
                case "parent_email":
                    sql = "UPDATE parents SET email = ? WHERE student_id = ?";
                    break;
                case "house":
                    sql = "UPDATE students SET house = ? WHERE student_id = ?";
                    break;
                default:
                    response.getWriter().println("❌ Invalid field selected.");
                    return;
            }

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, newValue);
                stmt.setInt(2, studentId);
                int rows = stmt.executeUpdate();

                if (rows > 0) {
                    response.getWriter().println("✅ Field updated successfully!");
                } else {
                    response.getWriter().println("⚠️ No changes made. Student may not exist.");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("❌ Database error occurred.");
        }
    }
}
