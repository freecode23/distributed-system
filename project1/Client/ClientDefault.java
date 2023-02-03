import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Date;
public class ClientDefault implements Client {

    @Override
    public void startClient() {
        // TODO Auto-generated method stub
        
    }

    private boolean isWordNumeric(String word) {
        for (char c : word.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }
    @Override
    public void validateCommand(String command) throws IllegalArgumentException {
        if (command == null || command.isEmpty()) {
            // the string is either null or empty
            throw new IllegalArgumentException("invalid empty command");
        }

        String[] commandArr = command.split(" ");

        
        // 1. check by command
        if ("put".equals(commandArr[0].toLowerCase())){

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

    public ArrayList<String> parseTextFile() {
        ArrayList<String> arrL = new ArrayList<String>();

        try {
            File file = new File("../file.txt");
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

    @Override
    public String getDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");
        long timestamp = System.currentTimeMillis();
        String currentTimestamp = formatter.format(new Date(timestamp));
        return currentTimestamp;
    }

}
    


