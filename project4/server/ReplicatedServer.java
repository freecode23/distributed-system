import java.util.ArrayList;
import java.util.List;

public class ReplicatedServer {
    private List<KeyValueServer> servers;

    public ReplicatedServer(int[] ports) {

        List<Integer> replicaPorts = new ArrayList<>();
        for (int port : ports) {
            replicaPorts.add(port);
        }
        servers = new ArrayList<>();
        for (int port : ports) {
            KeyValueServer server = new KeyValueServer(port, replicaPorts);
            servers.add(server);
        }
    }

    public void startServers() {
        for (KeyValueServer server : servers) {

            // pass in the start method of KeyValueServer
            // this allows the start method of the server object to run in separate thread
            new Thread(server::start).start();
        }
    }
}