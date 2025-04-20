import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Assign {
    private JPanel panel;
    private JTable requestTable;
    private JTextField dormField;
    private JTextField mealCardField;
    private JTextField mealPlanField;
    private JTextField sectionField;

    public Assign() {

        panel = new JPanel(new BorderLayout());


        JPanel headerPanel = new JPanel();
        JLabel headerLabel = new JLabel("Service Assignment", JLabel.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerPanel.add(headerLabel);
        panel.add(headerPanel, BorderLayout.NORTH);


        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Student Name");
        model.addColumn("ID Number");
        model.addColumn("Dorm");
        model.addColumn("Section");
        model.addColumn("Meal Card");
        model.addColumn("Meal Plan");

        requestTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(requestTable);
        panel.add(scrollPane, BorderLayout.CENTER);


        JPanel footerPanel = new JPanel();
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> {
            model.setRowCount(0);
            populateTable(model);
        });
        footerPanel.add(refreshButton);
        panel.add(footerPanel, BorderLayout.SOUTH);


        JPanel assignmentPanel = new JPanel();
        assignmentPanel.setLayout(new GridLayout(5, 2, 10, 10));
        assignmentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        assignmentPanel.add(new JLabel("Dorm ID:"));
        dormField = new JTextField();
        assignmentPanel.add(dormField);

        assignmentPanel.add(new JLabel("Meal Card Number:"));
        mealCardField = new JTextField();
        assignmentPanel.add(mealCardField);

        assignmentPanel.add(new JLabel("Meal Plan:"));
        mealPlanField = new JTextField();
        assignmentPanel.add(mealPlanField);

        assignmentPanel.add(new JLabel("Section:"));
        sectionField = new JTextField();
        assignmentPanel.add(sectionField);

        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(this::handleSubmit);
        assignmentPanel.add(new JLabel()); // Spacer
        assignmentPanel.add(submitButton);

        panel.add(assignmentPanel, BorderLayout.SOUTH);


        populateTable(model);
    }

    private void populateTable(DefaultTableModel model) {
        String query = """
                SELECT s.Name, sr.idnumber, 
                       sr.dorm, sr.section, sr.mealcard, sr.mealplan
                FROM servicerequest sr
                JOIN studentidinfo s ON sr.idnumber = s.IDnumber
                WHERE sr.dorm = 'requested' OR sr.section = 'requested' 
                      OR sr.mealcard = 'requested' OR sr.mealplan = 'requested'
                """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String studentName = rs.getString("Name");
                String idNumber = rs.getString("idnumber");
                String dorm = rs.getString("dorm");
                String section = rs.getString("section");
                String mealCard = rs.getString("mealcard");
                String mealPlan = rs.getString("mealplan");

                model.addRow(new Object[]{
                        studentName, idNumber,
                        dorm != null ? dorm : "N/A",
                        section != null ? section : "N/A",
                        mealCard != null ? mealCard : "N/A",
                        mealPlan != null ? mealPlan : "N/A"
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(panel, "Error fetching data from database.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleSubmit(ActionEvent e) {
        int selectedRow = requestTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(panel, "Please select a student from the table.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String idNumber = requestTable.getValueAt(selectedRow, 1).toString();
        String dorm = dormField.getText();
        String mealCard = mealCardField.getText();
        String mealPlan = mealPlanField.getText();
        String section = sectionField.getText();

        if (dorm.isEmpty() || mealCard.isEmpty() || mealPlan.isEmpty() || section.isEmpty()) {
            JOptionPane.showMessageDialog(panel, "All fields must be filled.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String updateQuery = """
                UPDATE servicerequest
                SET dorm = ?, mealcard = ?, mealplan = ?, section = ?, approval = 'accepted'
                WHERE idnumber = ?
                """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(updateQuery)) {

            stmt.setString(1, dorm);
            stmt.setString(2, mealCard);
            stmt.setString(3, mealPlan);
            stmt.setString(4, section);
            stmt.setString(5, idNumber);

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(panel, "Assignment updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                dormField.setText("");
                mealCardField.setText("");
                mealPlanField.setText("");
                sectionField.setText("");
                ((DefaultTableModel) requestTable.getModel()).setRowCount(0);
                populateTable((DefaultTableModel) requestTable.getModel());
            } else {
                JOptionPane.showMessageDialog(panel, "Failed to update assignment.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(panel, "Database error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    public JPanel getPanel() {
        return panel;
    }
}
