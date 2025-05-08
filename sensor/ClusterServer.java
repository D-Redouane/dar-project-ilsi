package sensor;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class ClusterServer {
    public static void main(String[] args) {
        try {
            // Set the hostname for RMI to PC2's IP
            System.setProperty("java.rmi.server.hostname", "172.16.144.108");
            LocateRegistry.createRegistry(2095);
            Cluster cluster = new Cluster();
            Naming.rebind("rmi://172.16.144.108:2095/Cluster", cluster);
            System.out.println("Cluster is running and bound to RMI registry on 172.16.144.108:2095.");
        } catch (RemoteException | java.net.MalformedURLException e) {
            e.printStackTrace();
        }
    }
}