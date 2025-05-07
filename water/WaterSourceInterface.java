package water;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface WaterSourceInterface extends Remote {
    void open() throws RemoteException;
    void close() throws RemoteException;
    double getStatus() throws RemoteException;
}
