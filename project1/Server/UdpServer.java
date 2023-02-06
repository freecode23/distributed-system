
/**
 * Author: Sherly Hartono
 * Email: hartono.s@northeastern.edu
 */
import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.HashMap;
import java.util.Map;

public class UdpServer extends ServerDefault {

    private int port;
    private DatagramSocket serverSocket;
    private Command commandObj = new Command();
    private Map<Integer, Integer> keyVal = new HashMap<>();

    /**
     * 1. Constructor
     * that initialise datagram socket which creates Udp connection
     * 
     * @param port the port number
     */
    public UdpServer(int port) {

        // - init port
        this.port = port;

        // - create socket using this port
        try {
            this.serverSocket = new DatagramSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startThread() {
        System.out.println("Udp server started and listening to port " + this.port);
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

                    // 0. Udp In
                    byte[] receiveData = new byte[1024];
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    serverSocket.receive(receivePacket);

                    // 1. Read data from the client
                    String clientInput = new String(receivePacket.getData(), 0, receivePacket.getLength());

                    // - if there is no input skip this client
                    if (receivePacket.getLength() == 0) {
                        continue;
                    }

                    // 2. print client input
                    InetAddress clientAddress = receivePacket.getAddress();
                    String clientIpAddress = clientAddress.getHostAddress();
                    int clientPort = receivePacket.getPort();
                    printClientInput(clientInput, clientIpAddress, clientPort);

                    // Uncomment this to test timeout
                    // try {

                    // Thread.sleep(6000);
                    // } catch (InterruptedException e) {
                    // PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
                    // printWriter.println("sleep interrupted");
                    // }

                    // 3. validate client input
                    if (!validateClientInput(clientInput)) {
                        continue;
                    }

                    // 4. do put, get, delete based on the first command
                    String response = commandObj.go(clientInput, keyVal);

                    // 5. Send response back to client
                    // - get client ip address and port
                    InetAddress IPAddress = receivePacket.getAddress();
                    int port = receivePacket.getPort();

                    // - create packet
                    byte[] sendData = new byte[1024];
                    sendData = response.getBytes();
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);

                    // - Send back response to client
                    serverSocket.send(sendPacket);

                    // socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
