package com.example.demo;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.*;

public class StudentListServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String url = "jdbc:mysql://nozomi.proxy.rlwy.net:20003/school";
        String user = "root";
        String password = "PcPRhDcYaVtsVhyDjLLUPyjxJhdqbeXI";

        JSONArray studentList = new JSONArray();

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            String query = "SELECT student_id, first_name, last_name FROM students";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                JSONObject student = new JSONObject();
                student.put("id", rs.getInt("student_id")); // ✅ Changed from "id"
                student.put("first_name", rs.getString("first_name"));
                student.put("last_name", rs.getString("last_name"));
                studentList.put(student);
            }

            response.setContentType("application/json");
            response.getWriter().write(studentList.toString());

        } catch (Exception e) {
            e.printStackTrace();
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Failed to fetch students\"}");
        }
    }
}
