package ecommerce.rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RestaurantDefault extends UnicastRemoteObject implements Restaurant{

    protected RestaurantDefault() throws RemoteException {
        super();
    }

    @Override
    public String orderFood(String food) throws RemoteException {
        return "Food ordered!";
    }
}