import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ServerDriver {
    public static void main(String[] args) {
        // The ports you want the servers to run on
        List<Integer> ports = new ArrayList<>(Arrays.asList(9000, 9001, 9002, 9003, 9004));
        ReplicatedServer replicatedServer = new ReplicatedServer(ports);
        replicatedServer.startServers();
    }
}