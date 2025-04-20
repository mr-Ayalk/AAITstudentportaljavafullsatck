import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DormAvailability {
    private JPanel panel;

    public DormAvailability() {

        panel = new JPanel(new BorderLayout());


        JPanel headerPanel = new JPanel();
        JLabel headerLabel = new JLabel("AAIT Dorm Availability", JLabel.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerPanel.add(headerLabel);
        panel.add(headerPanel, BorderLayout.NORTH);


        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Dorm ID");
        model.addColumn("Location");
        model.addColumn("Number of Beds");
        model.addColumn("Floor");
        model.addColumn("Availability");

        JTable dormTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(dormTable);
        panel.add(scrollPane, BorderLayout.CENTER);


        populateTable(model);

        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> {
            model.setRowCount(0);
            populateTable(model);
        });
        footerPanel.add(refreshButton);
        panel.add(footerPanel, BorderLayout.SOUTH);
    }

    private void populateTable(DefaultTableModel model) {
        String query = "SELECT dormid, dormlocation, numberofbeds, floorofbuilding, availability FROM dorminfo";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int dormId = rs.getInt("dormid");
                String location = rs.getString("dormlocation");
                int beds = rs.getInt("numberofbeds");
                int floor = rs.getInt("floorofbuilding");
                String availability = rs.getString("availability");

                model.addRow(new Object[]{dormId, location, beds, floor, availability});
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(panel, "Error fetching data from database.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    public JPanel getPanel() {
        return panel;
    }
}
