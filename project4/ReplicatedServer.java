import java.util.ArrayList;
import java.util.List;

public class ReplicatedServer {
    private List<CommandServer> servers;

    public ReplicatedServer(List<Integer> ports) {
        servers = new ArrayList<>();
        for (int port : ports) {
            CommandServer server = new CommandServer(port, ports);
            servers.add(server);
        }
    }

    public void startServers() {
        for (CommandServer server : servers) {

            // pass in the start method of CommandServer
            // this allows the start method of the server object to run in separate thread
            new Thread(server::start).start();
        }
    }
}