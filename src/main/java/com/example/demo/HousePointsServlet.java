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

    private static final String DB_URL = "jdbc:mysql://tramway.proxy.rlwy.net:50944/railway";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "UZgNvgdRBJsyFtShwlrldLEclQrURJZb";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // ✅ CORS headers
        response.setHeader("Access-Control-Allow-Origin", "https://houses.westerduin.eu");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setContentType("application/json");

        JSONObject housePoints = new JSONObject();

        try (
                Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT house_name, points FROM houses")
        ) {
            while (rs.next()) {
                housePoints.put(rs.getString("house_name").toLowerCase(), rs.getInt("points"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(500);
            housePoints.put("error", "Internal server error");
        }

        PrintWriter out = response.getWriter();
        out.print(housePoints.toString());
        out.flush();
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // ✅ Preflight CORS support
        resp.setHeader("Access-Control-Allow-Origin", "https://houses.westerduin.eu");
        resp.setHeader("Access-Control-Allow-Credentials", "true");
        resp.setHeader("Access-Control-Allow-Methods", "GET, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
