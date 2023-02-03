import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TcpClient extends ClientDefault {
    private String host;
    private int port;
    private int threadN;
    private ExecutorService executor;

    /**
     * Constructor
     * @param host host ip
     * @param port the same port as server
     */
    public TcpClient(String host, int port, int threadN) {
        this.host = host;
        this.port = port;
        
        this.threadN = threadN;

        // init executor object that creates n threads
        executor = Executors.newFixedThreadPool(threadN);
    }

    @Override
    public void startClient() {
        System.out.println("Start client");

        // 0. Run n thread
        for (int i = 0; i < this.threadN; i++) {
            final int requestNum = i;
            int port = this.port;

            // 1. a single thread that runs all command
            this.executor.execute(new Runnable() {
                public void run() {

                    // - read from textfile
                    ArrayList<String> commands = parseTextFile();
            
                    // - for each command:
                    for(String command: commands) {
                        
                        try {
                            // - init socket
                            // TODO: change host name
                            Socket socket = new Socket("localhost", port);

                            // - OUT: create output obj to send message to server
                            PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);

                            // - validate command
                            try {
                                validateCommand(command);

                            } catch (IllegalArgumentException e) {
                                printWriter.println("Received unsolicited response=" + e.getMessage());
                                continue;
                            }
                            
                            // - IN: init input object to get input from server
                            Scanner inputScanner = new Scanner(socket.getInputStream());
                            
                            // - send message to server
                            printWriter.println(command);
                            String response = "";

                            // - get response
                            socket.setSoTimeout(3000);

                            // - if response empty after 2 seconds
                            if (!inputScanner.hasNextLine()) {

                                // print timeout
                                System.out.println("timeout>>>>>>>>");
                                String currentTimestamp = getDate();
                                System.out.println(String.format(
                                    "[%s] Timeout occurred for request= %d",
                                    currentTimestamp, requestNum
                                    ));

                                // skip this line
                                continue;
                            }
                            response = inputScanner.nextLine();

                  
                            
                            // - print response to terminal
                            String currentTimestamp = getDate();
                            System.out.println(String.format(
                                "[%s] response= %s", currentTimestamp, response
                                ));

                            socket.close();

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
