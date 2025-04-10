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
        // ✅ Required CORS
        response.setHeader("Access-Control-Allow-Origin", "https://houses.westerduin.eu");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setContentType("application/json");

        JSONObject housePoints = new JSONObject();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://tramway.proxy.rlwy.net:50944/railway",
                    "root",
                    "UZgNvgdRBJsyFtShwlrldLEclQrURJZb"
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
            e.printStackTrace(); // 👈 important so we see the issue in Railway logs
            response.setStatus(500);
            housePoints.put("error", "Internal server error");
        }

        PrintWriter out = response.getWriter();
        out.print(housePoints.toString());
        out.flush();
    }
}
