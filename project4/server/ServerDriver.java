import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ServerDriver {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: java ServerDriver <port> [<host:port>...]");
            System.exit(1);
        }

        int port = Integer.parseInt(args[0]);

        List<Integer> replicaPorts = new ArrayList<>();
        for (int i = 1; i < args.length; i++) {
            // System.out.println(String.format("i=%s", args[i]));
            replicaPorts.add(Integer.valueOf(args[i]));
        }

        KeyValueServer server = new KeyValueServer(port, replicaPorts);
        server.start();
    }
}

