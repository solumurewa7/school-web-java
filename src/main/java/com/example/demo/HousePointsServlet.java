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

        // Debug: Start of method
        System.out.println("🟢 Start of the doGet method!");

        try {
            // Try to connect to the database
            System.out.println("🔌 Connecting to the database...");
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://tramway.proxy.rlwy.net:50944/railway",
                    "root", "UZgNvgdRBJsyFtShwlrldLEclQrURJZb"
            );
            System.out.println("🟢 Successfully connected to the database!");

            // Debug: Check if the connection is good
            if (conn != null) {
                System.out.println("🔌 Connection is open!");
            } else {
                System.out.println("❌ Failed to connect!");
            }

            // Execute the query to get the points
            System.out.println("💬 Executing SQL query...");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT house_name, points FROM houses");

            // Debug: Check if the ResultSet has data
            if (!rs.next()) {
                System.out.println("❌ No data returned from the query.");
            } else {
                System.out.println("🟢 Data retrieved successfully from the database.");
                do {
                    // For each row, get the house name and points
                    String houseName = rs.getString("house_name");
                    int points = rs.getInt("points");
                    System.out.println("💬 Found house: " + houseName + " with points: " + points);

                    // Store the data in the JSON object
                    housePoints.put(houseName.toLowerCase(), points);

                    // Debug: Check what’s in the JSON object so far
                    System.out.println("📝 Current JSON data: " + housePoints.toString());

                } while (rs.next());  // Continue until all rows are processed
            }

            // Debug: Closing resources
            System.out.println("🔒 Closing ResultSet, Statement, and Connection...");
            rs.close();
            stmt.close();
            conn.close();

        } catch (SQLException e) {
            // Catch any SQL exceptions
            System.out.println("❌ SQL Exception: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(500);
            housePoints.put("error", "Internal server error - SQL issue.");
        }

        // Debug: Before sending the response
        System.out.println("📝 Preparing to send the data to the frontend...");

        // Debug: Check final JSON data before sending it
        System.out.println("📡 Sending the data: " + housePoints.toString());

        // Send the JSON response
        PrintWriter out = response.getWriter();
        out.print(housePoints.toString());
        out.flush();

        // Debug: End of method
        System.out.println("🟢 End of the doGet method!");
    }
}
