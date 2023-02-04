import java.text.SimpleDateFormat;
import java.util.Date;

public class ServerDefault implements Server{

    @Override
    public void startThread() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public String[] splitIdString(String response) {
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
    public void validateClientInput(String clientInput) throws IllegalArgumentException {

        String[] idReq = splitIdString(clientInput);
        String reqId = idReq[0];
        String command = idReq[1];

        if (command == null || command.isEmpty()) {
            // the string is either null or empty
            throw new IllegalArgumentException("invalid empty command");
        }   

        int putArgNum = 3; // command, key, val
        int getArgNum = 2; // command, key
        int delArgNum = 2; // command, key

        String[] commandArr = command.split(" ");

        // 1. check by command
        if ("put".equals(commandArr[0].toLowerCase())) {

            // - validate argument number (including id)
            if (commandArr.length != putArgNum) {
                throw new IllegalArgumentException("invalid number of arguments");
            }

            // - validate numeric
            if (!isWordNumeric(commandArr[1]) || !isWordNumeric(commandArr[2])) {
                throw new IllegalArgumentException("invalid key given, not numeric");
            }

        } else if ("get".equals(commandArr[0].toLowerCase())) {
            // - validate argument number
            if (commandArr.length != getArgNum) {
                throw new IllegalArgumentException("invalid number of arguments");
            }

            // - validate numeric
            if (!isWordNumeric(commandArr[1])) {
                throw new IllegalArgumentException("invalid key given, not numeric");
            }

        } else if ("delete".equals(commandArr[0].toLowerCase())) {
            // - validate argument number
            if (commandArr.length != delArgNum) {
                throw new IllegalArgumentException("invalid number of arguments");
            }

            // - validate numeric
            if (!isWordNumeric(commandArr[1])) {
                throw new IllegalArgumentException("invalid key given, not numeric");
            }

        } else {
            throw new IllegalArgumentException(
                    "Illegal command. Command should be put, delete, or get with integer arguments");
        }

    }

    @Override
    public String getDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");
        long timestamp = System.currentTimeMillis();
        String currentTimestamp = formatter.format(new Date(timestamp));
        return currentTimestamp;
    }
    
}
