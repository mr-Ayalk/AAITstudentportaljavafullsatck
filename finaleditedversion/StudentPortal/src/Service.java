import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class Service extends JFrame {

    private JTextField idField;
    private JButton assignDormButton, assignSectionButton, createMealCardButton, createMealPlanButton;
    private JButton submitRequestButton;
    private JTextArea complainTextArea;

    public Service() {
        setTitle("Student Service Request");
        setSize(900, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(new BorderLayout());

        // Header Panel
        JPanel headerPanel = new JPanel();
        JLabel headerLabel = new JLabel("Students Service Request", JLabel.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerPanel.add(headerLabel);
        add(headerPanel, BorderLayout.NORTH);

        // Services Panel
        JPanel servicesPanel = new JPanel();
        servicesPanel.setLayout(new GridLayout(6, 2, 10, 10));

        servicesPanel.add(new JLabel("ID Number"));
        idField = new JTextField(15);
        servicesPanel.add(idField);

        servicesPanel.add(new JLabel("Dorm"));
        assignDormButton = new JButton("Assign");
        assignDormButton.addActionListener(e -> handleButtonClick(assignDormButton, "dorm"));
        servicesPanel.add(assignDormButton);

        servicesPanel.add(new JLabel("Section"));
        assignSectionButton = new JButton("Assign");
        assignSectionButton.addActionListener(e -> handleButtonClick(assignSectionButton, "section"));
        servicesPanel.add(assignSectionButton);

        servicesPanel.add(new JLabel("Meal Card Number"));
        createMealCardButton = new JButton("Create");
        createMealCardButton.addActionListener(e -> handleButtonClick(createMealCardButton, "mealcard"));
        servicesPanel.add(createMealCardButton);

        servicesPanel.add(new JLabel("Meal Plan"));
        createMealPlanButton = new JButton("Create");
        createMealPlanButton.addActionListener(e -> handleButtonClick(createMealPlanButton, "mealplan"));
        servicesPanel.add(createMealPlanButton);

        add(servicesPanel, BorderLayout.CENTER);

        // Complaint Panel
        JPanel complaintPanel = new JPanel();
        complaintPanel.setLayout(new BoxLayout(complaintPanel, BoxLayout.Y_AXIS));

        JLabel complainLabel = new JLabel("Complain:");
        complainTextArea = new JTextArea(5, 30); // Text area with 5 rows and 30 columns
        complainTextArea.setLineWrap(true);
        complainTextArea.setWrapStyleWord(true);
        JScrollPane complainScrollPane = new JScrollPane(complainTextArea);

        JButton submitComplaintButton = new JButton("Submit");
        submitComplaintButton.addActionListener(e -> submitComplaint());

        complaintPanel.add(complainLabel);
        complaintPanel.add(complainScrollPane);
        complaintPanel.add(submitComplaintButton);

        // Ensure that the complaint panel is added to the bottom
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());
        bottomPanel.add(complaintPanel, BorderLayout.CENTER);

        // Submit Request Button
        submitRequestButton = new JButton("Submit Request");
        submitRequestButton.addActionListener(e -> submitRequest());
        bottomPanel.add(submitRequestButton, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);

        setVisible(true);
    }


    private void handleButtonClick(JButton button, String field) {
        button.setEnabled(false);
        button.setBackground(Color.GRAY);
        button.setText("Requested");
    }


    private void submitRequest() {
        String studentID = idField.getText().trim();

        if (studentID.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your ID number.");
            return;
        }


        if (!isValidStudentID(studentID)) {
            JOptionPane.showMessageDialog(this, "Unknown ID number used.");
            return;
        }

        updateServiceRequest(studentID);

        JOptionPane.showMessageDialog(this, "Your request is recorded.");

        resetForm();
    }


    private boolean isValidStudentID(String studentID) {
        String query = "SELECT COUNT(*) FROM studentidinfo WHERE idnumber = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, studentID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    return count > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void updateServiceRequest(String studentID) {
        String dormRequest = assignDormButton.getText().equals("Requested") ? "requested" : null;
        String sectionRequest = assignSectionButton.getText().equals("Requested") ? "requested" : null;
        String mealCardRequest = createMealCardButton.getText().equals("Requested") ? "requested" : null;
        String mealPlanRequest = createMealPlanButton.getText().equals("Requested") ? "requested" : null;

        String query = "INSERT INTO servicerequest (idnumber, dorm, section, mealcard, mealplan) VALUES (?, ?, ?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE dorm = ?, section = ?, mealcard = ?, mealplan = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, studentID);
            stmt.setString(2, dormRequest);
            stmt.setString(3, sectionRequest);
            stmt.setString(4, mealCardRequest);
            stmt.setString(5, mealPlanRequest);
            stmt.setString(6, dormRequest);
            stmt.setString(7, sectionRequest);
            stmt.setString(8, mealCardRequest);
            stmt.setString(9, mealPlanRequest);

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void submitComplaint() {
        String studentID = idField.getText().trim();
        String complaintText = complainTextArea.getText().trim();

        if (studentID.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your ID number.");
            return;
        }

        if (complaintText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your complaint.");
            return;
        }


        storeComplaint(studentID, complaintText);


        JOptionPane.showMessageDialog(this, "Your complaint is recorded.");
    }

    private void storeComplaint(String studentID, String complaintText) {
        String query = "INSERT INTO complain (idnumber, complaint_text) VALUES (?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, studentID);
            stmt.setString(2, complaintText);

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void resetForm() {
        idField.setText("");
        assignDormButton.setEnabled(true);
        assignSectionButton.setEnabled(true);
        createMealCardButton.setEnabled(true);
        createMealPlanButton.setEnabled(true);

        assignDormButton.setBackground(null);
        assignSectionButton.setBackground(null);
        createMealCardButton.setBackground(null);
        createMealPlanButton.setBackground(null);

        assignDormButton.setText("Assign");
        assignSectionButton.setText("Assign");
        createMealCardButton.setText("Create");
        createMealPlanButton.setText("Create");
    }

}
