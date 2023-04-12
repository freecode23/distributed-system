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
        System.out
                .println(String.format("\nproposer#[%d] proposing id#%s val=%s(%d)", 
                this.currPort, 
                Integer.toString(proposal.id).substring(Integer.toString(proposal.id).length() - 4),
                proposal.operation.opType,
                 proposal.operation.key));

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
        // Phase 1: sends prepare
        int promiseCount = 0;
        int nullPromiseCount = 0;
        int maxIdToSendAccept = -1;
        for (int i = 0; i < acceptorServers.size(); i++) {
            KeyValueService.Client acceptor = acceptorServers.get(i);
            Integer acceptorPort = replicaPorts.get(i);
            

            try {
                Promise prom = acceptor.prepare(proposal);
                // case 1: promise is null acceptor never respond
                if (prom.status == Status.REJECTED) {
                    System.out.println(String.format("proposer#[%d] promise request rejected by [%d]",this.currPort, acceptorPort));

                } else {
                    promiseCount += 1;
                    System.out.println(String.format("proposer#[%d] promise request accepted by [%d]",this.currPort, acceptorPort));
                    // case 2 : if not null : get the id 
                    if (prom.proposal != null) {
                        maxIdToSendAccept = Math.max(maxIdToSendAccept, prom.proposal.id);
                        // System.out.println("prom not null");
                    // case 3: proposal is null , we can use our own value
                    } else {
                        nullPromiseCount += 1;
                        // System.out.println("prom null");
                    }
                }
                
            } catch (TException e) {
                System.out.println("error when asking acceptor to prepare " + e.getMessage());
            }
        }
        // return false if in phase1 most servers died
        int half = Math.floorDiv(acceptorServers.size(), 2) + 1;
        if (promiseCount < half) {
            return false;
        }

        // phase2: sends accept
        int acceptCount = 0;
        for (int i = 0; i < acceptorServers.size(); i++) {
            // System.out.println(String.format("i=%d", i));
            KeyValueService.Client acceptor = acceptorServers.get(i);
            Integer acceptorPort = replicaPorts.get(i);

            try {
                Proposal acceptedProposal = acceptor.accept(proposal);
                if (acceptedProposal != null) {
                    acceptCount += 1;
                    System.out.println(
                            String.format("proposer#[%d]'s accept request ACCEPTED by [%d]", this.currPort,
                                    acceptorPort));
                } else {
                    System.out.println(
                            String.format("proposer#[%d]'s accept request REJECTED by [%d]", this.currPort, 
                                    acceptorPort));
                }
                
            } catch (TException e) {
                System.out.println("error when asking acceptor to accept " + e.getMessage());
            }
        }
        if (acceptCount < half) {
            return false;
        }
        // phase3: sends accept

        // Close communication
        for (TTransport acceptorTransport : acceptorTransports) {
            acceptorTransport.close();
        }
        return true;
    }

}
