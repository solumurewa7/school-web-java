package com.example.demo;

import java.io.IOException;
import java.sql.*;
import javax.servlet.http.*;
import org.json.JSONArray;
import org.json.JSONObject;

public class TopStudentsServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setHeader("Access-Control-Allow-Origin", "https://houses.westerduin.eu");
        response.setHeader("Access-Control-Allow-Credentials", "true");



        System.out.println("📊 TopStudentsServlet called!");

        String url = "jdbc:mysql://nozomi.proxy.rlwy.net:20003/school";
        String user = "root";
        String password = "PcPRhDcYaVtsVhyDjLLUPyjxJhdqbeXI";

        JSONArray jsonArray = new JSONArray();

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            String sql = "SELECT first_name, last_name, house, points FROM students ORDER BY points DESC LIMIT 10";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                JSONObject obj = new JSONObject();
                obj.put("first_name", rs.getString("first_name"));
                obj.put("last_name", rs.getString("last_name"));
                obj.put("house", rs.getString("house"));
                obj.put("points", rs.getInt("points"));
                jsonArray.put(obj);
            }

            // 👇 CORS fix
            response.setHeader("Access-Control-Allow-Origin", "*");

            response.setContentType("application/json");
            response.getWriter().write(jsonArray.toString());

        } catch (Exception e) {
            e.printStackTrace();

            // 👇 CORS for error responses too
            response.setHeader("Access-Control-Allow-Origin", "*");

            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Failed to fetch top students\"}");
        }
    }
}
