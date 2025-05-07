package water;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;

public class ClusterGUI extends JFrame {
    private ClusterInterface cluster;
    private JTextField statusField;
    private JButton openButton;
    private JButton closeButton;
    private JButton checkStatusButton;
    private JLabel notificationLabel;

    public ClusterGUI(ClusterInterface cluster) {
        this.cluster = cluster;
        setTitle("Cluster Control");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(5, 1));
        setResizable(false);

        openButton = new JButton("Open Water Source");
        closeButton = new JButton("Close Water Source");
        checkStatusButton = new JButton("Check Water Source Status");
        statusField = new JTextField();
        statusField.setEditable(false);
        notificationLabel = new JLabel("", SwingConstants.CENTER);
        notificationLabel.setForeground(Color.RED);

        openButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    cluster.openWaterSource();
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                }
            }
        });

        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    cluster.closeWaterSource();
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                }
            }
        });

        checkStatusButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    double status = cluster.checkWaterSourceStatus();
                    statusField.setText("Water Level: " + status + "%");
                    checkWaterLevel(status);
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                }
            }
        });

        add(openButton);
        add(closeButton);
        add(checkStatusButton);
        add(statusField);
        add(notificationLabel);
        setVisible(true);
    }

    private void checkWaterLevel(double status) {
        if (status < 40) {
            notificationLabel.setText("Reserve water! Water level is critically low.");
        } else {
            notificationLabel.setText("");
        }
    }
}
