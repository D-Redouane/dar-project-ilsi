package sensor;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.UUID;

public class SensorClient {
    public static void main(String[] args) {
        try {
            // Connect to the cluster on PC2
            ClusterInterface cluster = (ClusterInterface) Naming.lookup("rmi://192.168.163.156:2095/Cluster");

            // Create and bind a new sensor on PC1
            System.setProperty("java.rmi.server.hostname", "192.168.163.186");
            String sensorUrl = "rmi://192.168.163.186:2095/Sensor_" + UUID.randomUUID();
            Sensor sensor = new Sensor(sensorUrl);
            Naming.rebind(sensorUrl, sensor);

            // Add sensor to cluster
            String sensorId = cluster.addSensor(sensor);
            sensor.setCluster(cluster, sensorId);

            System.out.println("Sensor is running and connected to the cluster at 192.168.163.156:2095.");
        } catch (RemoteException | java.net.MalformedURLException | java.rmi.NotBoundException e) {
            e.printStackTrace();
        }
    }
}