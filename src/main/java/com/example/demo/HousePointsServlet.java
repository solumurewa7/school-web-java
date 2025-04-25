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
        // ✅ Required CORS - Add detailed headers
        System.out.println("🟢 Setting up CORS headers...");

        // Allow requests from your frontend
        response.setHeader("Access-Control-Allow-Origin", "https://houses.westerduin.eu");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");

        // Content type for the response
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Debug: Checking incoming request headers
        System.out.println("🟡 Incoming Request Headers: ");
        System.out.println("Origin: " + request.getHeader("Origin"));
        System.out.println("Method: " + request.getMethod());

        // If it's an OPTIONS request (pre-flight request for CORS), just respond with 200 OK
        if ("OPTIONS".equals(request.getMethod())) {
            System.out.println("🟢 Pre-flight CORS request received. Responding with 200 OK.");
            response.setStatus(HttpServletResponse.SC_OK);
            return; // Short-circuit further processing
        }

        JSONObject housePoints = new JSONObject();

        try {
            // Database connection logic
            System.out.println("🔌 Connecting to the database...");
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://tramway.proxy.rlwy.net:50944/railway",
                    "root", "UZgNvgdRBJsyFtShwlrldLEclQrURJZb"
            );
            System.out.println("🟢 Connected to the database!");

            // Query to get the points
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT house_name, points FROM houses");

            // Check if any data was returned
            if (!rs.next()) {
                System.out.println("❌ No data returned from the query.");
            } else {
                System.out.println("🟢 Data retrieved successfully from the database.");
                do {
                    String houseName = rs.getString("house_name");
                    int points = rs.getInt("points");
                    housePoints.put(houseName.toLowerCase(), points);
                    System.out.println("💬 Adding to JSON: " + houseName + ": " + points);
                } while (rs.next());
            }

            // Clean up database resources
            rs.close();
            stmt.close();
            conn.close();

        } catch (Exception e) {
            // Handle errors and send back error message in JSON
            e.printStackTrace();
            response.setStatus(500);
            housePoints.put("error", "Internal server error");
        }

        // Debug: Sending data
        System.out.println("🟢 Sending response data: " + housePoints.toString());

        // Send the JSON response to the client
        PrintWriter out = response.getWriter();
        out.print(housePoints.toString());
        out.flush();

        System.out.println("🟢 Response sent successfully!");
    }
}
