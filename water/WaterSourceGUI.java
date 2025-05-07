package water;

import javax.swing.*;
import java.awt.*;

public class WaterSourceGUI extends JFrame {
    private WaterSource waterSource;
    private JProgressBar waterLevelBar;

    public WaterSourceGUI(WaterSource waterSource) {
        this.waterSource = waterSource;
        setTitle("Water Source");
        setSize(400, 100);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        waterLevelBar = new JProgressBar(0, 100);
        waterLevelBar.setValue(100);
        waterLevelBar.setStringPainted(true);

        setLayout(new BorderLayout());
        add(waterLevelBar, BorderLayout.CENTER);

        // Start a thread to update the water level bar in real-time
        new Thread(() -> {
            try {
                while (true) {
                    double waterLevel = waterSource.getStatus();
                    waterLevelBar.setValue((int) waterLevel);
                    updateBarColor((int) waterLevel);
                    Thread.sleep(1000);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        setVisible(true);
    }

    private void updateBarColor(int waterLevel) {
        if (waterLevel < 40) {
            waterLevelBar.setForeground(Color.RED);
        } else {
            waterLevelBar.setForeground(Color.GREEN);
        }
    }
}
