import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class Status extends JFrame {

    private JTextField idField;
    private JButton checkStatusButton;
    private JTextArea statusTextArea;

    public Status() {
        setTitle("Service Request Status");
        setSize(900, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(new BorderLayout(10, 10));


        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout());


        idField = new JTextField(15);
        checkStatusButton = new JButton("Check Status");

        inputPanel.add(new JLabel("Enter ID Number:"));
        inputPanel.add(idField);
        inputPanel.add(checkStatusButton);

        add(inputPanel, BorderLayout.NORTH);


        statusTextArea = new JTextArea(10, 30);
        statusTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(statusTextArea);
        add(scrollPane, BorderLayout.CENTER);


        checkStatusButton.addActionListener(e -> checkStatus());

        setVisible(true);
    }

    private void checkStatus() {
        String studentID = idField.getText().trim();

        if (studentID.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your ID number.");
            return;
        }

        // Fetch the status from the database
        String status = getStatusFromDatabase(studentID);

        if (status == null) {
            statusTextArea.setText("No service request found for this ID.");
        } else {

            statusTextArea.setText(status);
        }
    }


    private String getStatusFromDatabase(String studentID) {
        String query = "SELECT dorm, section, mealcard, mealplan, approval FROM servicerequest WHERE idnumber = ?";
        StringBuilder status = new StringBuilder();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, studentID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    status.append("ID Number: ").append(studentID).append("\n");
                    status.append("Dorm: ").append(rs.getString("dorm")).append("\n");
                    status.append("Section: ").append(rs.getString("section")).append("\n");
                    status.append("Meal Card: ").append(rs.getString("mealcard")).append("\n");
                    status.append("Meal Plan: ").append(rs.getString("mealplan")).append("\n");
                    status.append("Approval Status: ").append(rs.getString("approval")).append("\n");
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        return status.toString();
    }


}
