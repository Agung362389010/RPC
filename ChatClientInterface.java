import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ChatClientInterface extends java.rmi.Remote {
    void receiveMessage(String message) throws RemoteException;
}

