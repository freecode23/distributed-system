import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import java.io.IOException;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CommandClient {

    private static void validateCommand(String command) throws IllegalArgumentException {
        String currentTimestamp = getDate();
        if (command == null || command.isEmpty()) {
            // the string is either null or empty
            throw new IllegalArgumentException("invalid empty command");
        }

        String[] commandArr = command.split(" ");

        // 1. check by command
        if ("put".equals(commandArr[0].toLowerCase())) {

            // - validate argument number
            if (commandArr.length != 3) {
                throw new IllegalArgumentException("invalid number of arguments");
            }

            // - validate numeric
            if (!isWordNumeric(commandArr[1]) || !isWordNumeric(commandArr[2])) {
                throw new IllegalArgumentException("invalid key given, not numeric");
            }

        } else if ("get".equals(commandArr[0].toLowerCase())) {
            // - validate argument number
            if (commandArr.length != 2) {
                throw new IllegalArgumentException("invalid number of arguments");
            }

            // - validate numeric
            if (!isWordNumeric(commandArr[1])) {
                throw new IllegalArgumentException("invalid key given, not numeric");
            }

        } else if ("delete".equals(commandArr[0].toLowerCase())) {
            // - validate argument number
            if (commandArr.length != 2) {
                throw new IllegalArgumentException("invalid number of arguments");
            }

            // - validate numeric
            if (!isWordNumeric(commandArr[1])) {
                throw new IllegalArgumentException("invalid key given, not numeric");
            }

        } else {
            throw new IllegalArgumentException("Illegal command. Command should be put, delete, or get with integer arguments");
        }
    }

    private static ArrayList<String> parseTextFile(String filename) {
        ArrayList<String> arrL = new ArrayList<String>();

        try {
            File file = new File(filename);
            Scanner scan = new Scanner(file);

            while (scan.hasNextLine()) {
                String line = scan.nextLine();
                // String[] command = line.split(" ");
                line.toLowerCase();
                arrL.add(line);
            }
            scan.close();

        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.getMessage());
        }
        return arrL;
    }

    private static boolean isWordNumeric(String word) {
        for (char c : word.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            } 
        }
        return true;
    }
    
    private static String getDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");
        long timestamp = System.currentTimeMillis();
        String currentTimestamp = formatter.format(new Date(timestamp));
        return currentTimestamp;
    }

    private static String generateUniqueID() {
        return UUID.randomUUID().toString();
    }

    private static void printLog(Result res, String reqId, String command) {

        String currentTimestamp = getDate();
        String reqId4= reqId.substring(Math.max(reqId.length() - 4, 0)); // get last 5char only

        // 1. invalid command
        if ("invalid".equals(command)) {
            System.out.println(
                String.format("[%s] ERROR: reqId=..%s %s", currentTimestamp,
                reqId4, res.msg));
        
        // 2. timout
        } else if ("timeout".equals(command)) {
            System.out.println(String.format("[%s] ERROR: reqId=%s Server Timeout", currentTimestamp, 
            reqId4));

        // 3. unrequested response
        } else if (!(res.reqId).equals(reqId)) {

            System.out.println(
                String.format("[%s] ERROR: reqId=..%s Received unsolicited response of reqId=%s", currentTimestamp,
                reqId4, res.reqId));
                
        // 3. sucessful
        } else {
            System.out.println(
                String.format("[%s] SUCCESS: reqId=..%s %s val=%d",
                currentTimestamp, reqId4, command, res.value));

        }
    }
    
    private static void executeCommand(String command, Command.Client client, Socket clientSocket) {

        Result res = new Result();
        String reqId = generateUniqueID();
        String clientIp = clientSocket.getLocalAddress().getHostAddress();
        int clientPort = clientSocket.getLocalPort();

        try {
            try {
                validateCommand(command);
            } catch (IllegalArgumentException ex) {
                res.msg = ex.getMessage();
                printLog(res, reqId, "invalid");
                return;
            }

            // 2. split the command
            String[] commandArr = command.split(" ");
            int key = Integer.parseInt(commandArr[1]);

            // 3. execute
            switch (commandArr[0].toLowerCase()) {
                case "put":
                    int val = Integer.parseInt(commandArr[2]);
                    res = client.put(key, val, reqId, clientIp, clientPort);
                    printLog(res, reqId, "put");
                    break;

                case "get":
                    res = client.get(key, reqId, clientIp, clientPort);
                    printLog(res, reqId, "get");
                    break;

                case "delete":
                    res = client.delete(key, reqId, clientIp, clientPort);
                    printLog(res, reqId,"delete");;

                    break;

                default:
                    // do nothing, already taken care of in invalid command
                    break;
            }
                
        } catch (TException ex) {

            if (ex.getCause() instanceof SocketTimeoutException) {
                // handle socket timeout exception
                printLog(res, reqId, "timeout");;
               
            } else {
                // handle other TTransportException
                System.out.println(ex.getMessage());
            }
        }

    }

    private static int getRandomServerIndex(int serverCount) {
        Random random = new Random();
        return random.nextInt(serverCount);
    }


    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println(
                    "please enter the argument of the host ip address. e.g 127.0.0.1");
            return;
        }

        int[] serverPorts = { 9000, 9001, 9002, 9003, 9004 };


        String serverhost = args[0];
        int timeout = 5000; //5000 ms timeout

        // 1. prepopulate
        try {
            int serverIndex = getRandomServerIndex(5);
            int serverPort = serverPorts[serverIndex];
            // 1.1 init client and socket
            TTransport transport = new TSocket(serverhost, serverPort, timeout);
            transport.open();
            TBinaryProtocol protocol = new TBinaryProtocol(transport);
            Command.Client client = new Command.Client(protocol);
            Socket clientSocket = ((TSocket) transport).getSocket();


            // 1.2 read commands from textfile
            String preFilename = "./lib/prepop.txt";
            ArrayList<String> preCommands = parseTextFile(preFilename);

            // 1.3 prepopulate key store
            for(String preCommand: preCommands) {
                executeCommand(preCommand, client, clientSocket);
            }
            transport.close();
        } catch (TTransportException ex) {
            
            // handle other TTransportException
            System.out.println(ex.getMessage());

        } 

        // 2. run actual command
        // init executor object that creates 10 threads
        ExecutorService executor = Executors.newFixedThreadPool(10);
        String actFilename = "./lib/file.txt";
        ArrayList<String> actCommands = parseTextFile(actFilename);

        // Use executor
        for (String actCommand : actCommands) {
            executor.execute(new Runnable(){
                public void run(){
                    try {
                        int serverIndex = getRandomServerIndex(5);
                        int serverPort = serverPorts[serverIndex];
                        // 2.1 init client and timout
                        TTransport transportM = new TSocket(serverhost, serverPort, timeout);
                        transportM.open();
                        TBinaryProtocol protocolM = new TBinaryProtocol(transportM);
                        Command.Client clientM = new Command.Client(protocolM);
                        Socket clientSocket = ((TSocket) transportM).getSocket();
                        
                        // 2.2 execute command
                        executeCommand(actCommand, clientM, clientSocket);
                        transportM.close();
                 
                    } catch (TTransportException ex) {
                         // handle other transport errors
                         System.out.println(ex.getMessage());
                    } 
                }
            });
        }

        executor.shutdown();
    }
    
}