public class PreparedOperation {
    public OperationType operationType;
    private int key;
    private int value;

    public PreparedOperation(OperationType operationType, int key, int value) {
        this.operationType = operationType;
        this.key = key;
        this.value = value;
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
}