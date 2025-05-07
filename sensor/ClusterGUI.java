package sensor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class ClusterGUI extends JFrame {
    private final ClusterInterface cluster;
    private final JTable dataTable;
    private final DefaultTableModel tableModel;
    private final JComboBox<String> sensorComboBox;
    private final JLabel statusLabel;
    private List<String> lastSensorIds = new ArrayList<>(); // Track last sensor list

    public ClusterGUI(ClusterInterface cluster) throws RemoteException {
        this.cluster = cluster;
        setTitle("Cluster Management");
        setSize(800, 600);
        setMinimumSize(new Dimension(700, 500));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(245, 245, 245));

        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(60, 141, 188));
        headerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        JLabel titleLabel = new JLabel("Cluster Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);

        // Sensor selection panel
        JPanel sensorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        sensorPanel.setBackground(new Color(245, 245, 245));
        sensorPanel.setBorder(BorderFactory.createTitledBorder("Select Sensor"));
        sensorPanel.add(new JLabel("Sensor:"));
        sensorComboBox = new JComboBox<>();
        sensorComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        sensorComboBox.setPreferredSize(new Dimension(200, 30));
        sensorComboBox.addActionListener(e -> updateDataOnSelection());
        sensorPanel.add(sensorComboBox);
        add(sensorPanel, BorderLayout.CENTER);

        // Status panel
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBackground(new Color(245, 245, 245));
        statusPanel.setBorder(BorderFactory.createTitledBorder("Cluster Status"));
        statusLabel = new JLabel("Status: Running");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        statusPanel.add(statusLabel);
        add(statusPanel, BorderLayout.WEST);

        // Data display
        tableModel = new DefaultTableModel(new Object[]{"Parameter", "Value", "Status", "Firmware"}, 0);
        dataTable = new JTable(tableModel);
        dataTable.setFont(new Font("Arial", Font.PLAIN, 12));
        dataTable.setRowHeight(25);
        dataTable.setGridColor(new Color(200, 200, 200));
        JScrollPane dataScrollPane = new JScrollPane(dataTable);
        dataScrollPane.setBorder(BorderFactory.createTitledBorder("Selected Sensor Data"));
        add(dataScrollPane, BorderLayout.SOUTH);

        // Start a thread to update sensor list and data
        new Thread(() -> {
            try {
                while (true) {
                    updateSensorList();
                    updateDataContinuous();
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
        }).start();

        setVisible(true);
    }

    private void updateSensorList() {
        try {
            // Get current sensor IDs
            List<String> sensorIds = cluster.getSensorIds();

            // Check if sensor list has changed
            if (!sensorIds.equals(lastSensorIds)) {
                // Store current selection
                String selectedSensor = (String) sensorComboBox.getSelectedItem();

                // Create new model
                DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
                for (String id : sensorIds) {
                    model.addElement(id);
                }
                if (sensorIds.isEmpty()) {
                    model.addElement("No Sensors Available");
                }

                // Update model
                sensorComboBox.setModel(model);

                // Restore selection if it still exists
                if (selectedSensor != null && !selectedSensor.equals("No Sensors Available") && sensorIds.contains(selectedSensor)) {
                    sensorComboBox.setSelectedItem(selectedSensor);
                } else if (!sensorIds.isEmpty()) {
                    sensorComboBox.setSelectedIndex(0); // Select first sensor if previous selection is invalid
                }

                // Update last sensor list
                lastSensorIds = new ArrayList<>(sensorIds);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to update sensor list.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateDataContinuous() {
        try {
            String selectedSensor = (String) sensorComboBox.getSelectedItem();
            tableModel.setRowCount(0);
            if (selectedSensor == null || selectedSensor.equals("No Sensors Available")) {
                tableModel.addRow(new Object[]{"No sensor selected", "", "", ""});
                return;
            }

            SensorData data = cluster.getSensorData(selectedSensor);
            String status = cluster.getSensorStatus(selectedSensor);
            String firmware = cluster.getFirmwareVersion(selectedSensor);
            if (data != null) {
                tableModel.addRow(new Object[]{"", "", status, firmware});
                tableModel.addRow(new Object[]{"Environment Data", "", "", ""});
                for (Map.Entry<String, Double> entry : data.getEnvironmentData().entrySet()) {
                    tableModel.addRow(new Object[]{entry.getKey(), String.format("%.2f", entry.getValue()), "", ""});
                }
                tableModel.addRow(new Object[]{"Crop Data", "", "", ""});
                for (Map.Entry<String, Double> entry : data.getCropData().entrySet()) {
                    tableModel.addRow(new Object[]{entry.getKey(), String.format("%.2f", entry.getValue()), "", ""});
                }
            } else {
                tableModel.addRow(new Object[]{"No data available for sensor " + selectedSensor, "", status, firmware});
            }
        } catch (RemoteException e) {
            tableModel.setRowCount(0);
            tableModel.addRow(new Object[]{"Failed to retrieve sensor data", "", "", ""});
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to retrieve sensor data.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateDataOnSelection() {
        try {
            String selectedSensor = (String) sensorComboBox.getSelectedItem();
            tableModel.setRowCount(0);
            if (selectedSensor == null || selectedSensor.equals("No Sensors Available")) {
                tableModel.addRow(new Object[]{"No sensor selected", "", "", ""});
                return;
            }

            SensorData data = cluster.getSensorData(selectedSensor);
            String status = cluster.getSensorStatus(selectedSensor);
            String firmware = cluster.getFirmwareVersion(selectedSensor);
            if (data != null) {
                tableModel.addRow(new Object[]{"", "", status, firmware});
                tableModel.addRow(new Object[]{"Environment Data", "", "", ""});
                for (Map.Entry<String, Double> entry : data.getEnvironmentData().entrySet()) {
                    tableModel.addRow(new Object[]{entry.getKey(), String.format("%.2f", entry.getValue()), "", ""});
                }
                tableModel.addRow(new Object[]{"Crop Data", "", "", ""});
                for (Map.Entry<String, Double> entry : data.getCropData().entrySet()) {
                    tableModel.addRow(new Object[]{entry.getKey(), String.format("%.2f", entry.getValue()), "", ""});
                }
            } else {
                tableModel.addRow(new Object[]{"No data available for sensor " + selectedSensor, "", status, firmware});
            }
        } catch (RemoteException e) {
            tableModel.setRowCount(0);
            tableModel.addRow(new Object[]{"Failed to retrieve sensor data", "", "", ""});
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to retrieve sensor data.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}