package water;

import java.rmi.Naming;
import java.rmi.RemoteException;

public class WaterSourceClient {
    public static void main(String[] args) {
        try {
            // Locate the cluster using the RMI URL
            ClusterInterface cluster = (ClusterInterface) Naming.lookup("rmi://localhost:2100/Cluster");

            // Interact with the cluster
            // Open the water source
            cluster.openWaterSource();

            // Wait for a while (e.g., 5 seconds) to let the water flow
            Thread.sleep(5000);

            // Check the water source status
            double status = cluster.checkWaterSourceStatus();
            System.out.println("Water source status: " + status + "%");

            // Close the water source
            cluster.closeWaterSource();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
