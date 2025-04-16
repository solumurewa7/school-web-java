package com.example.demo;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet("/test-db")
public class TestDBServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/plain");
        try (PrintWriter out = response.getWriter()) {
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://tramway.proxy.rlwy.net:50944/railway?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC",
                    "root",
                    "UZgNvgdRBJsyFtShwlrldLEclQrURJZb"
            );
            out.println("✅ Connection success to Railway SQL!");
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("❌ Connection failed: " + e.getMessage());
        }
    }
}
