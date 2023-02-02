
/**
 * Author: Sherly Hartono
 * Email: s.hartono@northeastern.edu
 */
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class TCPServer {
  public static void main(String[] args) {
    int port = Integer.parseInt(args[0]);

    try {
      // 0. Create a ServerSocket and bind it to the specified port
      ServerSocket server = new ServerSocket(port);
      System.out.println("Server started and listening to port " + port);

      // 1. Wait for a client to connect - will wait here until client connect
      Socket socket = server.accept();
      // System.out.println("Client connected");

      // 2. Read data from the client
      Scanner scannerSocketInput = new Scanner(socket.getInputStream());
      String clientInput = scannerSocketInput.nextLine();
      // System.out.println("Client input: " + clientInput);

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
      server.close();
      scannerSocketInput.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
