package sensor;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.UUID;

public class StartSensorGUI {
    public static void main(String[] args) {
        try {
            // Start RMI registry on PC1 if not already running
            try {
                LocateRegistry.createRegistry(2095);
                System.out.println("RMI registries started on 192.168.163.186:2095.");
            } catch (RemoteException e) {
                System.out.println("RMI registry already running on 192.168.163.186:2095.");
            }

            // Create and bind the sensor on PC1
            System.setProperty("java.rmi.server.hostname", "192.168.163.186");
            String sensorUrl = "rmi://192.168.163.186:2095/Sensor_" + UUID.randomUUID();
            Sensor sensor = new Sensor(sensorUrl);
            Naming.rebind(sensorUrl, sensor);

            // Connect to the cluster on PC2
            ClusterInterface cluster = (ClusterInterface) Naming.lookup("rmi://192.168.163.156:2095/Cluster");
            String sensorId = cluster.addSensor(sensor);
            sensor.setCluster(cluster, sensorId);

            // Create and show the sensor GUI
            SensorGUI sensorGUI = new SensorGUI(sensor);
            sensorGUI.setVisible(true);

            System.out.println("Sensor GUI is running on 192.168.163.186, connected to cluster at 192.168.163.156.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}