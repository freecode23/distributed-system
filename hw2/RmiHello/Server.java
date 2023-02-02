import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Server implements Hello {

    public Server() {
    }

    public String sayHello() {
        return "Hello, world!";
    }

    public static void main(String args[]) {

        try {
            // 0. init serverObj which is a Hello class
            Server serverObj = new Server();

            // 1. Exports the serverObj in the registry
            // - creates a serverStub for it
            // - this object will listen to port 0
            // - now the client refere to this stub and invoke sayHello()
            Hello serverStub = (Hello) UnicastRemoteObject.exportObject(serverObj, 0);

            // 2. create a registry
            Registry registry = LocateRegistry.getRegistry();

            // 3. bind the stub with the name hello
            registry.bind("Hello", serverStub);

            System.err.println("Server ready");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}