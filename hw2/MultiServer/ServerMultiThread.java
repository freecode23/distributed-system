
/**
 * Author: Sherly Hartono
 * Email: s.hartono@northeastern.edu
 * HW2 Qn 1
 */
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ServerMultiThread extends Thread {
   
    private int port;
    private Socket socket;
    private static int threadNum = 0;

    /**
     * Constructor
     * @param port the port number
     */
    public ServerMultiThread(Socket socket, int port) {
        
        // init port and socket 
        this.port = port;
        this.socket = socket;
        // ServerMultiThread.threadNum = ServerMultiThread.threadNum + 1;
        // System.out.println("create new server thread=" + ServerMultiThread.threadNum);
    }
    
    /**
     * Override the run method
     */
    @Override
    public void run() {
        
        try {
            // 2. Read data from the client
            Scanner scannerSocketInput = new Scanner(socket.getInputStream());
            String clientInput = scannerSocketInput.nextLine();
            System.out.println("Running thread name: " + Thread.currentThread().getName());

            // 3. Reverse the characters
            StringBuilder reversedStr = new StringBuilder(clientInput).reverse();

            // 4. Reverse capitalize
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
            // System.out.println("Sent to client: " + outputStr);

            // 6. Close the socket and scanner
            socket.close();
            scannerSocketInput.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        int port = Integer.parseInt(args[0]);
        try (
            
            // 0. Create a ServerSocket and bind it to the specified port
            ServerSocket server = new ServerSocket(port)) {
            System.out.println("Server started and listening to port " + port);

            while (true) {
                
                // - will wait here until client connect
                Socket socket = server.accept();
                System.out.println("\nNew client connect>>>>");

                // - create new server with its own thread
                ServerMultiThread newServer = new ServerMultiThread(socket, port);
                newServer.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
