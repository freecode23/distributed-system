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
        System.out.println("Phase1");
        // Phase 1: init prepare
        int promiseCount = 0;
        int nullPromiseCount = 0;
        int maxIdToSendAccept = -1;
        for (int i = 0; i < acceptorServers.size(); i++) {
            System.out.println(String.format("i=%d", i));
            KeyValueService.Client acceptor = acceptorServers.get(i);
            Integer portNum = replicaPorts.get(i);
            try {
                Promise prom = acceptor.prepare(proposal);

                
                // case 1: promise is null acceptor never respond
                if (prom.status == Status.REJECTED) {
                    System.out.println(String.format("promise reqquest rejected by [%d]", portNum));

                } else {
                    System.out.println(String.format("promise request not rejected by [%d]", portNum));
                    // case 2 : if not null : get the id 
                    if (prom.proposal != null) {
                        maxIdToSendAccept = Math.max(maxIdToSendAccept, prom.proposal.id);
                        System.out.println("prom not null");
                    // case 3: proposal is null , we cna use our own value
                    } else {
                        nullPromiseCount += 1;
                        System.out.println("prom null");
                    }
                }
                

            } catch (TException e) {
                System.out.println("error when asking acceptor to prepare " + e.getMessage());
            }
        }
        System.out.println(String.format("finish"));

        // Close communication
        for (TTransport acceptorTransport : acceptorTransports) {
            acceptorTransport.close();
        }
        return true;
    }

}
