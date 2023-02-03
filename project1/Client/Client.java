public interface Client {
    /**
     * Start thread of client instance
     */
    public void startClient();

    public void validateCommand(String command);

    public String getDate();
}
