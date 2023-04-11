import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Proposer {
    int currPort;
    List<Integer>replicaPorts;

    public Proposer( int currPort, List<Integer> replicaPorts) {
        this.currPort = currPort;
        this.replicaPorts = replicaPorts;
    }

    public boolean getConsensus(Proposal proposal) {

        // phase 0 - populate list of acceptor server (including itself)
        List<KeyValueService.Client> acceptorServers = new ArrayList<>();
        List<TTransport> acceptorTransports = new ArrayList<>(); 

        for (int replicaPort : replicaPorts) {
            // - create acceptor Server
            if (replicaPort != currPort) {
                try {
                    TTransport replicaTransport = new TSocket("localhost", replicaPort);
                    // 1. init replica communication
                    replicaTransport.open();
                    TBinaryProtocol replicaProtocol = new TBinaryProtocol(replicaTransport);
                    KeyValueService.Client replica = new KeyValueService.Client(replicaProtocol);

                    // 2. add these replicas in list of acceptors
                    acceptorServers.add(replica);
                    acceptorTransports.add(replicaTransport);
                    
                } catch (TException e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }

        // Phase 1: init prepare
        for (KeyValueService.Client acceptor : acceptorServers) {

            try {
                Promise prom = acceptor.prepare(proposal);
            } catch (TException e) {
                System.out.println("error when asking acceptor to prepare " + e.getMessage());
            }
        }


        // Close communication
        for (TTransport acceptorTransport : acceptorTransports) {
            acceptorTransport.close();
        }
        return true;
    }

}
