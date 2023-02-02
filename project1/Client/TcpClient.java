import java.io.IOException;
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
        for (int i = 0; i < this.threadN; i++) {
            final int requestNum = i;
            int port = this.port;

            // 1. run thread(s)
            this.executor.execute(new Runnable() {
                public void run() {
                    // System.out.println("request=" + requestNum);
                    try {

                        // - init socket
                        Socket socket = new Socket("localhost", port);

                        // - read from textfile
                        ArrayList<String> commands = parseTextFile();
                        // System.out.println(commands);

                        // - OUT: create output obj and send message to server
                        PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
                        
                        // - send command
                        for(String command: commands) {
                            System.out.println(command);
                            printWriter.println(command);
                        }

                        // - IN: init input object to get input from server
                        Scanner input = new Scanner(socket.getInputStream());
                        String response = input.nextLine();

                        // // - print response to terminal
                        // System.out.println("response " + finalI + "=" + response);

                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        executor.shutdown();

    }
    
}
