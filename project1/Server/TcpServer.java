
/**
 * Author: Sherly Hartono
 * Email: hartono.s@northeastern.edu
 */
import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.HashMap;
import java.util.Map;

public class TcpServer extends ServerDefault {

    private int port;
    private ServerSocket serverSocket;
    private Command commandObj = new Command();
    private Map<Integer, Integer> keyVal = new HashMap<>();

    /**
     * 1. Constructor
     * that initialise server socket which creates TCP connection
     * @param port the port number
     */
    public TcpServer(int port) {

        // - init port
        this.port = port;

        // - create socket using this port
        try {
            this.serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startThread() {
        System.out.println("Tcp server started and listening to port " + this.port);
        ServerThread newServerThread = new ServerThread();
        newServerThread.start();
    }

    private class ServerThread extends Thread {
        /**
         * Override the run method
         */
        @Override
        public void run() {
            while (true) {
                try {
                    // System.out.println("Thread name: " + Thread.currentThread().getName());

                    // 0. Wait for a client to connect
                    Socket clientSocket = serverSocket.accept();

                    // 1. Read data from the client
                    Scanner inputScanner = new Scanner(clientSocket.getInputStream());

                    // - if there is no input skip this client
                    if (!inputScanner.hasNextLine()) {
                        continue;
                    }

                    String clientInput = inputScanner.nextLine();

                    // 2. print client input
                    // get client ip and port
                    InetAddress clientAddress = clientSocket.getInetAddress();
                    String clientIpAddress = clientAddress.getHostAddress();
                    int clientPort = clientSocket.getPort();
                    printClientInput(clientInput, clientIpAddress, clientPort);
                    
                    
                    // Uncomment this to test timeout
                    // try {

                    //     Thread.sleep(6000);
                    // } catch (InterruptedException e) {
                    //     PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
                    //     printWriter.println("sleep interrupted");
                    // }

                    // 3. validate client input
                    if (!validateClientInput(clientInput)){
                        continue;
                    }
                   
                    // 4. do put, get, delete based on the first command
                    String response = commandObj.go(clientInput, keyVal);
                    
                    // 5. Send back to client
                    // - create printwriter object
                    PrintWriter printWriter = new PrintWriter(clientSocket.getOutputStream(), true);
          
                    // - Send back response to client using printWriter
                    printWriter.println(response);

                    // socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
