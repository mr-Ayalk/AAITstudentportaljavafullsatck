import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class StudentInfoPage {
    private JPanel panel;

    public StudentInfoPage() {

        panel = new JPanel(new BorderLayout());

        JPanel headerPanel = new JPanel();
        JLabel headerLabel = new JLabel("AAIT Students Information Page", JLabel.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerPanel.add(headerLabel);
        panel.add(headerPanel, BorderLayout.NORTH);

        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Student ID");
        model.addColumn("Name");
        model.addColumn("ID Number");
        model.addColumn("Username");
        model.addColumn("Password");
        model.addColumn("Department");


        JTable studentTable = new JTable(model);
        studentTable.setFillsViewportHeight(true);

        populateStudentTable(model);

        JScrollPane scrollPane = new JScrollPane(studentTable);
        panel.add(scrollPane, BorderLayout.CENTER);
    }

    private void populateStudentTable(DefaultTableModel model) {
        String query = "SELECT studentid, Name, IDnumber, username, password, department FROM studentidinfo";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int studentId = rs.getInt("studentid");
                String name = rs.getString("Name");
                String idNumber = rs.getString("IDnumber");
                String username = rs.getString("username");
                String password = rs.getString("password");
                String department = rs.getString("department");


                model.addRow(new Object[]{studentId, name, idNumber, username, password, department});
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(panel, "Error fetching data from the database.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public JPanel getPanel() {
        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Student Info Page");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(900, 500);
            StudentInfoPage studentInfoPage = new StudentInfoPage();
            frame.add(studentInfoPage.getPanel());
            frame.setVisible(true);
        });
    }
}
