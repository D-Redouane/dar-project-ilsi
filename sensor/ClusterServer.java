package sensor;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class ClusterServer {
    public static void main(String[] args) {
        try {
            LocateRegistry.createRegistry(2095);
            Cluster cluster = new Cluster();
            Naming.rebind("rmi://localhost:2095/Cluster", cluster);
            System.out.println("Cluster is running and bound to RMI registry on port 2095.");
        } catch (RemoteException | java.net.MalformedURLException e) {
            e.printStackTrace();
        }
    }
}