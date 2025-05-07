package sensor;

import java.rmi.Naming;
import java.util.UUID;

public class StartSensorGUI {
    public static void main(String[] args) {
        try {
            // Create and bind the sens
            String sensorUrl = "rmi://localhost:2095/Sensor_" + UUID.randomUUID();
            Sensor sensor = new Sensor(sensorUrl);
            Naming.rebind(sensorUrl, sensor);

            // Create and show the sensor GUI
            SensorGUI sensorGUI = new SensorGUI(sensor);
            sensorGUI.setVisible(true);

            System.out.println("Sensor GUI is running.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}