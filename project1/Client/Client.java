import java.util.Map;

public interface Client {
    /**
     * Start thread of client instance
     */
    public void startClient();

    /**
     * Given a command received from the user or a file, check if its a valid command
     * @param command
     * @return true if the command is valid, false otherwise
     */
    public boolean validateCommand(String command);


    /**
     * Get current time up to milisecond precision
     * @return the time
     */
    public String getDate();

    public String generateUniqueID();

    /**
     * Log the response received by the client and indicate the request id 
     * to prove that its the correct response
     * @param reqId
     * @param resString
     */
    public void printResponse(String reqId, String resString);


    /**
     * Check if the reqId and resId matches.
     * Log some error message if it doesn't match
     * @param reqId
     * @param resId
     * @return false if it doesnt match, true otherwise
     */
    public boolean validateResponseId(String reqId, String resId);


    /**
     * Convert json String to Json object in Map form
     * @param jsonString
     * @return hashmap of json 
     */
    public Map<String, String> convertJsonToMap(String jsonString);

}
