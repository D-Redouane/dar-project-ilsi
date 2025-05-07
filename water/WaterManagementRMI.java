package water;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import javax.swing.SwingUtilities;

public class WaterManagementRMI {
    public static void main(String[] args) {
        try {
            // Create RMI registry
            LocateRegistry.createRegistry(101);

            // Create cluster and water sources
            WaterSource waterSource = new WaterSource();
            Cluster cluster = new Cluster(waterSource);

            // Bind the cluster and water source to RMI registry
            Naming.rebind("rmi://localhost:2100/WaterSource", waterSource);
            Naming.rebind("rmi://localhost:2100/Cluster", cluster);

            System.out.println("RMI setup complete. Cluster and water source are bound.");

            // Start the GUIs
            SwingUtilities.invokeLater(() -> {
                new ClusterGUI(cluster);
                new WaterSourceGUI(waterSource);
            });
        } catch (RemoteException | MalformedURLException e) {
            e.printStackTrace();
        }
    }
}
