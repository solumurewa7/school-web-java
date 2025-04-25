package com.example.demo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class TestDatabaseConnection {

    public static void main(String[] args) {
        String url = "jdbc:mysql://tramway.proxy.rlwy.net:50944/railway";
        String user = "root";
        String password = "UZgNvgdRBJsyFtShwlrldLEclQrURJZb";

        try {
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("🟢 Connected to the database!");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT 1 AS test");

            while (rs.next()) {
                System.out.println("💬 Database response: " + rs.getInt("test"));
            }

            conn.close();
        } catch (Exception e) {
            System.out.println("❌ Database connection failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
