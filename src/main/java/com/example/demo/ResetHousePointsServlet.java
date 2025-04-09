package com.example.demo;

import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;

public class ResetHousePointsServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setHeader("Access-Control-Allow-Origin", "https://houses.westerduin.eu");
        response.setHeader("Access-Control-Allow-Credentials", "true");



        String url = "jdbc:mysql://nozomi.proxy.rlwy.net:20003/school";
        String user = "root";
        String password = "PcPRhDcYaVtsVhyDjLLUPyjxJhdqbeXI";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            // Reset all student points
            PreparedStatement resetStudentPoints = conn.prepareStatement("UPDATE students SET points = 0");
            resetStudentPoints.executeUpdate();

            // Reset house points
            PreparedStatement resetHousePoints = conn.prepareStatement("UPDATE houses SET points = 0");
            resetHousePoints.executeUpdate();

            response.getWriter().println("✅ All house and student points have been reset.");
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("❌ Failed to reset points: " + e.getMessage());
        }
    }
}
