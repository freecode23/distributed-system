import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;

import project1.Server;

public class Server implements Sort {

    public Server() {
    }

    public int[] sort(int arr[]) {
        Arrays.sort(arr);
        return arr;
    }

    public static void main(String args[]) {

        try {
            // 0. init serverObj which is a Hello class
            Server serverObj = new Server();

            // 1. Exports the serverObj in the registry
            // - creates a serverStub for it
            // - this object will listen to port 0
            // - now the client refere to this stub and invoke sayHello()
            Sort serverStub = (Sort) UnicastRemoteObject.exportObject(serverObj, 0);

            // 2. create a registry
            Registry registry = LocateRegistry.getRegistry();

            // 3. bind the stub with the name hello
            registry.bind("Sort", serverStub);

            System.err.println("Server ready");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}