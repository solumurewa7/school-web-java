package com.example.demo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import org.json.JSONObject;

@WebServlet("/house-points")
public class HousePointsServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


        System.out.println("🏁 HousePointsServlet was called!");



        // ✅ CORS
        response.setHeader("Access-Control-Allow-Origin", "https://houses.westerduin.eu");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        JSONObject housePoints = new JSONObject();




        try {
            System.out.println("📡 Trying to connect to DB...");
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://nozomi.proxy.rlwy.net:20003/school",
                    "root",
                    "seyolu7X"
            );
            System.out.println("✅ Connected to DB!");
        } catch (SQLException e) {
            System.out.println("❌ DB connection failed: " + e.getMessage());
            response.setStatus(500);
            response.getWriter().write("{\"error\": \"DB connection failed.\"}");
            return;
        }





        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://nozomi.proxy.rlwy.net:20003/school",
                    "root",
                    "seyolu7X"
            );


            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT house_name, points FROM houses");

            while (rs.next()) {
                housePoints.put(rs.getString("house_name").toLowerCase(), rs.getInt("points"));
            }

            rs.close();
            stmt.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(500);
            housePoints.put("error", "Internal server error");
        }

        out.print(housePoints.toString());
        out.flush();
    }
}
