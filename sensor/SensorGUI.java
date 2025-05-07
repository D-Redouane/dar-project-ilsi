package sensor;

import javax.swing.*;
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
    private final JTextArea dataTextArea;
    private final JButton createSensorButton;
    private final JButton deleteSensorButton;
    private final JButton deactivateButton;
    private final JButton activateButton;

    public SensorGUI(Sensor sensor) throws RemoteException {
        this.sensor = sensor;
        setTitle("Sensor Management");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Connect to cluster
        try {
            cluster = (ClusterInterface) Naming.lookup("rmi://localhost:2095/Cluster");
            sensorId = cluster.addSensor(sensor);
            sensor.setCluster(cluster, sensorId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Status panel
        JPanel statusPanel = new JPanel(new FlowLayout());
        statusLabel = new JLabel("Status: Active");
        firmwareVersionLabel = new JLabel("Firmware Version: " + sensor.getFirmwareVersion());
        statusPanel.add(statusLabel);
        statusPanel.add(firmwareVersionLabel);
        add(statusPanel, BorderLayout.NORTH);

        // Data display
        dataTextArea = new JTextArea(20, 40);
        dataTextArea.setEditable(false);
        dataTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane dataScrollPane = new JScrollPane(dataTextArea);
        add(dataScrollPane, BorderLayout.CENTER);

        // Control panel
        JPanel controlPanel = new JPanel(new GridLayout(4, 1));
        createSensorButton = new JButton("Create New Sensor");
        createSensorButton.addActionListener(e -> createNewSensor());
        controlPanel.add(createSensorButton);

        deleteSensorButton = new JButton("Delete Sensor");
        deleteSensorButton.addActionListener(e -> deleteSensor());
        controlPanel.add(deleteSensorButton);

        deactivateButton = new JButton("Deactivate Sensor");
        deactivateButton.addActionListener(e -> deactivateSensor());
        controlPanel.add(deactivateButton);

        activateButton = new JButton("Activate Sensor");
        activateButton.addActionListener(e -> activateSensor());
        activateButton.setEnabled(false);
        controlPanel.add(activateButton);
        add(controlPanel, BorderLayout.EAST);

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
        }
    }

    private void deleteSensor() {
        try {
            // Remove from cluster
            cluster.removeSensor(sensorId);
            // Unbind from RMI
            Naming.unbind(sensor.getRmiUrl());
            // Close GUI
            dispose();
        } catch (Exception e) {
            e.printStackTrace();
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
        }
    }

    private void updateData() {
        try {
            SensorData data = sensor.sendData();
            statusLabel.setText("Status: " + sensor.getStatus());
            firmwareVersionLabel.setText("Firmware Version: " + sensor.getFirmwareVersion());
            if (data != null) {
                dataTextArea.setText("Sensor Data for " + sensorId + ":\n");
                dataTextArea.append("\nEnvironment Data:\n");
                for (Map.Entry<String, Double> entry : data.getEnvironmentData().entrySet()) {
                    dataTextArea.append(String.format("%-15s: %.2f\n", entry.getKey(), entry.getValue()));
                }
                dataTextArea.append("\nCrop Data:\n");
                for (Map.Entry<String, Double> entry : data.getCropData().entrySet()) {
                    dataTextArea.append(String.format("%-15s: %.2f\n", entry.getKey(), entry.getValue()));
                }
            } else {
                dataTextArea.setText("No data available for sensor " + sensorId);
            }
        } catch (RemoteException e) {
            dataTextArea.setText("Failed to retrieve sensor data for " + sensorId);
            e.printStackTrace();
        }
    }
}