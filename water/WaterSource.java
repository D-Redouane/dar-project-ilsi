package water;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class WaterSource extends UnicastRemoteObject implements WaterSourceInterface {
    private double waterLevel = 100.0;
    private boolean isOpen = false;

    public WaterSource() throws RemoteException {
        super();
    }

    @Override
    public void open() throws RemoteException {
        isOpen = true;
        // Start the process of reducing water level
        new Thread(() -> {
            try {
                while (isOpen && waterLevel > 0) {
                    waterLevel -= 1; // Decrease water level by 1% per second
                    Thread.sleep(1000); // Wait for 1 second
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void close() throws RemoteException {
        isOpen = false;
    }

    @Override
    public double getStatus() throws RemoteException {
        return waterLevel;
    }
}
