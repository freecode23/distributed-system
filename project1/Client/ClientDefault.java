import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
public class ClientDefault implements Client {

    @Override
    public void startClient() {
        // TODO Auto-generated method stub
        
    }

    
    @Override
    public String generateUniqueID() {
        return UUID.randomUUID().toString();
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
    public boolean validateResponseId(String reqId, String resId) {
        if (!reqId.equals(resId)) {

            // unrequested id
            String currentTimestamp = getDate();
            System.out.println(String.format(
                    "[%s] Received unrequested response of id #[%s]",
                    currentTimestamp, resId));

            return false;
        }
        return true;
    }
    
    @Override
    public boolean validateCommand(String command) {
        String currentTimestamp = getDate();
        if (command == null || command.isEmpty()) {
        // the string is either null or empty
           System.out.println(String.format("[%s]invalid empty command", currentTimestamp));
           return false;
        }

        String[] commandArr = command.split(" ");
        

        // 1. check by command
        if ("put".equals(commandArr[0].toLowerCase())){

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
            System.out.println(String.format("[%s]Illegal command. Command should be put, delete, or get with integer arguments", currentTimestamp));
            return false;
        }

        return true;
        
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

    @Override
    public void printResponse(String reqId, String resString) {
        String currentTimestamp = getDate();
        String reqIdLast3 = reqId.substring(reqId.length() - 3);
        System.out.println(String.format(
        "[%s] response received for reqId XXXX%s: %s", currentTimestamp,
        reqIdLast3, resString
        ));
    }

    @Override
    public Map<String, String> convertJsonToMap(String jsonString) {
        Gson gson = new Gson();
        Map<String, String> resObj = gson.fromJson(jsonString,
        new TypeToken<Map<String, String>>() {
        }.getType());
        return resObj;
    }
}
    


