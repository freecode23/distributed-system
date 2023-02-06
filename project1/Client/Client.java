import java.util.Map;

public interface Client {
    /**
     * Start thread of client instance
     */
    public void startClient();

    public boolean validateCommand(String command);

    public String getDate();

    public String generateUniqueID();

    public void printResponse(String reqId, String resString);

    public boolean validateResponseId(String reqId, String resId);

    public Map<String, String> convertJsonToMap(String jsoString);

}
