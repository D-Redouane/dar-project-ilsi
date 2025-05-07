package sensor;

import javax.swing.*;
import java.awt.*;
import java.rmi.RemoteException;
import java.util.Map;

public class ClusterGUI extends JFrame {
    private final ClusterInterface cluster;
    private final JTextArea dataTextArea;
    private final JComboBox<String> sensorComboBox;
    private final JLabel statusLabel;

    public ClusterGUI(ClusterInterface cluster) throws RemoteException {
        this.cluster = cluster;
        setTitle("Cluster Management");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Sensor selection panel
        JPanel sensorPanel = new JPanel(new FlowLayout());
        sensorPanel.add(new JLabel("Select Sensor:"));
        sensorComboBox = new JComboBox<>();
        sensorComboBox.addActionListener(e -> updateData());
        sensorPanel.add(sensorComboBox);
        add(sensorPanel, BorderLayout.NORTH);

        // Status panel
        JPanel statusPanel = new JPanel(new FlowLayout());
        statusLabel = new JLabel("Cluster Status: Running");
        statusPanel.add(statusLabel);
        add(statusPanel, BorderLayout.CENTER);

        // Data display
        dataTextArea = new JTextArea(20, 40);
        dataTextArea.setEditable(false);
        dataTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane dataScrollPane = new JScrollPane(dataTextArea);
        add(dataScrollPane, BorderLayout.SOUTH);

        // Start a thread to update sensor list and data
        new Thread(() -> {
            try {
                while (true) {
                    updateSensorList();
                    updateData();
                    Thread.sleep(1000); // Increased frequency for faster updates
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
            java.util.List<String> sensorIds = cluster.getSensorIds();
            DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
            model.addElement("All Sensors");
            for (String id : sensorIds) {
                model.addElement(id);
            }
            sensorComboBox.setModel(model);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void updateData() {
        try {
            String selectedSensor = (String) sensorComboBox.getSelectedItem();
            if (selectedSensor == null) return;

            if (selectedSensor.equals("All Sensors")) {
                dataTextArea.setText("All Sensors Data:\n");
                Map<String, SensorData> allData = cluster.getAllSensorData();
                Map<String, String> statuses = cluster.getAllSensorStatuses();
                for (String sensorId : allData.keySet()) {
                    dataTextArea.append("\nSensor " + sensorId + " (Status: " + statuses.get(sensorId) + "):\n");
                    SensorData data = allData.get(sensorId);
                    appendSensorData(data);
                }
            } else {
                SensorData data = cluster.getSensorData(selectedSensor);
                String status = cluster.getSensorStatus(selectedSensor);
                String firmware = cluster.getFirmwareVersion(selectedSensor);
                dataTextArea.setText("Sensor " + selectedSensor + " Data (Status: " + status + ", Firmware: " + firmware + "):\n");
                if (data != null) {
                    appendSensorData(data);
                } else {
                    dataTextArea.append("No data available.\n");
                }
            }
        } catch (RemoteException e) {
            dataTextArea.setText("Failed to retrieve sensor data.\n");
            e.printStackTrace();
        }
    }

    private void appendSensorData(SensorData data) {
        dataTextArea.append("\nEnvironment Data:\n");
        for (Map.Entry<String, Double> entry : data.getEnvironmentData().entrySet()) {
            dataTextArea.append(String.format("%-15s: %.2f\n", entry.getKey(), entry.getValue()));
        }
        dataTextArea.append("\nCrop Data:\n");
        for (Map.Entry<String, Double> entry : data.getCropData().entrySet()) {
            dataTextArea.append(String.format("%-15s: %.2f\n", entry.getKey(), entry.getValue()));
        }
    }
}