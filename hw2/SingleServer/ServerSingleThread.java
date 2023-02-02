/**
 * Author: Sherly Hartono
 * Email: s.hartono@northeastern.edu
 */
import java.io.*;
import java.net.*;
import java.util.Scanner;

import project1.ServerSingleThread;

public class ServerSingleThread extends Thread {
   
    private int port;
    private ServerSocket serverSocket;

    /**
     * Constructor
     * @param port the port number
     */
    public ServerSingleThread(int port) {

        // init port
        this.port = port;
        try {
            this.serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        } 
    }
    
    /**
     * Override the run method
     */
    @Override
    public void run() {
        while (true) {

            try {
                System.out.println("Thread name: " + Thread.currentThread().getName());
                Socket socket = this.serverSocket.accept();
                
                // 1. Read data from the client
                Scanner scannerSocketInput = new Scanner(socket.getInputStream());
                String clientInput = scannerSocketInput.nextLine();
                
                // 2. Reverse the characters
                StringBuilder reversedStr = new StringBuilder(clientInput).reverse();
    
                // 3. Reverse capitalize
                for (int i = 0; i < reversedStr.length(); i++) {
                    char c = reversedStr.charAt(i);
                    if (Character.isLowerCase(c)) {
                        reversedStr.setCharAt(i, Character.toUpperCase(c));
                    } else {
                        reversedStr.setCharAt(i, Character.toLowerCase(c));
                    }
                }
                String outputStr = reversedStr.toString();
    
                // 5. Send back to client
                // - create printwriter object
                PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
    
                // - Send the modified string back to the client using printWriter
                printWriter.println(outputStr);
    
                // // - close socket
                // socket.close();
    
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {

        int port = Integer.parseInt(args[0]);
        System.out.println("Server started and listening to port " + port);  
        ServerSingleThread newServerThread = new ServerSingleThread(port);
        newServerThread.start();

    
    }
}
