package com.example.demo;

import java.io.IOException;
import java.sql.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

public class StudentPointsServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // ✅ Allow CORS from your HTML website
        response.setHeader("Access-Control-Allow-Origin", "https://houses.westerduin.eu");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");

        // ✅ Make sure we're sending JSON back
        response.setContentType("application/json");

        String studentId = request.getParameter("id");

        // 🚨 If ID is missing or invalid, return error JSON
        if (studentId == null || studentId.isEmpty()) {
            response.getWriter().write("{\"error\": \"Missing student ID.\"}");
            return;
        }

        String url = "jdbc:mysql://nozomi.proxy.rlwy.net:20003/school";
        String user = "root";
        String password = "PcPRhDcYaVtsVhyDjLLUPyjxJhdqbeXI";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            String sql = "SELECT first_name, last_name, points FROM students WHERE student_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, Integer.parseInt(studentId));
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                JSONObject json = new JSONObject();
                json.put("name", rs.getString("first_name") + " " + rs.getString("last_name"));
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
}
