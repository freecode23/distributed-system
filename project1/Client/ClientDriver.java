public class ClientDriver {

    public static void main(String[] args) {
        
        String host = args[0];
        int port = Integer.parseInt(args[1]);

        // 1. create TCP client
        Client tcpClient = new TcpClient(host, port, 1);

        tcpClient.startClient();
    }
}
