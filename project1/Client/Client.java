public interface Client {
    /**
     * Start thread of client instance
     */
    public void startClient();

    public void validateCommand(String command);

    public String getDate();

    public String generateUniqueID();


    /**
     * extract id from the start of the command
     * @param command
     * @return an array of [id, response string]
     */
    public String[] splitIdString(String command);
}
