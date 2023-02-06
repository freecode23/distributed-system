public class ServerDriver {

    private static boolean isWordNumeric(String word) {
        for (char c : word.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }
    public static void main(String[] args) {

        if (args.length != 2) {
            System.out.println("please enter the argument of the port number followed by space and then 0 for tcp and 1 for udp");
            return;
        }
        if (!isWordNumeric(args[0]) || !isWordNumeric(args[1]) ) {
            System.out.println("Invalid argument, please enter port number followed by space and then 0 for tcp and 1 for udp");
            return;
        }
        int port = Integer.parseInt(args[0]);
        int connection = Integer.parseInt(args[1]);

        Server server;
        if (connection == 0) {
            // 1. create TCP server
            server = new TcpServer(port);

        } else {

            // 2. create TCP server
            server = new UdpServer(port);
        }
        server.startThread();


    }

}
