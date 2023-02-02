import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {

    private Client() {
    }

    public static void main(String[] args) {

        String host = (args.length < 1) ? null : args[0];
        try {
            // 1. obtain a reference to a Registry object running on the local host
            Registry registry = LocateRegistry.getRegistry(host);

            // 2. Find the stub of name hello
            Hello stub = (Hello) registry.lookup("Hello");

            // 3. invoke the method
            
            String response = stub.sayHello();

            // 4. print
            
            System.out.println("response: " + response);
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }

}