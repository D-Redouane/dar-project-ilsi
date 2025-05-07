package sensor;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

public interface ClusterInterface extends Remote {
    String addSensor(SensorInterface sensor) throws RemoteException;
    void removeSensor(String sensorId) throws RemoteException;
    List<String> getSensorIds() throws RemoteException;
    SensorData getSensorData(String sensorId) throws RemoteException;
    Map<String, SensorData> getAllSensorData() throws RemoteException;
    void updateFirmware(String sensorId, String version) throws RemoteException;
    String getSensorStatus(String sensorId) throws RemoteException;
    Map<String, String> getAllSensorStatuses() throws RemoteException;
    String getFirmwareVersion(String sensorId) throws RemoteException;
    boolean isSensorConnected(String sensorId) throws RemoteException;
}