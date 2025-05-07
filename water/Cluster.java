package water;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Cluster extends UnicastRemoteObject implements ClusterInterface {
    private WaterSourceInterface waterSource;

    public Cluster(WaterSourceInterface waterSource) throws RemoteException {
        super();
        this.waterSource = waterSource;
    }

    @Override
    public void openWaterSource() throws RemoteException {
        waterSource.open();
    }

    @Override
    public void closeWaterSource() throws RemoteException {
        waterSource.close();
    }

    @Override
    public double checkWaterSourceStatus() throws RemoteException {
        return waterSource.getStatus();
    }
}
