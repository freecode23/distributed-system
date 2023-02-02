import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Arrays;

public class Client {

    private Client() {
    }

    public static void main(String[] args) {

        String host = (args.length < 1) ? null : args[0];
        try {
            // 1. obtain a reference to a Registry object running on the local host
            Registry registry = LocateRegistry.getRegistry(host);

            // 2. Find the stub of name sort
            Sort stub = (Sort) registry.lookup("Sort");

            // 3. invoke the method
            int myArr[] = { 5, 4, 1, 2, 3, 6, 7, 9, 8, 10 };
            int sorted[] = stub.sort(myArr);

            // 4. print
            System.out.println(Arrays.toString(sorted));
            
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }

}