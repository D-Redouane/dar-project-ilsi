package water;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClusterInterface extends Remote {
    void openWaterSource() throws RemoteException;
    void closeWaterSource() throws RemoteException;
    double checkWaterSourceStatus() throws RemoteException;
}
