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
            response.getWriter().write("{\"error\": \"Missing student ID.\"}");
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
                JSONObject json = new JSONObject();
                json.put("first_name", rs.getString("first_name"));
                json.put("last_name", rs.getString("last_name"));
                json.put("house", rs.getString("house"));
                json.put("points", rs.getInt("points"));
                response.getWriter().write(json.toString());
            } else {
                response.getWriter().write("{\"error\": \"Student not found.\"}");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("{\"error\": \"Something went wrong.\"}");
        }
    }

    // ✅ Preflight
    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader("Access-Control-Allow-Origin", "https://houses.westerduin.eu");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "GET, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
