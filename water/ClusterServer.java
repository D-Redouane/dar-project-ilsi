package water;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class ClusterServer {
    public static void main(String[] args) {
        try {
            LocateRegistry.createRegistry(2100);
            WaterSource source = new WaterSource();
            Naming.rebind("rmi://localhost:2100/WaterSource", source);
            Cluster cluster = new Cluster(source);
            Naming.rebind("rmi://localhost:2100/Cluster", cluster);
            System.out.println("Cluster and water source are bound to the RMI registry.");
        } catch (RemoteException | MalformedURLException e) {
            e.printStackTrace();
        }
    }
}
