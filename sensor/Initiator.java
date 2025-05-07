package sensor;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.UUID;

public class Initiator {
    public static void main(String[] args) {
        try {
            // Start the RMI registry on port 2095
            java.rmi.registry.LocateRegistry.createRegistry(2095);

            // Start the cluster
            Cluster cluster = new Cluster();
            Naming.rebind("rmi://localhost:2095/Cluster", cluster);

            // Start the GUI for the cluster
            ClusterGUI clusterGUI = new ClusterGUI(cluster);
            clusterGUI.setVisible(true);

            System.out.println("Cluster is running and bound to RMI registry.");

            // Create and start a sensor
            //String sensorUrl = "rmi://localhost:2095/Sensor_" + UUID.randomUUID();
            //Sensor sensor = new Sensor(sensorUrl);
            //Naming.rebind(sensorUrl, sensor);

            // Start the GUI for the sensor
            //SensorGUI sensorGUI = new SensorGUI(sensor);
            //sensorGUI.setVisible(true);

            System.out.println("Sensor is running and bound to RMI registry.");

        } catch (RemoteException | java.net.MalformedURLException e) {
            e.printStackTrace();
        }
    }
}