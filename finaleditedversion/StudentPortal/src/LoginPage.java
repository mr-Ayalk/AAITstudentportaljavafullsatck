import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

class LoginPage {
    private JFrame frame;

    public LoginPage() {
        frame = new JFrame("AAIT Portal - Login");
        frame.setSize(800, 500);
        frame.setMinimumSize(new Dimension(800, 500));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null);
        mainPanel.setBackground(new Color(240, 240, 240));

        JPanel announcementPanel = new JPanel();
        announcementPanel.setBounds(0, 0, 800, 50);
        announcementPanel.setBackground(new Color(200, 230, 255));

        JLabel announcementLabel = new JLabel("Welcome to AAIT Portal", SwingConstants.CENTER);
        announcementLabel.setFont(new Font("Arial", Font.BOLD, 16));
        announcementLabel.setForeground(new Color(50, 50, 150));
        announcementPanel.setLayout(new BorderLayout());
        announcementPanel.add(announcementLabel, BorderLayout.CENTER);

        try (Connection conn = DatabaseConfig.getConnection()) {
            String query = "SELECT message FROM announcement ORDER BY id DESC LIMIT 1";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                announcementLabel.setText(rs.getString("message"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        mainPanel.add(announcementPanel);

        JPanel loginPanel = new JPanel();
        loginPanel.setBounds(250, 100, 300, 300);
        loginPanel.setBackground(new Color(230, 240, 255));
        loginPanel.setLayout(null);
        loginPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));

        JLabel labelTitle = new JLabel("AAIT Portal", SwingConstants.CENTER);
        labelTitle.setFont(new Font("Arial", Font.BOLD, 24));
        labelTitle.setForeground(new Color(50, 50, 150));
        labelTitle.setBounds(0, 20, 300, 30);
        loginPanel.add(labelTitle);

        JLabel labelUsername = new JLabel("Username:");
        labelUsername.setBounds(30, 80, 100, 25);
        labelUsername.setFont(new Font("Arial", Font.PLAIN, 14));
        loginPanel.add(labelUsername);

        JTextField textUsername = new JTextField();
        textUsername.setBounds(120, 80, 150, 25);
        loginPanel.add(textUsername);

        JLabel labelPassword = new JLabel("Password:");
        labelPassword.setBounds(30, 120, 100, 25);
        labelPassword.setFont(new Font("Arial", Font.PLAIN, 14));
        loginPanel.add(labelPassword);

        JPasswordField textPassword = new JPasswordField();
        textPassword.setBounds(120, 120, 150, 25);
        loginPanel.add(textPassword);

        JRadioButton adminButton = new JRadioButton("Admin");
        adminButton.setBounds(50, 160, 80, 25);
        adminButton.setFont(new Font("Arial", Font.PLAIN, 14));
        adminButton.setBackground(new Color(230, 240, 255));

        JRadioButton studentButton = new JRadioButton("Student");
        studentButton.setBounds(150, 160, 100, 25);
        studentButton.setFont(new Font("Arial", Font.PLAIN, 14));
        studentButton.setBackground(new Color(230, 240, 255));

        ButtonGroup group = new ButtonGroup();
        group.add(adminButton);
        group.add(studentButton);
        loginPanel.add(adminButton);
        loginPanel.add(studentButton);

        JButton loginButton = new JButton("Login");
        loginButton.setBounds(100, 220, 100, 30);
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setBackground(new Color(50, 150, 50));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginPanel.add(loginButton);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = textUsername.getText();
                String password = new String(textPassword.getPassword());
                boolean isAdmin = adminButton.isSelected();

                try (Connection conn = DatabaseConfig.getConnection()) {
                    if (isAdmin) {
                        String query = "SELECT * FROM admin WHERE username=? AND password=?";
                        PreparedStatement stmt = conn.prepareStatement(query);
                        stmt.setString(1, username);
                        stmt.setString(2, password);
                        ResultSet rs = stmt.executeQuery();

                        if (rs.next()) {
                            new AdminHome();
                            frame.dispose();
                        } else {
                            JOptionPane.showMessageDialog(frame, "Invalid Admin Credentials");
                        }
                    } else {
                        String query = "SELECT * FROM studentidinfo WHERE username=? AND password=?";
                        PreparedStatement stmt = conn.prepareStatement(query);
                        stmt.setString(1, username);
                        stmt.setString(2, password);
                        ResultSet rs = stmt.executeQuery();

                        if (rs.next()) {
                            new StudentHome(rs.getString("Name"), rs.getString("IDnumber"));
                            frame.dispose();
                        } else {
                            JOptionPane.showMessageDialog(frame, "Invalid Student Credentials");
                        }
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(frame, "Database error: " + ex.getMessage());
                }
            }
        });

        mainPanel.add(loginPanel);
        frame.add(mainPanel);
        frame.setVisible(true);
    }
    public static void main(String[] args) {
        new LoginPage();
    }
}
