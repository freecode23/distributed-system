public interface Server {
    /**
     * This function will start a single thread of the server
     * program
     */
    public void startThread();

    public boolean validateClientInput(String clientInput);

    public String getDate();

    public String[] splitIdString(String idstring);
}
