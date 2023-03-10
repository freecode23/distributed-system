import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.HashMap;
import java.util.Map;

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
        System.out.println("Start Tcp client");

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
                    for(String command: commands) {
                        
                        try {
                            // 1. TCP: init socket
                            Socket socket = new Socket(host, port);

                            // 2. validate command from textfile
                            if (!validateCommand(command)) {
                                    continue;
                            }
                            
                            // 3. TCP out: create output obj to send message to server
                            PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
                            
                            // - create request id then send
                            String reqId = generateUniqueID();
                            printWriter.println(reqId + " " + command);
                            
                            
                            // 4. TCP in: init input object to get input from server
                            Scanner inputScanner = new Scanner(socket.getInputStream());

                            // 5. TCP set timeout
                            socket.setSoTimeout(3000);

                            // case A: timeout after 2 seconds
                            if (!inputScanner.hasNextLine()) {
                                
                                // unsresponsive server
                                String currentTimestamp = getDate();
                                System.out.println(String.format(
                                    "[%s] Timeout occurred for request= %s",
                                    currentTimestamp, reqId
                                    ));
                                    
                                    // skip this response
                                    continue;
                                }
                            // 6. not timeout, get response and convert to hash map
                            String resString = inputScanner.nextLine();

                            Map<String, String> resObj = convertJsonToMap(resString);
   
                            // case B: responseId doesn't match
                            if (! validateResponseId(reqId, resObj.get("reqId"))) {
                                // skip this response
                                continue;
                            }

                            // 7. print response to terminal
                            printResponse(reqId, resString);

                            // 8. end of client request
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
