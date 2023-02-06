public class ClientDriver {
    private static boolean isWordNumeric(String word) {
        for (char c : word.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }
    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("please enter the argument of the port number followed by space and then 0 for tcp and 1 for udp");
            return;
        }
        if (!isWordNumeric(args[1]) ||!isWordNumeric(args[2]) ) {
            System.out.println("Invalid argument, please enter port number followed by space and then 0 for tcp and 1 for udp");
            return;
        }
        String host = args[0];
        int port = Integer.parseInt(args[1]);
        int connection = Integer.parseInt(args[2]);

        Client client;
        if (connection == 0) {
            // 1. create TCP client
            client = new TcpClient(host, port, 1); 

        } else {
            // 2. create Udp server
            client = new UdpClient(host, port, 1);
        }

        client.startClient();
    }
}
