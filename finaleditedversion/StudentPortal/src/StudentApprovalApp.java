import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class StudentApprovalApp {
    private JPanel panel;
    private JTable table;
    private JButton approveButton;
    private DefaultTableModel tableModel;

    public StudentApprovalApp() {

        panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(900, 500));

        tableModel = new DefaultTableModel(new String[]{"ID Number", "Name", "Number of Courses", "Courses Registered", "Acceptance"}, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);


        approveButton = new JButton("Approve");
        approveButton.addActionListener(new ApproveButtonListener());
        panel.add(approveButton, BorderLayout.SOUTH);


        loadData();
    }

    private void loadData() {
        try (Connection connection = DatabaseConfig.getConnection()) {
            String query = "SELECT rc.idnumber, si.name, rc.num_courses, rc.courses_registered, rc.acceptance " +
                    "FROM registeredcourses rc " +
                    "JOIN studentidinfo si ON rc.idnumber = si.idnumber";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                String idNumber = resultSet.getString("idnumber");
                String name = resultSet.getString("name");
                int numCourses = resultSet.getInt("num_courses");
                String coursesRegistered = resultSet.getString("courses_registered");
                String acceptance = resultSet.getString("acceptance");

                tableModel.addRow(new Object[]{idNumber, name, numCourses, coursesRegistered, acceptance});
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(panel, "Failed to load data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private class ApproveButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(panel, "Please select a student to approve.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String idNumber = tableModel.getValueAt(selectedRow, 0).toString();

            try (Connection connection = DatabaseConfig.getConnection()) {
                String updateQuery = "UPDATE registeredcourses SET acceptance = 'Approved' WHERE idnumber = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);
                preparedStatement.setString(1, idNumber);
                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    tableModel.setValueAt("Approved", selectedRow, 4);
                    JOptionPane.showMessageDialog(panel, "Student's courses approved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(panel, "Approval failed. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(panel, "Error updating database: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    public JPanel getPanel() {
        return panel;
    }
}
