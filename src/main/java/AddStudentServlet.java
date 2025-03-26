import java.io.IOException;
import java.sql.*;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AddStudentServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("🔥 AddStudentServlet has been called!");

        String[] nameParts = request.getParameter("student-name").split(" ");
        String firstName = nameParts[0];
        String lastName = nameParts.length > 1 ? nameParts[1] : "";

        String studentEmail = request.getParameter("student-email");
        String parentName = request.getParameter("parent-name");
        String parentEmail = request.getParameter("parent-email");
        String parentType = request.getParameter("parent-type");
        String house = request.getParameter("house");
        int points = Integer.parseInt(request.getParameter("points"));

        String[] parentNameParts = parentName.split(" ");
        String parentFirstName = parentNameParts[0];
        String parentLastName = parentNameParts.length > 1 ? parentNameParts[1] : "";

        // Connect to Railway SQL
        String url = "jdbc:mysql://nozomi.proxy.rlwy.net:20003/railway";
        String user = "root";
        String password = "PcPRhDcYaVtsVhyDjLLUPyjxJhdqbeXI";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {

            // Step 1: Insert student
            String insertStudentSQL = "INSERT INTO students (first_name, last_name, email, house, points) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement studentStmt = conn.prepareStatement(insertStudentSQL, Statement.RETURN_GENERATED_KEYS);
            studentStmt.setString(1, firstName);
            studentStmt.setString(2, lastName);
            studentStmt.setString(3, studentEmail);
            studentStmt.setString(4, house);
            studentStmt.setInt(5, points);
            studentStmt.executeUpdate();

            ResultSet rs = studentStmt.getGeneratedKeys();
            int studentId = -1;
            if (rs.next()) {
                studentId = rs.getInt(1);
            }

            // Step 2: Insert parent
            String insertParentSQL = "INSERT INTO parents (student_id, parent_type, first_name, last_name, email) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement parentStmt = conn.prepareStatement(insertParentSQL);
            parentStmt.setInt(1, studentId);
            parentStmt.setString(2, parentType);
            parentStmt.setString(3, parentFirstName);
            parentStmt.setString(4, parentLastName);
            parentStmt.setString(5, parentEmail);
            parentStmt.executeUpdate();

            // Step 3: Update house points
            String updateHouseSQL = "UPDATE houses SET total_points = total_points + ? WHERE house_name = ?";
            PreparedStatement houseStmt = conn.prepareStatement(updateHouseSQL);
            houseStmt.setInt(1, points);
            houseStmt.setString(2, house);
            houseStmt.executeUpdate();

            response.getWriter().println("✅ Student, parent, and house points updated successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("❌ Error saving data.");
        }
    }
}
