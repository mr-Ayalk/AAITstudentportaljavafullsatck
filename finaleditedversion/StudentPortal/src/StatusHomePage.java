import javax.swing.*;
class StatusHomePage {
    public StatusHomePage() {
        JFrame frame = new JFrame("Status Home Page");
        frame.setSize(800, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JMenuBar menuBar = new JMenuBar();

        JMenu registrationMenu = new JMenu("Registration");
        JMenu serviceMenu = new JMenu("Service");

        menuBar.add(registrationMenu);
        menuBar.add(serviceMenu);

        frame.setJMenuBar(menuBar);
        frame.setVisible(true);
    }
}