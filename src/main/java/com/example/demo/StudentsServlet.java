package com.example.demo;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;

public class StudentsServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // ✅ Add this line to fix CORS
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setContentType("application/json");

        String url = "jdbc:mysql://nozomi.proxy.rlwy.net:20003/school";
        String user = "root";
        String password = "PcPRhDcYaVtsVhyDjLLUPyjxJhdqbeXI";

        JSONArray studentsArray = new JSONArray();

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            String sql = "SELECT id, first_name, last_name FROM students";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                JSONObject student = new JSONObject();
                student.put("id", rs.getInt("id"));
                student.put("first_name", rs.getString("first_name"));
                student.put("last_name", rs.getString("last_name"));
                studentsArray.put(student);
            }

            response.getWriter().write(studentsArray.toString());

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("{\"error\": \"Failed to fetch students\"}");
        }
    }
}
