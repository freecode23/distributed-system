import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.UUID;
public class CommandClient {

    private static boolean validateCommand(String command) {
        String currentTimestamp = getDate();
        if (command == null || command.isEmpty()) {
            // the string is either null or empty
            System.out.println(String.format("[%s]invalid empty command", currentTimestamp));
            return false;
        }

        String[] commandArr = command.split(" ");

        // 1. check by command
        if ("put".equals(commandArr[0].toLowerCase())) {

            // - validate argument number
            if (commandArr.length != 3) {
                System.out.println(String.format(
                        "[%s]invalid number of arguments", currentTimestamp));
                return false;
            }

            // - validate numeric
            if (!isWordNumeric(commandArr[1]) || !isWordNumeric(commandArr[2])) {
                System.out.println(String.format("[%s]invalid key given, not numeric", currentTimestamp));
                return false;
            }

        } else if ("get".equals(commandArr[0].toLowerCase())) {
            // - validate argument number
            if (commandArr.length != 2) {
                System.out.println(String.format("[%s]invalid number of arguments", currentTimestamp));
                return false;
            }

            // - validate numeric
            if (!isWordNumeric(commandArr[1])) {
                System.out.println(String.format("[%s]invalid key given, not numeric", currentTimestamp));
                return false;
            }

        } else if ("delete".equals(commandArr[0].toLowerCase())) {
            // - validate argument number
            if (commandArr.length != 2) {
                System.out.println(String.format("[%s]invalid number of arguments", currentTimestamp));
                return false;
            }

            // - validate numeric
            if (!isWordNumeric(commandArr[1])) {
                System.out.println(String.format("[%s]invalid key given, not numeric", currentTimestamp));
                return false;
            }

        } else {
            System.out.println(
                    String.format("[%s]Illegal command. Command should be put, delete, or get with integer arguments",
                            currentTimestamp));
            return false;
        }

        return true;

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
    
    private static Result executeCommand(String command, Command.Client client) {

        Result res = null;
        try {
            if (! validateCommand(command)) {
                res.msg = "Invalid command only put, get, delete are allowed";
                res.value = -1;
            } 

            // 2. split the command
            String[] commandArr = command.split(" ");
            int key = Integer.parseInt(commandArr[1]);
            
            // 3. execute
            switch (commandArr[0].toLowerCase()) {
                case "put":
                    int val = Integer.parseInt(commandArr[2]);
                    res = client.put(key, val);
                    break;

                case "get":
                    res = client.get(key);
                    break;

                case "delete":
                    res = client.delete(key);
                    break;

                default:
                    break;
                }
                
            } catch (TException ex) {
                ex.printStackTrace();
            }
        return res;
    }

    public static void main(String[] args) {
        try {
            TTransport transport = new TSocket("localhost", 9090);
            transport.open();
            TBinaryProtocol protocol = new TBinaryProtocol(transport);
            Command.Client client = new Command.Client(protocol);

            // 1. prepopulate the keyValue
            // - read from textfile
            String prepop_filename = "./lib/prepop.txt";
            ArrayList<String> commands = parseTextFile(prepop_filename);

            for(String command: commands) {
                Result res = executeCommand(command, client);
            }

            // 2. 

            transport.close();
        } catch (TTransportException ex) {
            ex.printStackTrace();
        } 
    }
}