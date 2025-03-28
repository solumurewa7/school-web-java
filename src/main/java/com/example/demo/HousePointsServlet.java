package com.example.demo;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

public class HousePointsServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // ✅ Allow CORS from any domain (or specify yours)
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setContentType("application/json");

        String url = "jdbc:mysql://nozomi.proxy.rlwy.net:20003/school";
        String user = "root";
        String password = "PcPRhDcYaVtsVhyDjLLUPyjxJhdqbeXI";

        JSONObject json = new JSONObject();

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            String sql = "SELECT house_name, points FROM houses";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String house = rs.getString("house_name").toLowerCase();
                int points = rs.getInt("points");
                json.put(house, points);
            }

            response.getWriter().write(json.toString());
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("{\"error\":\"Failed to fetch points.\"}");
        }
    }
}
