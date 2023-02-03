import java.text.SimpleDateFormat;
import java.util.Date;

public class ServerDefault implements Server{

    @Override
    public void startThread() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void validateClientInput(String clientInput) throws IllegalArgumentException {
        String[] commandArr = clientInput.split(" ");
        commandArr[0] = commandArr[0].toLowerCase();
        if (!"put".equals(commandArr[0]) && 
            !"get".equals(commandArr[0]) &&
                !"delete".equals(commandArr[0])) {
                    String time = getDate();

                throw new IllegalArgumentException(String.format(
                        "[%s] Illegal command from user. Command should only be put, delete, or get with two integer argument",
                        time));
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
