import java.util.ArrayList;
import java.util.List;

public class ReplicatedServer {
    private List<CommandServer> servers;

    public ReplicatedServer(int[] ports) {
        servers = new ArrayList<>();
        for (int port : ports) {
            CommandServer server = new CommandServer(port);
            servers.add(server);
        }
    }

    public void startServers() {
        for (CommandServer server : servers) {
            new Thread(server::start).start();
        }
    }
}