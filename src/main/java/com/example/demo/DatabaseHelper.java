package com.example.demo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseHelper {
    public static Connection getConnection() throws SQLException {
        // Use the connection details you provided
        return DriverManager.getConnection(
                "jdbc:mysql://maglev.proxy.rlwy.net:52297/railway",  // Host and Port
                "root",                                            // Username
                "NnHJemHDJCcbwQFKMkkmbTCPubLNyIaM"                 // Password
        );
    }
}

