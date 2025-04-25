package com.example.demo;

import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;

public class EditOneFieldServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // CORS Headers
        response.setHeader("Access-Control-Allow-Origin", "https://houses.westerduin.eu");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "POST");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");

        response.setContentType("text/plain");

        String studentIdStr = request.getParameter("student-id");
        String field = request.getParameter("field-to-edit");
        String newValue = request.getParameter("new-value");

        if (studentIdStr == null || field == null || newValue == null ||
                studentIdStr.isEmpty() || field.isEmpty() || newValue.isEmpty()) {
            response.getWriter().println("❌ Missing input. Please fill all fields.");
            return;
        }

        int studentId;
        try {
            studentId = Integer.parseInt(studentIdStr);
            if (field.equals("house")) {
                String[] validHouses = {"Red", "Blue", "Yellow", "Purple", "Black"}; // Changed Green to Purple
                boolean isValid = false;
                for (String h : validHouses) {
                    if (h.equalsIgnoreCase(newValue)) {
                        isValid = true;
                        break;
                    }
                }
                if (!isValid) {
                    response.getWriter().println("❌ Invalid house name.");
                    return;
                }
            }
        } catch (NumberFormatException e) {
            response.getWriter().println("❌ Invalid student ID.");
            return;
        }

        String url = "jdbc:mysql://tramway.proxy.rlwy.net:50944/railway";
        String user = "root";
        String password = "UZgNvgdRBJsyFtShwlrldLEclQrURJZb";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            String sql;

            switch (field) {
                case "student_first_name":
                    sql = "UPDATE students SET student_first_name = ? WHERE id = ?";
                    break;
                case "student_last_name":
                    sql = "UPDATE students SET student_last_name = ? WHERE id = ?";
                    break;
                case "student_email":
                    sql = "UPDATE students SET student_email = ? WHERE id = ?";
                    break;
                case "parent_first_name":
                    sql = "UPDATE parents SET parent_first_name = ? WHERE student_id = ?";
                    break;
                case "parent_last_name":
                    sql = "UPDATE parents SET parent_last_name = ? WHERE student_id = ?";
                    break;
                case "parent_email":
                    sql = "UPDATE parents SET parent_email = ? WHERE student_id = ?";
                    break;
                case "house":
                    sql = "UPDATE students SET house = ? WHERE id = ?";
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

    // Allow preflight CORS
    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader("Access-Control-Allow-Origin", "https://houses.westerduin.eu");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
    }
}
