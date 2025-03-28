package com.example.demo;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.*;
import java.io.*;
import java.sql.*;
import java.util.Collection;

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

        Part filePart = request.getPart("student-file");

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
                String[] fields = line.trim().split("\\s+");
                if (fields.length != 8) {
                    System.out.println("⚠️ Skipping line (invalid format): " + line);
                    continue;
                }

                String studentFirst = fields[0];
                String studentLast = fields[1];
                String studentEmail = fields[2];
                String houseName = fields[3];
                String parentFirst = fields[4];
                String parentLast = fields[5];
                String parentEmail = fields[6];
                String parentType = fields[7];

                // Check if house exists
                try (PreparedStatement houseCheck = conn.prepareStatement("SELECT 1 FROM houses WHERE house_name = ?")) {
                    houseCheck.setString(1, houseName);
                    ResultSet rs = houseCheck.executeQuery();
                    if (!rs.next()) {
                        System.out.println("⚠️ House not found: " + houseName);
                        continue;
                    }
                }

                // Insert into students
                int studentId = -1;
                String insertStudent = "INSERT INTO students (first_name, last_name, email, house_name, points) VALUES (?, ?, ?, ?, 0)";
                try (PreparedStatement stmt = conn.prepareStatement(insertStudent, Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setString(1, studentFirst);
                    stmt.setString(2, studentLast);
                    stmt.setString(3, studentEmail);
                    stmt.setString(4, houseName);
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