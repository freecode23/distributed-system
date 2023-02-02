public class ServerDriver {

    public static void main(String[] args) {

        int port = Integer.parseInt(args[0]);
        // 1. create TCP server
        Server tcpServer = new TcpServer(port);
        tcpServer.startThread();


    }

}
