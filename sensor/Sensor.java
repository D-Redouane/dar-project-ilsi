package sensor;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Random;

public class Sensor extends UnicastRemoteObject implements SensorInterface {
    private static final long serialVersionUID = 1L;
    private SensorData data;
    private String firmwareVersion;
    private boolean active;
    private ClusterInterface cluster;
    private String sensorId;
    private String rmiUrl;

    public Sensor(String rmiUrl) throws RemoteException {
        super();
        this.rmiUrl = rmiUrl;
        data = new SensorData();
        firmwareVersion = "1.0";
        active = true;
        startDataUpdateThread();
    }

    public void setCluster(ClusterInterface cluster, String sensorId) throws RemoteException {
        this.cluster = cluster;
        this.sensorId = sensorId;
    }

    private void startDataUpdateThread() {
        new Thread(() -> {
            try {
                Random random = new Random();
                while (active) {
                    synchronized (data) {
                        data.addEnvironmentData("Temperature", random.nextDouble() * 40);
                        data.addEnvironmentData("Humidity", random.nextDouble() * 100);
                        data.addEnvironmentData("Soil Moisture", random.nextDouble() * 100);
                        data.addEnvironmentData("Light Level", random.nextDouble() * 100);
                        data.addEnvironmentData("CO2 Level", random.nextDouble() * 100);
                        data.addCropData("Crop Growth", random.nextDouble() * 100);
                        data.addCropData("Crop Health", random.nextDouble() * 100);
                    }
                    Thread.sleep(2000);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public SensorData sendData() throws RemoteException {
        synchronized (data) {
            return data;
        }
    }

    @Override
    public void updateFirmwareVersion(String version) throws RemoteException {
        firmwareVersion = version;
    }

    @Override
    public String getStatus() throws RemoteException {
        return active ? "Active" : "Disconnected";
    }

    @Override
    public String getFirmwareVersion() throws RemoteException {
        return firmwareVersion;
    }

    @Override
    public String getRmiUrl() throws RemoteException {
        return rmiUrl;
    }

    @Override
    public void activate() throws RemoteException {
        if (!active) {
            active = true;
            startDataUpdateThread();
            if (cluster != null) {
                cluster.getSensorStatus(sensorId);
            }
        }
    }

    @Override
    public void deactivate() throws RemoteException {
        if (active) {
            active = false;
            if (cluster != null) {
                cluster.getSensorStatus(sensorId);
            }
        }
    }
}