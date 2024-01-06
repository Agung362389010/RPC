import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;


public interface ChatServerInterface extends java.rmi.Remote {
    void registerClient(ChatClientInterface client) throws RemoteException;
    void broadcastMessage(String message) throws RemoteException;
}