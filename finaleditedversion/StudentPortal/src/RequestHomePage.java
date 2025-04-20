import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class Course {
    String courseName;
    int creditHour;
    int ects;
    String requirement;

    Course(String courseName, int creditHour, int ects, String requirement) {
        this.courseName = courseName;
        this.creditHour = creditHour;
        this.ects = ects;
        this.requirement = requirement;
    }
}

class RequestHomePage {

    public RequestHomePage() {
        JFrame frame = new JFrame("Course Registration");
        frame.setSize(900, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) {
                    return Boolean.class;
                }
                return String.class;
            }
        };

        model.addColumn("Select");
        model.addColumn("Course Name");
        model.addColumn("Credit Hour");
        model.addColumn("ETCS");
        model.addColumn("Prerequisite");

        JTable courseTable = new JTable(model);
        courseTable.setFillsViewportHeight(true);

        List<Course> courses = getRandomCourses(7);

        for (Course course : courses) {
            model.addRow(new Object[]{false, course.courseName, course.creditHour, course.ects, course.requirement});
        }


        JLabel idLabel = new JLabel("ID Number:");
        JTextField idField = new JTextField(15);


        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(e -> {
            String studentID = idField.getText().trim();
            if (studentID.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter your ID number.");
                return;
            }


            if (!isValidStudentID(studentID)) {
                JOptionPane.showMessageDialog(frame, "Unknown ID number used.");
                return;
            }

            int selectedCount = 0;
            List<String> selectedCourses = new ArrayList<>();

            for (int i = 0; i < model.getRowCount(); i++) {
                boolean isSelected = (Boolean) model.getValueAt(i, 0);
                if (isSelected) {
                    selectedCount++;
                    String courseName = (String) model.getValueAt(i, 1);
                    selectedCourses.add(courseName);
                }
            }

            if (selectedCount < 3) {
                JOptionPane.showMessageDialog(frame, "You must register for at least 3 courses.");
            } else {
                if (isAlreadyRegistered(studentID)) {
                    JOptionPane.showMessageDialog(frame, "Your data has already been registered. Go to the status check menu to check your status.");
                } else {
                    saveRegistrationData(studentID, selectedCourses, selectedCount);
                    JOptionPane.showMessageDialog(frame, "Congratulations! Your data has been saved. Go to the status check menu to check your status.");
                }
            }
        });

        JCheckBox checkAllCourses = new JCheckBox("Select All Courses");
        checkAllCourses.addActionListener(e -> {
            boolean isSelected = checkAllCourses.isSelected();
            for (int i = 0; i < model.getRowCount(); i++) {
                model.setValueAt(isSelected, i, 0);
            }
        });

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(checkAllCourses);
        topPanel.add(idLabel);
        topPanel.add(idField);

        JScrollPane scrollPane = new JScrollPane(courseTable);

        frame.setLayout(new BorderLayout());
        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(registerButton, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private List<Course> getRandomCourses(int numberOfCourses) {
        List<Course> courseList = new ArrayList<>();

        String query = "SELECT course_name, credit_hour, ETCS, requirement FROM courses";
        try (Connection conn = DatabaseConfig.getConnection(); // Using DatabaseConfig class for connection
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String courseName = rs.getString("course_name");
                int creditHour = rs.getInt("credit_hour");
                int ects = rs.getInt("ETCS");
                String requirement = rs.getString("requirement");

                courseList.add(new Course(courseName, creditHour, ects, requirement));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Collections.shuffle(courseList);
        return courseList.size() > numberOfCourses ? courseList.subList(0, numberOfCourses) : courseList;
    }

    private void saveRegistrationData(String studentID, List<String> selectedCourses, int selectedCount) {
        String coursesRegistered = String.join(", ", selectedCourses);
        String query = "INSERT INTO registeredcourses (idnumber, num_courses, courses_registered) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection(); // Using DatabaseConfig class for connection
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, studentID);
            stmt.setInt(2, selectedCount);
            stmt.setString(3, coursesRegistered);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean isAlreadyRegistered(String studentID) {
        String query = "SELECT COUNT(*) FROM registeredcourses WHERE idnumber = ?";
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
}
