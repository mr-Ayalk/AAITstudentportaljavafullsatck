import javax.swing.*;
import java.awt.*;

public class AdminHome {

    public AdminHome() {
        JFrame frame = new JFrame("Admin Home");
        frame.setSize(800, 500);
        frame.setMinimumSize(new Dimension(800, 500));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        CardLayout cardLayout = new CardLayout();
        JPanel mainPanel = new JPanel(cardLayout);

        JPanel approvalPanel = new StudentApprovalApp().getPanel();
        JPanel dormPanel = new DormAvailability().getPanel();
        JPanel assignPanel = new Assign().getPanel();
        JPanel studentInfoPanel = new StudentInfoPage().getPanel();


        mainPanel.add(approvalPanel, "Approval");
        mainPanel.add(dormPanel, "Dorm Availability");
        mainPanel.add(assignPanel, "Assign");
        mainPanel.add(studentInfoPanel, "Student Info");

        JMenuBar menuBar = new JMenuBar();

        JMenu navigationMenu = new JMenu("Navigation");
        JMenuItem approvalMenuItem = new JMenuItem("Approval");
        JMenuItem dormMenuItem = new JMenuItem("Dorm Availability");
        JMenuItem assignMenuItem = new JMenuItem("Assign");
        JMenuItem studentInfoMenuItem = new JMenuItem("Student Info");


        navigationMenu.add(approvalMenuItem);
        navigationMenu.add(dormMenuItem);
        navigationMenu.add(assignMenuItem);
        navigationMenu.add(studentInfoMenuItem);


        menuBar.add(navigationMenu);


        approvalMenuItem.addActionListener(e -> cardLayout.show(mainPanel, "Approval"));
        dormMenuItem.addActionListener(e -> cardLayout.show(mainPanel, "Dorm Availability"));
        assignMenuItem.addActionListener(e -> cardLayout.show(mainPanel, "Assign"));
        studentInfoMenuItem.addActionListener(e -> cardLayout.show(mainPanel, "Student Info"));

        frame.setJMenuBar(menuBar);
        frame.add(mainPanel);
        frame.setVisible(true);
    }


}
