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

    public ConsensusResult getConsensus(Proposal proposal) {
        System.out
                .println(String.format("\nproposer#[%d] proposing id#%s val=%s(%d)", 
                this.currPort, 
                Integer.toString(proposal.id).substring(Integer.toString(proposal.id).length() - 4),
                proposal.operation.opType,
                 proposal.operation.key));

        // phase 0 - populate list of acceptor server (including itself)
        List<KeyValueService.Client> acceptorServers = new ArrayList<>();
        List<TTransport> acceptorTransports = new ArrayList<>(); 
        ConsensusResult consResult = new ConsensusResult();

        for (int replicaPort : replicaPorts) {
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
                consResult.isConsensusReached = false;
                return consResult;
            }
            
        }
        

        // Phase 1: sends prepare
        int promiseCount = 0;
        int nullPromiseCount = 0;
        int maxIdToSendAccept = -1;
        int maxAttempts = 3;
        int retryDelay = 500; // 500 ms delay between retries

        for (int i = 0; i < acceptorServers.size(); i++) {
            KeyValueService.Client acceptor = acceptorServers.get(i);
            Integer acceptorPort = replicaPorts.get(i);
            
            int acceptorAttempt = 0;
            boolean success = false;
            while (acceptorAttempt < maxAttempts && !success) {
                try {
                    // case 0: server failed
                    Promise prom = acceptor.prepare(proposal);
                    if (prom == null) {
                        acceptorAttempt++;
                        System.out.println(String.format("proposer#[%d] promise request FAILED by [%d], attempt=%d", 
                        this.currPort,
                        acceptorPort,
                        acceptorAttempt));

                        // sleep
                        try {
                            Thread.sleep(retryDelay);
                        } catch (InterruptedException e) {
                            System.out.println(String.format("proposer#[%d] promise request FAILED by [%d]",
                                    this.currPort,
                                    acceptorPort));
                        }

                        // retry
                        continue;
                        
                    // case 1: promise is rejected/ignored acceptor id too small
                    } else if (prom.status == Status.REJECTED) {

                        System.out.println(String.format("proposer#[%d] promise request REJECTED by [%d] attempt=%d",
                        this.currPort,
                        acceptorPort,
                        acceptorAttempt));

                        success = true;

                    } else {
                        promiseCount += 1;
                        System.out.println(String.format("proposer#[%d] promise request PROMISED by [%d] attempt=%d",
                        this.currPort, 
                        acceptorPort,
                        acceptorAttempt));

                        // case 2 : if not null : get the id 
                        if (prom.proposal != null) {
                            maxIdToSendAccept = Math.max(maxIdToSendAccept, prom.proposal.id);

                        // case 3: proposal is null , we can use our own value
                        } else {
                            nullPromiseCount += 1;
                        }
                        success = true;
                    }
                    
                } catch (TException te) {
                    System.out.println(String.format("proposer#[%d] promise request FAILED by [%d]'s network, attempt=%d",
                            this.currPort,
                            acceptorPort,
                            acceptorAttempt));
                    acceptorAttempt++;

                    // sleep
                    try {
                        Thread.sleep(retryDelay);
                    } catch (InterruptedException ie) {
                        System.out.println(
                                String.format("proposer#[%d] promise request FAILED by [%d]. cannot retry attempt=%d",
                                        this.currPort,
                                        acceptorPort,
                                        acceptorAttempt));
                    }
                }
            }
        }
        
        // return false if in phase1 most servers died
        int half = Math.floorDiv(acceptorServers.size(), 2) + 1;
        if (promiseCount < half) {
            consResult.isConsensusReached = false;
            return consResult;
        }

        // phase2: sends accept
        int acceptCount = 0;
        for (int i = 0; i < acceptorServers.size(); i++) {
            KeyValueService.Client acceptor = acceptorServers.get(i);
            Integer acceptorPort = replicaPorts.get(i);
            int acceptorAttempt = 0;
            boolean success = false;
            while (acceptorAttempt < maxAttempts && !success) {
                try {
                    Proposal acceptedProposal = acceptor.accept(proposal);
                    if (acceptedProposal != null) {
                        acceptCount += 1;
                        System.out.println(
                                String.format("proposer#[%d]'s accept request ACCEPTED by [%d], attempt=%d", this.currPort,
                                        acceptorPort,
                                        acceptorAttempt));
                        success = true;
                    } else {
                        acceptorAttempt++;
                        System.out.println(String.format("proposer#[%d] accept request FAILED by [%d], attempt=%d",
                                this.currPort,
                                acceptorPort,
                                acceptorAttempt));
                        
                        // sleep
                        try {
                            Thread.sleep(retryDelay);
                        } catch (InterruptedException e) {
                            System.out.println(String.format("proposer#[%d] promise request FAILED by [%d]",
                                    this.currPort,
                                    acceptorPort));
                        }

                        // retry
                        continue;
                    }
                    
                } catch (TException e) {
                    System.out.println(String.format("proposer#[%d] accept request FAILED by [%d]'s network, attempt=%d",
                                    this.currPort,
                                    acceptorPort,
                                    acceptorAttempt));
                    acceptorAttempt++;

                    // sleep
                    try {
                        Thread.sleep(retryDelay);
                    } catch (InterruptedException ie) {
                        System.out.println(
                                String.format("proposer#[%d] accept request FAILED by [%d]. cannot retry attempt=%d",
                                        this.currPort,
                                        acceptorPort,
                                        acceptorAttempt));
                    }
                }
            }
        }
        if (acceptCount < half) {
            consResult.isConsensusReached = false;
            return consResult;
        }

        // phase3: sends learn
        int committedLearnerCount = 0;
        for (int i = 0; i < acceptorServers.size(); i++) {
            KeyValueService.Client acceptor = acceptorServers.get(i);
            Integer acceptorPort = replicaPorts.get(i);
            // System.out.println(String.format("\nlearn pahse i=%d, acceptorPort#[%d] learning id#%s", i,
            //     replicaPorts.get(i), 
            //         Integer.toString(proposal.id).substring(Integer.toString(proposal.id).length() - 4)));
            try {
                Result commitResult = acceptor.learn(proposal); // will commit 
                
                // if this is the port of proposer, save the commit result to consensus result
                if (acceptorPort == this.currPort) {
                    consResult.proposerCommitResult = commitResult;
                }
            } catch (TException e) {
                 System.out.println("error when asking acceptor to learn " + e.getMessage());
            }
            // System.out.println("finish learn");
        }

        // Close communication
        for (TTransport acceptorTransport : acceptorTransports) {
            acceptorTransport.close();
        }
        // consensus has been reached. but it maybe that some learners have not committed
        return consResult;
    }

}
