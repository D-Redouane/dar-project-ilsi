package sensor;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface SensorInterface extends Remote {
    SensorData sendData() throws RemoteException;
    void updateFirmwareVersion(String version) throws RemoteException;
    String getStatus() throws RemoteException;
    String getFirmwareVersion() throws RemoteException;
    String getRmiUrl() throws RemoteException;
    void activate() throws RemoteException;
    void deactivate() throws RemoteException;
}