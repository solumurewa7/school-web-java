package com.example.demo;


import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RecalculatePointsServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("🔄 RecalculatePointsServlet triggered!");

        String url = "jdbc:mysql://nozomi.proxy.rlwy.net:20003/railway";
        String user = "root";
        String password = "PcPRhDcYaVtsVhyDjLLUPyjxJhdqbeXI";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            // Step 1: Get total points per house
            String sumPointsSQL = "SELECT house, SUM(points) AS total FROM students GROUP BY house";
            PreparedStatement sumStmt = conn.prepareStatement(sumPointsSQL);
            ResultSet rs = sumStmt.executeQuery();

            // Step 2: Update house points
            String updateHouseSQL = "UPDATE houses SET points = ? WHERE name = ?";
            PreparedStatement updateStmt = conn.prepareStatement(updateHouseSQL);

            while (rs.next()) {
                String house = rs.getString("house");
                int total = rs.getInt("total");

                updateStmt.setInt(1, total);
                updateStmt.setString(2, house);
                updateStmt.executeUpdate();
            }

            response.getWriter().println("✅ House points successfully recalculated!");
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("❌ Error during house point recalculation.");
        }
    }
}
