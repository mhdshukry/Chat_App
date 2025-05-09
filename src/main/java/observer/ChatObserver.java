package observer;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ChatObserver extends Remote {
    void update(String message) throws RemoteException;
}
