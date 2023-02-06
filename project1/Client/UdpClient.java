import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.HashMap;
import java.util.Map;

public class UdpClient extends ClientDefault {
    private String host;
    private int port;
    private int threadN;
    private ExecutorService executor;

    /**
     * Constructor
     * 
     * @param host host ip
     * @param port the same port as server
     */
    public UdpClient(String host, int port, int threadN) {
        this.host = host;
        this.port = port;

        this.threadN = threadN;

        // init executor object that creates n threads
        executor = Executors.newFixedThreadPool(threadN);
    }

    @Override
    public void startClient() {
        System.out.println("Start Udp client");

        // 0. Run n thread
        for (int i = 0; i < this.threadN; i++) {
            final int requestNum = i;
            int port = this.port;
            String host = this.host;

            // 1. a single thread that runs all command
            this.executor.execute(new Runnable() {
                public void run() {

                    // - read from textfile
                    ArrayList<String> commands = parseTextFile();

                    // - for each command:
                    for (String command : commands) {

                        try {
                            // 1. UDP: init socket, ip addres, and data to send
                            DatagramSocket clientSocket = new DatagramSocket();
                            InetAddress IPAddress = InetAddress.getByName(host);
                            

                            // 2. validate command from textfile
                            if (!validateCommand(command)) {
                                continue;
                            }

                            // 3. UDP: out
                            // create request id and concat with command
                            String reqId = generateUniqueID();
                            String reqIdCommand = reqId + " " + command;

                            // - create bytes array to store the data to send
                            byte[] sendData = new byte[1024];
                            sendData = reqIdCommand.getBytes();

                            // - create packet from data, ip, and port
                            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
                            
                            // - send
                            clientSocket.send(sendPacket);

                            // 4. UDP in
                            // - init data and packet to get input from server
                            byte[] responseData = new byte[1024];
                            DatagramPacket responsePacket = new DatagramPacket(responseData, responseData.length);
                            
                            // - receive
                            clientSocket.receive(responsePacket);

                            // 5. TCP set timeout
                            clientSocket.setSoTimeout(3000);

                            // case A: timeout after 2 seconds
                            if (responsePacket.getLength() == 0) {

                                // unsresponsive server
                                String currentTimestamp = getDate();
                                System.out.println(String.format(
                                        "[%s] Timeout occurred for request= %s",
                                        currentTimestamp, reqId));

                                // skip this response
                                continue;
                            }
                            // 6. not timeout, get response and convert to hash map
                            String resString = new String(responsePacket.getData(), 0, responsePacket.getLength());
                            Map<String, String> resObj = convertJsonToMap(resString);

                            // case B: responseId doesn't match
                            if (!validateResponseId(reqId, resObj.get("reqId"))) {
                                // skip this response
                                continue;
                            }

                            // 7. print response to terminal
                            printResponse(reqId, resString);

                            // 8. end of client request
                            clientSocket.close();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                };
            });
            executor.shutdown();
        }
    }

}
