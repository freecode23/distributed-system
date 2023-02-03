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

            throw new IllegalArgumentException("Illegal command should be put, delete, or get");
        }
        
    }
    
}
