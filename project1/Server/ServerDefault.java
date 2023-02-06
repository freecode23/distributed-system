import java.text.SimpleDateFormat;
import java.util.Date;

public class ServerDefault implements Server{

    @Override
    public void startThread() {
        // TODO Auto-generated method stub
        
    }

    private String[] splitIdString(String response) {
        String[] parts = response.split(" ",2);
        // System.out.print("server split string id**= "+ parts[0] +"\n");
        // System.out.println("command**= "+ parts[1]);
        return parts;
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
    public boolean validateClientInput(String clientInput) throws IllegalArgumentException {
        String currentTimestamp = getDate();
        String[] idReq = splitIdString(clientInput);
        String command = idReq[1];

        if (command == null || command.isEmpty()) {
            // the string is either null or empty
            System.out.println(String.format("[%s]invalid empty command", currentTimestamp));
            return false;
        }   

        int putArgNum = 3; // command, key, val
        int getArgNum = 2; // command, key
        int delArgNum = 2; // command, key

        String[] commandArr = command.split(" ");

        // 1. check by command
        if ("put".equals(commandArr[0].toLowerCase())) {
            // - validate argument number
            if (commandArr.length != putArgNum) {
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
            if (commandArr.length != getArgNum) {
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
            if (commandArr.length != delArgNum) {
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

    @Override
    public String getDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");
        long timestamp = System.currentTimeMillis();
        String currentTimestamp = formatter.format(new Date(timestamp));
        return currentTimestamp;
    }
    
}
