
/**
 * Author: Sherly Hartono
 * Email: hartono.s@northeastern.edu
 */
import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.HashMap;
import java.util.Map;

public class TcpServer implements Server {

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
        System.out.println("Server started and listening to port " + this.port);
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
                    System.out.println("Thread name: " + Thread.currentThread().getName());

                    // 0. Wait for a client to connect
                    Socket socket = serverSocket.accept();

                    // 1. Read data from the client
                    Scanner scannerSocketInput = new Scanner(socket.getInputStream());
                    String clientInput = scannerSocketInput.nextLine();
                    
                    // 2. do put, get, delete based on the first command
                    String response = commandObj.go(clientInput, keyVal);
                    
          
                    // 3. Send back to client
                    // - create printwriter object
                    PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
          
                    // - Send back response to client using printWriter
                    // System.out.println("response="+ response);
                    printWriter.println("response="+response);
        

                    // - close socket
                    // socket.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
