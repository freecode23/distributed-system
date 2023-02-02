
/**
 * Author: Sherly Hartono
 * Email: s.hartono@northeastern.edu
 */

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class TCPClient {
  public static void main(String[] args) {
    // init server's host and port that client wants to conenct to
    String host = args[0]; 
    int port = Integer.parseInt(args[1]);

    try {
      // 0. Create a socket and connect to the server
      Socket socket = new Socket(host, port);
      System.out.println("Connected to " + host + " on port " + port);

      // 1. Read input from the user
      System.out.print("Enter text: ");
      Scanner scannerUserInput = new Scanner(System.in);
      String userInput = scannerUserInput.nextLine();

      // 2. Send the userInput to the server
      // - init printwriter
      PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);

      // - send to server
      printWriter.println(userInput);
      // System.out.println("Sent to server: " + userInput);

      // 3. Read the response in the socket from the server
      Scanner scannerSocketInput = new Scanner(socket.getInputStream());
      String serverResponse = scannerSocketInput.nextLine();
      System.out.println("Response from server: " + serverResponse);

      // 4. Close the socket and scanner
      socket.close();
      scannerSocketInput.close();
      scannerUserInput.close();

    } catch (IOException e) {
      e.printStackTrace();
    } finally {
    }
  }
}
