package com.example.demo;

import java.io.IOException;
import java.sql.*;
import javax.servlet.http.*;
import org.json.JSONObject;

public class StudentPointsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // ✅ CORS for frontend
        response.setHeader("Access-Control-Allow-Origin", "https://houses.westerduin.eu");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "GET, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setContentType("application/json");

        String studentId = request.getParameter("id");

        if (studentId == null || studentId.isEmpty()) {
            // ✅ Return error in JSON format for consistency
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("error", "Missing student ID.");
            response.getWriter().write(errorResponse.toString());
            return;
        }

        // ✅ Railway SQL connection
        String url = "jdbc:mysql://tramway.proxy.rlwy.net:50944/railway";
        String user = "root";
        String password = "UZgNvgdRBJsyFtShwlrldLEclQrURJZb";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            String sql = "SELECT first_name, last_name, house, points FROM students WHERE student_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, Integer.parseInt(studentId));
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // ✅ Student found, send response as JSON
                JSONObject jsonResponse = new JSONObject();
                jsonResponse.put("first_name", rs.getString("first_name"));
                jsonResponse.put("last_name", rs.getString("last_name"));
                jsonResponse.put("house", rs.getString("house"));
                jsonResponse.put("points", rs.getInt("points"));
                response.getWriter().write(jsonResponse.toString());
            } else {
                // ✅ Return error in JSON format when student is not found
                JSONObject errorResponse = new JSONObject();
                errorResponse.put("error", "Student not found.");
                response.getWriter().write(errorResponse.toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
            // ✅ Return error in JSON format if an exception occurs
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("error", "Something went wrong.");
            response.getWriter().write(errorResponse.toString());
        }
    }

    // ✅ Preflight for OPTIONS request (CORS)
    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader("Access-Control-Allow-Origin", "https://houses.westerduin.eu");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "GET, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
