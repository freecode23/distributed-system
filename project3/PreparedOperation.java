public class PreparedOperation {
    public OperationType operationType;
    private int key;
    private int value;
    private String clientIp;
    private int clientPort;

    public PreparedOperation(OperationType operationType, int key, int value, String clientIp, int clientPort) {
        this.operationType = operationType;
        this.key = key;
        this.value = value;
        this.clientIp = clientIp;
        this.clientPort = clientPort;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public int getKey() {
        return key;
    }

    public int getValue() {
        return value;
    }

    public String getClientIp() {
        return clientIp;
    }

    public int getClientPort() {
        return clientPort;
    }
}