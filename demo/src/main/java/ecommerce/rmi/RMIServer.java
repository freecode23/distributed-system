package ecommerce.rmi;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class RMIServer {
    public static void main(String[] args) {
        try {
            LocateRegistry.createRegistry(1099);
            Restaurant restaurant = new RestaurantDefault();
            Naming.rebind("rmi://localhost/Restaurant", restaurant);
            System.out.println("RMI server is running");
        } catch (Exception e) {
            System.err.println("RMI server exception:");
            e.printStackTrace();
        }
    }
}