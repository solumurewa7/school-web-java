import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/add-student")
public class AddStudentServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("🔥 AddStudentServlet has been called!");
        // Get form data
        String[] nameParts = request.getParameter("student-name").split(" ");
        String firstName = nameParts[0];
        String lastName = nameParts.length > 1 ? nameParts[1] : "";

        String studentEmail = request.getParameter("student-email");
        String parentName = request.getParameter("parent-name");
        String parentEmail = request.getParameter("parent-email");
        String house = request.getParameter("house");
        int points = Integer.parseInt(request.getParameter("points"));

        String[] parentNameParts = parentName.split(" ");
        String parentFirstName = parentNameParts[0];
        String parentLastName = parentNameParts.length > 1 ? parentNameParts[1] : "";

        String url = "jdbc:mysql://localhost:3306/school";
        String user = "root";
        String password = "yourPassword"; // ← Replace with your actual MySQL password

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            // 1. Insert student
            String insertStudentSQL = "INSERT INTO students (first_name, last_name, email, house, points) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement studentStmt = conn.prepareStatement(insertStudentSQL, PreparedStatement.RETURN_GENERATED_KEYS);
            studentStmt.setString(1, firstName);
            studentStmt.setString(2, lastName);
            studentStmt.setString(3, studentEmail);
            studentStmt.setString(4, house);
            studentStmt.setInt(5, points);
            studentStmt.executeUpdate();

            // Get student_id that was just inserted
            ResultSet rs = studentStmt.getGeneratedKeys();
            int studentId = -1;
            if (rs.next()) {
                studentId = rs.getInt(1);
            }

            // 2. Insert parent linked to the student
            String insertParentSQL = "INSERT INTO parents (student_id, parent_type, first_name, last_name, email) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement parentStmt = conn.prepareStatement(insertParentSQL);
            parentStmt.setInt(1, studentId);
            parentStmt.setString(2, "Parent");
            parentStmt.setString(3, parentFirstName);
            parentStmt.setString(4, parentLastName);
            parentStmt.setString(5, parentEmail);
            parentStmt.executeUpdate();

            // Response
            response.getWriter().println("✅ Student and parent added successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("❌ Error adding student and parent.");
        }
    }
}
