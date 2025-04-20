import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

class StudentHome {
    private JLabel photoLabel;
    private JTextField textName, textDepartment, textIDNumber, textAge, textAcademicYear;
    private JButton saveButton, updateButton;
    private File photoFile;

    public StudentHome(String name, String idNumber) {
        JFrame frame = new JFrame("Student Home");
        frame.setSize(800, 600);
        frame.setMinimumSize(new Dimension(800, 600));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(null);

        JLabel header = new JLabel("My Profile", SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 24));
        header.setBounds(300, 20, 200, 40);
        panel.add(header);

        JLabel labelName = new JLabel("Full Name:");
        labelName.setBounds(200, 80, 100, 25);
        panel.add(labelName);

        textName = new JTextField();
        textName.setBounds(300, 80, 200, 25);
        panel.add(textName);

        JLabel labelDepartment = new JLabel("Department:");
        labelDepartment.setBounds(200, 120, 100, 25);
        panel.add(labelDepartment);

        textDepartment = new JTextField();
        textDepartment.setBounds(300, 120, 200, 25);
        textDepartment.setEditable(false);
        panel.add(textDepartment);

        JLabel labelIDNumber = new JLabel("ID Number:");
        labelIDNumber.setBounds(200, 160, 100, 25);
        panel.add(labelIDNumber);

        textIDNumber = new JTextField();
        textIDNumber.setBounds(300, 160, 200, 25);
        textIDNumber.setEditable(false);
        panel.add(textIDNumber);

        JLabel labelAge = new JLabel("Age:");
        labelAge.setBounds(200, 200, 100, 25);
        panel.add(labelAge);

        textAge = new JTextField();
        textAge.setBounds(300, 200, 200, 25);
        panel.add(textAge);

        JLabel labelAcademicYear = new JLabel("Academic Year:");
        labelAcademicYear.setBounds(200, 240, 100, 25);
        panel.add(labelAcademicYear);

        textAcademicYear = new JTextField();
        textAcademicYear.setBounds(300, 240, 200, 25);
        panel.add(textAcademicYear);

        JLabel labelPhoto = new JLabel("Upload Photo:");
        labelPhoto.setBounds(200, 280, 100, 25);
        panel.add(labelPhoto);

        JButton uploadButton = new JButton("Upload");
        uploadButton.setBounds(300, 280, 200, 25);
        uploadButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int option = fileChooser.showOpenDialog(null);
            if (option == JFileChooser.APPROVE_OPTION) {
                photoFile = fileChooser.getSelectedFile();
                ImageIcon icon = new ImageIcon(new ImageIcon(photoFile.getAbsolutePath()).getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH));
                photoLabel.setIcon(icon);
            }
        });
        panel.add(uploadButton);

        photoLabel = new JLabel();
        photoLabel.setBounds(550, 120, 100, 100);
        photoLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        panel.add(photoLabel);

        saveButton = new JButton("Save");
        saveButton.setBounds(250, 350, 100, 30);
        saveButton.addActionListener(e -> saveProfile(name, idNumber));
        panel.add(saveButton);

        updateButton = new JButton("Update");
        updateButton.setBounds(400, 350, 100, 30);
        updateButton.addActionListener(e -> enableEditing());
        panel.add(updateButton);

        JButton hamburgerMenu = new JButton("≡");
        hamburgerMenu.setBounds(700, 20, 50, 30);
        hamburgerMenu.addActionListener(e -> showHamburgerMenu(frame));
        panel.add(hamburgerMenu);

        loadProfile(name, idNumber);

        frame.add(panel);
        frame.setVisible(true);
    }

    private void loadProfile(String name, String idNumber) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            String query = "SELECT * FROM studentidinfo WHERE IDnumber = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, idNumber);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                textName.setText(rs.getString("Name"));
                textDepartment.setText(rs.getString("Department"));
                textIDNumber.setText(rs.getString("IDnumber"));
            } else {
                textName.setText(name);
                textDepartment.setText("Unknown");
                textIDNumber.setText(idNumber);
            }

            query = "SELECT * FROM student_details WHERE IDnumber = ?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, idNumber);
            rs = stmt.executeQuery();

            if (rs.next()) {
                textAge.setText(rs.getString("Age"));
                textAcademicYear.setText(rs.getString("AcademicYear"));
                String photoPath = rs.getString("PhotoPath");
                if (photoPath != null) {
                    photoFile = new File(photoPath);
                    ImageIcon icon = new ImageIcon(new ImageIcon(photoFile.getAbsolutePath()).getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH));
                    photoLabel.setIcon(icon);
                }
            }

            disableEditing();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error loading profile: " + e.getMessage());
        }
    }

    private void saveProfile(String name, String idNumber) {
        if (idNumber == null || idNumber.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Error: Student ID is missing. Please try again.");
            return;
        }

        try (Connection conn = DatabaseConfig.getConnection()) {
            String query = "REPLACE INTO student_details (IDnumber, Name, Age, AcademicYear, PhotoPath) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, idNumber);
            stmt.setString(2, textName.getText());
            stmt.setString(3, textAge.getText());
            stmt.setString(4, textAcademicYear.getText());
            stmt.setString(5, photoFile != null ? photoFile.getAbsolutePath() : null);

            stmt.executeUpdate();

            JOptionPane.showMessageDialog(null, "Profile saved successfully.");
            disableEditing();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error saving profile: " + e.getMessage());
        }
    }

    private void enableEditing() {
        textName.setEditable(true);
        textAge.setEditable(true);
        textAcademicYear.setEditable(true);
    }

    private void disableEditing() {
        textName.setEditable(false);
        textAge.setEditable(false);
        textAcademicYear.setEditable(false);
    }

    private void showHamburgerMenu(JFrame parentFrame) {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem requestItem = new JMenuItem("Course Registration");
        requestItem.addActionListener(e -> {
            parentFrame.dispose();
         new RequestHomePage();
        });



        JMenuItem serviceItem = new JMenuItem("Service Request");
        serviceItem.addActionListener(e -> {
            parentFrame.dispose();
            new Service();
        });
        JMenuItem statusItem = new JMenuItem("Registration Status");
        statusItem.addActionListener(e -> {
            parentFrame.dispose();
            new Status();
        });

        menu.add(requestItem);
        menu.add(serviceItem);
        menu.add(statusItem);

        menu.show(parentFrame, 700, 50);
    }

}
