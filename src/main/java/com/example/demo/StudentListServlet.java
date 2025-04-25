package com.example.demo;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import javax.servlet.http.*;
import org.json.JSONArray;
import org.json.JSONObject;

public class StudentListServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // ✅ Add CORS header for your frontend domain
        response.setHeader("Access-Control-Allow-Origin", "https://houses.westerduin.eu");  // Allow only your frontend
        response.setHeader("Access-Control-Allow-Credentials", "true");  // Allow credentials (cookies, etc.)
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");

        // If it's an OPTIONS request (pre-flight check), we can just return 200
        if ("OPTIONS".equals(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        // Set content type to JSON
        response.setContentType("application/json");

        String url = "jdbc:mysql://nozomi.proxy.rlwy.net:20003/school";
        String user = "root";
        String password = "PcPRhDcYaVtsVhyDjLLUPyjxJhdqbeXI";

        JSONArray students = new JSONArray();

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            String sql = "SELECT student_id AS id, first_name, last_name FROM students";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                JSONObject student = new JSONObject();
                student.put("id", rs.getInt("id"));
                student.put("first_name", rs.getString("first_name"));
                student.put("last_name", rs.getString("last_name"));
                students.put(student);
            }

            PrintWriter out = response.getWriter();
            out.print(students.toString());
            out.flush();

        } catch (Exception e) {
            e.printStackTrace();
            // Sending a JSON error message
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("error", "Failed to fetch students");
            errorResponse.put("message", e.getMessage());
            response.getWriter().write(errorResponse.toString());
        }
    }
}
