package com.example.demo;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import javax.servlet.http.*;
import org.json.JSONArray;
import org.json.JSONObject;

public class StudentListServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // ✅ Add CORS header
        response.setHeader("Access-Control-Allow-Origin", "*");
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
            response.getWriter().write("{\"error\":\"Failed to fetch students\"}");
        }
    }
}
