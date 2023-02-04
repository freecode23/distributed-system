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
                            // 1. init socket
                            // TODO: change host name
                            Socket socket = new Socket("localhost", port);

                            // 2. OUT: create output obj to send message to server
                            PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);

                            // 3. validate command
                            try {
                                validateCommand(command);

                            } catch (IllegalArgumentException e) {
                                String currentTimestamp = getDate();

                                // save to log file
                                System.out.println(String.format(
                                    "[%s] Request #%d is invalid",
                                    currentTimestamp, requestNum));
                                continue;
                            }
                            
                            // 4. IN: init input object to get input from server
                            Scanner inputScanner = new Scanner(socket.getInputStream());
                            
                            // 5. send message to server
                            // - append request id to the end of command
                            String reqId = generateUniqueID();
                            printWriter.println(reqId + " " + command);
                            String response = "";

                            // 6. get response
                            socket.setSoTimeout(3000);

                            // case A: timeout after 2 seconds
                            if (!inputScanner.hasNextLine()) {

                                // unsresponsive server
                                String currentTimestamp = getDate();
                                System.out.println(String.format(
                                    "[%s] Timeout occurred for request= %d",
                                    currentTimestamp, requestNum
                                    ));

                                // skip this line
                                continue;
                            }
                            response = inputScanner.nextLine();

                            // case B: responseId doesn't match
                            String[] idRes = splitIdString(response);

                            if (! reqId.equals(idRes[0])){

                                // unsresponsive server
                                String currentTimestamp = getDate();
                                System.out.println(String.format(
                                        "[%s] Received unrequested response of id #[%s]",
                                        currentTimestamp, idRes[0]));
                                // don't print this request
                                continue;
                            }

                            // 7. print response to terminal
                            String currentTimestamp = getDate();
                            System.out.println(String.format(
                                "[%s] response received for reqId=[%s]: %s", currentTimestamp, reqId, idRes[1]
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
