package sensor;


import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class StartClusterGUI {
    public static void main(String[] args) {
        try {
            // Create and show the cluster GUI
            LocateRegistry.createRegistry(2095);
            Cluster cluster = new Cluster();
            Naming.rebind("rmi://localhost:2095/Cluster", cluster);
            System.out.println("Cluster is running and bound to RMI registry on port 2095.");


            //Lookup the cluster on localhost (or server IP)
            //ClusterInterface cluster = (ClusterInterface) Naming.lookup("rmi://localhost:2095/Cluster");

            ClusterGUI clusterGUI = new ClusterGUI(cluster);
            clusterGUI.setVisible(true);
            
            System.out.println("Cluster GUI is running.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
