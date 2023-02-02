import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientMultiThread extends Thread {
    public static void main(String[] args) throws IOException {

        // init executor object that creates 10 threads
        ExecutorService executor = Executors.newFixedThreadPool(10);

        // Use executor
        for (int i = 0; i < 10; i++) {
            final int finalI = i;
            executor.execute(new Runnable(){
                public void run(){
                    System.out.println("request " + finalI);
                    try {

                        // - initsocket
                        Socket socket = new Socket("localhost", 3200);

                        // - create output obj and send message to server 
                        PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
                        output.println("Client: Hello Server!");

                        // - init input object to get input from server
                        Scanner input = new Scanner(socket.getInputStream());
                        String response = input.nextLine();

                        // - print response to terminal
                        System.out.println(response);
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

