package com.example.demo;

import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet("/test-db")  // This URL will be used to access the test
public class TestDBServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Set content type for JSON response
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String url = "jdbc:mysql://tramway.proxy.rlwy.net:50944/railway";
        String user = "root";
        String password = "UZgNvgdRBJsyFtShwlrldLEclQrURJZb";

        JSONObject result = new JSONObject();

        System.out.println("🟢 TestDBServlet - Attempting to connect to the database...");
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            System.out.println("🟢 TestDBServlet - Database connection established!");

            // Test the connection by running a simple query
            String sql = "SELECT 1 AS test";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                System.out.println("🟢 TestDBServlet - Database query executed successfully.");
                result.put("success", true);
                result.put("message", "Database connection successful!");
            }

            // Clean up
            rs.close();
            stmt.close();
            conn.close();
            System.out.println("🟢 TestDBServlet - Database connection closed.");
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "Database connection failed: " + e.getMessage());
            System.out.println("❌ TestDBServlet - Database connection failed: " + e.getMessage());
        }

        // Send response
        PrintWriter out = response.getWriter();
        out.print(result.toString());
        out.flush();

        System.out.println("🟢 TestDBServlet - Response sent successfully!");
    }
}
