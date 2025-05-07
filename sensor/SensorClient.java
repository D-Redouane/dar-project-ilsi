package sensor;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.UUID;

public class SensorClient {
    public static void main(String[] args) {
        try {
            // Connect to the cluster
            ClusterInterface cluster = (ClusterInterface) Naming.lookup("rmi://localhost:2095/Cluster");

            // Create and bind a new sensor
            String sensorUrl = "rmi://localhost:2095/Sensor_" + UUID.randomUUID();
            Sensor sensor = new Sensor(sensorUrl);
            Naming.rebind(sensorUrl, sensor);

            // Add sensor to cluster
            cluster.addSensor(sensor);

            System.out.println("Sensor is running and connected to the cluster.");
        } catch (RemoteException | java.net.MalformedURLException | java.rmi.NotBoundException e) {
            e.printStackTrace();
        }
    }
}