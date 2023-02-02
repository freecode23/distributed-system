import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Sort extends Remote {
    public int[] sort(int arr[]) throws RemoteException;
}