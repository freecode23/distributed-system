public interface Server {
    /**
     * This function will start a single thread of the server
     * program
     */
    public void startThread();

    public void validateClientInput(String clientInput) throws IllegalArgumentException;

    public String getDate();

    public String[] splitIdString(String idstring);
}
