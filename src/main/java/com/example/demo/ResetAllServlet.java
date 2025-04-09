package com.example.demo;

import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;

public class ResetAllServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {


        response.setHeader("Access-Control-Allow-Origin", "https://houses.westerduin.eu");
        response.setHeader("Access-Control-Allow-Credentials", "true");


        String url = "jdbc:mysql://nozomi.proxy.rlwy.net:20003/school";
        String user = "root";
        String password = "PcPRhDcYaVtsVhyDjLLUPyjxJhdqbeXI";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            // Delete all parents
            PreparedStatement deleteParents = conn.prepareStatement("DELETE FROM parents");
            deleteParents.executeUpdate();

            // Delete all students
            PreparedStatement deleteStudents = conn.prepareStatement("DELETE FROM students");
            deleteStudents.executeUpdate();

            // Reset house points
            PreparedStatement resetHouses = conn.prepareStatement("UPDATE houses SET points = 0");
            resetHouses.executeUpdate();

            response.getWriter().println("✅ All student, parent, and house data reset successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("❌ Failed to reset all data: " + e.getMessage());
        }
    }
}
