public class ServerDriver {
    public static void main(String[] args) {
        int[] ports = { 9000, 9001, 9002, 9003, 9004 }; // The ports you want the servers to run on
        ReplicatedServer replicatedServer = new ReplicatedServer(ports);
        replicatedServer.startServers();
    }
}