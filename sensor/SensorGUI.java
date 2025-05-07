package sensor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.UUID;

public class SensorGUI extends JFrame {
    private Sensor sensor;
    private ClusterInterface cluster;
    private String sensorId;
    private final JLabel statusLabel;
    private final JLabel firmwareVersionLabel;
    private final JTable dataTable;
    private final DefaultTableModel tableModel;
    private final JButton createSensorButton;
    private final JButton deleteSensorButton;
    private final JButton deactivateButton;
    private final JButton activateButton;

    public SensorGUI(Sensor sensor) throws RemoteException {
        this.sensor = sensor;
        setTitle("Sensor Management");
        setSize(800, 600);
        setMinimumSize(new Dimension(700, 500));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(245, 245, 245));

        // Connect to cluster
        try {
            cluster = (ClusterInterface) Naming.lookup("rmi://localhost:2095/Cluster");
            sensorId = cluster.addSensor(sensor);
            sensor.setCluster(cluster, sensorId);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to connect to cluster.", "Error", JOptionPane.ERROR_MESSAGE);
        }

        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(60, 141, 188));
        headerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        JLabel titleLabel = new JLabel("Sensor Management - ID: " + sensorId, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);

        // Control panel (placed in NORTH for guaranteed visibility)
        JPanel controlPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        controlPanel.setBackground(new Color(245, 245, 245));
        controlPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        controlPanel.setPreferredSize(new Dimension(0, 60));

        createSensorButton = new JButton("Create New Sensor");
        createSensorButton.setFont(new Font("Arial", Font.PLAIN, 14));
        createSensorButton.setBackground(new Color(92, 184, 92));
        createSensorButton.setForeground(Color.WHITE);
        createSensorButton.setToolTipText("Create a new sensor and open its GUI");
        createSensorButton.addActionListener(e -> createNewSensor());
        controlPanel.add(createSensorButton);

        deleteSensorButton = new JButton("Delete Sensor");
        deleteSensorButton.setFont(new Font("Arial", Font.PLAIN, 14));
        deleteSensorButton.setBackground(new Color(217, 83, 79));
        deleteSensorButton.setForeground(Color.WHITE);
        deleteSensorButton.setToolTipText("Delete this sensor and close its GUI");
        deleteSensorButton.addActionListener(e -> deleteSensor());
        controlPanel.add(deleteSensorButton);

        deactivateButton = new JButton("Deactivate Sensor");
        deactivateButton.setFont(new Font("Arial", Font.PLAIN, 14));
        deactivateButton.setBackground(new Color(240, 173, 78));
        deactivateButton.setForeground(Color.WHITE);
        deactivateButton.setToolTipText("Deactivate this sensor");
        deactivateButton.addActionListener(e -> deactivateSensor());
        controlPanel.add(deactivateButton);

        activateButton = new JButton("Activate Sensor");
        activateButton.setFont(new Font("Arial", Font.PLAIN, 14));
        activateButton.setBackground(new Color(91, 192, 222));
        activateButton.setForeground(Color.WHITE);
        activateButton.setToolTipText("Activate this sensor");
        activateButton.addActionListener(e -> activateSensor());
        activateButton.setEnabled(false);
        controlPanel.add(activateButton);

        add(controlPanel, BorderLayout.CENTER);

        // Status panel
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBackground(new Color(245, 245, 245));
        statusPanel.setBorder(BorderFactory.createTitledBorder("Sensor Status"));
        statusLabel = new JLabel("Status: Active");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        firmwareVersionLabel = new JLabel("Firmware Version: " + sensor.getFirmwareVersion());
        firmwareVersionLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        statusPanel.add(statusLabel);
        statusPanel.add(Box.createHorizontalStrut(20));
        statusPanel.add(firmwareVersionLabel);
        add(statusPanel, BorderLayout.WEST);

        // Data display
        tableModel = new DefaultTableModel(new Object[]{"Parameter", "Value"}, 0);
        dataTable = new JTable(tableModel);
        dataTable.setFont(new Font("Arial", Font.PLAIN, 12));
        dataTable.setRowHeight(25);
        dataTable.setGridColor(new Color(200, 200, 200));
        JScrollPane dataScrollPane = new JScrollPane(dataTable);
        dataScrollPane.setBorder(BorderFactory.createTitledBorder("Sensor Data"));
        add(dataScrollPane, BorderLayout.SOUTH);

        // Debug button visibility
        System.out.println("Control panel components: " + controlPanel.getComponentCount());
        for (Component comp : controlPanel.getComponents()) {
            System.out.println("Component: " + comp);
        }

        // Start a thread to update sensor data
        new Thread(() -> {
            try {
                while (true) {
                    updateData();
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
        }).start();

        setVisible(true);
    }

    private void createNewSensor() {
        try {
            String newSensorUrl = "rmi://localhost:2095/Sensor_" + UUID.randomUUID();
            Sensor newSensor = new Sensor(newSensorUrl);
            Naming.rebind(newSensorUrl, newSensor);
            new SensorGUI(newSensor);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to create new sensor.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSensor() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete sensor " + sensorId + "?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                cluster.removeSensor(sensorId);
                Naming.unbind(sensor.getRmiUrl());
                dispose();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to delete sensor.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deactivateSensor() {
        try {
            sensor.deactivate();
            statusLabel.setText("Status: Disconnected");
            deactivateButton.setEnabled(false);
            activateButton.setEnabled(true);
        } catch (RemoteException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to deactivate sensor.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void activateSensor() {
        try {
            sensor.activate();
            statusLabel.setText("Status: Active");
            deactivateButton.setEnabled(true);
            activateButton.setEnabled(false);
        } catch (RemoteException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to activate sensor.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateData() {
        try {
            SensorData data = sensor.sendData();
            statusLabel.setText("Status: " + sensor.getStatus());
            firmwareVersionLabel.setText("Firmware Version: " + sensor.getFirmwareVersion());
            tableModel.setRowCount(0);
            if (data != null) {
                tableModel.addRow(new Object[]{"", ""});
                tableModel.addRow(new Object[]{"Environment Data", ""});
                for (Map.Entry<String, Double> entry : data.getEnvironmentData().entrySet()) {
                    tableModel.addRow(new Object[]{entry.getKey(), String.format("%.2f", entry.getValue())});
                }
                tableModel.addRow(new Object[]{"", ""});
                tableModel.addRow(new Object[]{"Crop Data", ""});
                for (Map.Entry<String, Double> entry : data.getCropData().entrySet()) {
                    tableModel.addRow(new Object[]{entry.getKey(), String.format("%.2f", entry.getValue())});
                }
            } else {
                tableModel.addRow(new Object[]{"No data available for sensor " + sensorId, ""});
            }
        } catch (RemoteException e) {
            tableModel.setRowCount(0);
            tableModel.addRow(new Object[]{"Failed to retrieve sensor data for " + sensorId, ""});
            e.printStackTrace();
        }
    }
}