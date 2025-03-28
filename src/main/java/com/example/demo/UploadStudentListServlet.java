package com.example.demo;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.*;
import java.io.*;
import java.sql.*;

@MultipartConfig
public class UploadStudentListServlet extends HttpServlet {

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "https://houses.westerduin.eu");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.setHeader("Access-Control-Allow-Origin", "https://houses.westerduin.eu");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setContentType("text/plain; charset=UTF-8");

        Part filePart = request.getPart("file");
        if (filePart == null) {
            response.getWriter().println("❌ No file uploaded.");
            return;
        }

        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(filePart.getInputStream()));
                Connection conn = DriverManager.getConnection(
                        "jdbc:mysql://nozomi.proxy.rlwy.net:20003/school", "root", "PcPRhDcYaVtsVhyDjLLUPyjxJhdqbeXI")
        ) {
            conn.setAutoCommit(false);
            String line;
            int successCount = 0;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.trim().split("\\s+");
                if (parts.length != 8) {
                    System.out.println("⚠️ Skipping line (invalid format): " + line);
                    continue;
                }

                String studentFirst = parts[0];
                String studentLast = parts[1];
                String studentEmail = parts[2];
                String houseName = parts[3];
                String parentFirst = parts[4];
                String parentLast = parts[5];
                String parentEmail = parts[6];
                String parentType = parts[7];

                // Get house_id
                int houseId = -1;
                try (PreparedStatement houseStmt = conn.prepareStatement("SELECT id FROM houses WHERE house_name = ?")) {
                    houseStmt.setString(1, houseName);
                    ResultSet rs = houseStmt.executeQuery();
                    if (rs.next()) {
                        houseId = rs.getInt("id");
                    } else {
                        System.out.println("⚠️ House not found: " + houseName);
                        continue;
                    }
                }

                // Insert into students
                int studentId = -1;
                String insertStudent = "INSERT INTO students (first_name, last_name, email, house_id, points) VALUES (?, ?, ?, ?, 0)";
                try (PreparedStatement stmt = conn.prepareStatement(insertStudent, Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setString(1, studentFirst);
                    stmt.setString(2, studentLast);
                    stmt.setString(3, studentEmail);
                    stmt.setInt(4, houseId);
                    stmt.executeUpdate();

                    ResultSet rs = stmt.getGeneratedKeys();
                    if (rs.next()) {
                        studentId = rs.getInt(1);
                    } else {
                        System.out.println("❌ Failed to get student ID for " + studentFirst);
                        continue;
                    }
                }

                // Insert into parents
                String insertParent = "INSERT INTO parents (first_name, last_name, email, parent_type, student_id) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(insertParent)) {
                    stmt.setString(1, parentFirst);
                    stmt.setString(2, parentLast);
                    stmt.setString(3, parentEmail);
                    stmt.setString(4, parentType);
                    stmt.setInt(5, studentId);
                    stmt.executeUpdate();
                }

                successCount++;
            }

            conn.commit();
            response.getWriter().println("✅ Uploaded successfully. " + successCount + " students added.");

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("❌ Error processing file: " + e.getMessage());
        }
    }
}
