package sensor;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Cluster extends UnicastRemoteObject implements ClusterInterface {
    private static final long serialVersionUID = 1L;
    private final Map<String, SensorInterface> sensors;
    private final List<String> sensorIds;

    public Cluster() throws RemoteException {
        super();
        sensors = new HashMap<>();
        sensorIds = new ArrayList<>();
    }

    @Override
    public String addSensor(SensorInterface sensor) throws RemoteException {
        String sensorId = UUID.randomUUID().toString();
        sensors.put(sensorId, sensor);
        sensorIds.add(sensorId);
        return sensorId;
    }

    @Override
    public void removeSensor(String sensorId) throws RemoteException {
        sensors.remove(sensorId);
        sensorIds.remove(sensorId);
    }

    @Override
    public List<String> getSensorIds() throws RemoteException {
        return new ArrayList<>(sensorIds);
    }

    @Override
    public SensorData getSensorData(String sensorId) throws RemoteException {
        SensorInterface sensor = sensors.get(sensorId);
        if (sensor != null) {
            return sensor.sendData();
        }
        return null;
    }

    @Override
    public Map<String, SensorData> getAllSensorData() throws RemoteException {
        Map<String, SensorData> allData = new HashMap<>();
        for (String sensorId : sensorIds) {
            SensorData data = getSensorData(sensorId);
            if (data != null) {
                allData.put(sensorId, data);
            }
        }
        return allData;
    }

    @Override
    public void updateFirmware(String sensorId, String version) throws RemoteException {
        SensorInterface sensor = sensors.get(sensorId);
        if (sensor != null) {
            sensor.updateFirmwareVersion(version);
        }
    }

    @Override
    public String getSensorStatus(String sensorId) throws RemoteException {
        SensorInterface sensor = sensors.get(sensorId);
        return sensor != null ? sensor.getStatus() : "Disconnected";
    }

    @Override
    public Map<String, String> getAllSensorStatuses() throws RemoteException {
        Map<String, String> statuses = new HashMap<>();
        for (String sensorId : sensorIds) {
            statuses.put(sensorId, getSensorStatus(sensorId));
        }
        return statuses;
    }

    @Override
    public String getFirmwareVersion(String sensorId) throws RemoteException {
        SensorInterface sensor = sensors.get(sensorId);
        return sensor != null ? sensor.getFirmwareVersion() : "N/A";
    }

    @Override
    public boolean isSensorConnected(String sensorId) throws RemoteException {
        return sensors.containsKey(sensorId);
    }
}