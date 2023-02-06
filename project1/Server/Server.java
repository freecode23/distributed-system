public interface Server {
    /**
     * This method will start a single thread of the server
     * program
     */
    public void startThread();

    /**
     * Given a client input as argument it will check if its valid according to the requirement
     * @param clientInput
     * @return true if the client input is valid, false otherwise
     */
    public boolean validateClientInput(String clientInput);

    /**
     * Get current time up to milisecond precision
     * @return the time
     */
    public String getDate();

}
