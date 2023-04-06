package ecommerce.rmi;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Restaurant extends Remote{
    String orderFood(String food) throws RemoteException;
}
