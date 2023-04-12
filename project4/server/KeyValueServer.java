import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.server.TThreadPoolServer.Args;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.THttpClient;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;



import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.text.SimpleDateFormat;
import java.net.Socket;
import java.net.URI;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class KeyValueServer {

    private final int port;
    private List<Integer>replicaPorts;
   
    public KeyValueServer(int port, List<Integer> replicaPorts) {
        this.port = port;
        this.replicaPorts = replicaPorts;
    }
    
    public static class KeyValueServiceDefault implements KeyValueService.Iface {

        private final int port;
        private List<Integer> replicaPorts;
        private Map<Integer, Integer> keyVal;
        private long promisedPropId;
        private Proposal acceptedProposal;
        private Proposer proposerRole;
        

        /**
         * 1. Constructor that will create a new TSocket for each replica server with a different port number than the current server instance
         * @param currPort the port that the current server belongs to
         * @param replicaPorts  array of port numbers representing all the replicated servers, including the current server instance.
         */
        public KeyValueServiceDefault(int currPort, List<Integer> replicaPorts) {
            this.port = currPort;
            this.replicaPorts = replicaPorts;
            this.keyVal = new ConcurrentHashMap<>();
            this.promisedPropId = 0;
            this.acceptedProposal = null;

            // - init roles
            this.proposerRole = new Proposer(currPort, replicaPorts);
      
        }

       
        private Result putHelper(int key, int val, String reqId) {
            Result result = new Result();
            result.reqId = reqId;
            result.value = val;
            String KeyValueService = "put";

            // 1. Validate
            try {
                validateKey(key, "put");
                validateValue(val);
                validateString(reqId);
                keyVal.put(key, val);
                result.msg = "op successful";
                result.status = "OK";

            } catch (IllegalArgumentException e) {
                result.msg = e.getMessage();
                result.status = "ERROR";
                result.value = 0;
            }
            return result;

        }

        private Result deleteHelper(int key, String reqId) {
            
            Result result = new Result();
            result.reqId = reqId;
            String command = "delete";

            // 1. Validate
            try {
                validateKey(key, command);
                validateString(reqId);
                int val = keyVal.get(key);
                keyVal.remove(key);
                result.msg = "op successful";
                result.status = "OK";
                result.value = val;

            } catch (IllegalArgumentException e) {
                result.msg = e.getMessage();
                result.status = "ERROR";
                result.value = 0;
            }
            return result;

        }
        
        private class AcceptCallable implements Callable<Proposal> {
            private Proposal incomingProposal;

            public AcceptCallable(Proposal incomingProposal) {
                this.incomingProposal = incomingProposal;
            }

            @Override
            public Proposal call() throws Exception {
                long promisedPropId = KeyValueServiceDefault.this.promisedPropId;

                //  1. ignore id less than accepted
                if (incomingProposal.id < promisedPropId) {
                    return null;
                } else {
                    // 2. reply with the accepted incoming proposal and send to learners
                    // - assign as its last accepted proposal
                    KeyValueServiceDefault.this.acceptedProposal = new Proposal(this.incomingProposal.id, this.incomingProposal.operation);

                    // - Question: send to learners
                    // for all acceptorServers, call their learn() method

                    // - return the same proposal to proposer
                    return this.incomingProposal;
                }
               
            }
        }

        private class PrepareCallable implements Callable<Promise> {
            private Proposal incomingProposal;

            public PrepareCallable(Proposal incomingProposal) {
                this.incomingProposal = incomingProposal;
            }

            @Override
            public Promise call() throws Exception {

                long promisedPropId = KeyValueServiceDefault.this.promisedPropId;
                Proposal oldProposal = KeyValueServiceDefault.this.acceptedProposal;

                // case1: incoming id <= maximumAccepted proposal id that this acceptor has accepted
                if (this.incomingProposal.id <= promisedPropId) {
                    System.out.println(String.format("proposalID [%d] is less or the same as last seen propId",
                            promisedPropId));
                    return new Promise(Status.REJECTED, null);

                // incoming id > the maximum id this acceptor has accepted
                } else {
                    // assign new maxID
                    KeyValueServiceDefault.this.promisedPropId = this.incomingProposal.id;

                    // case2: incoming id > the maximum 
                    // have accepted proposal with smaller id, return the old proposal (with smaller id)
                    if (oldProposal != null) {
                        Proposal copyAcceptedProposal = new Proposal(
                                oldProposal.id,
                                oldProposal.operation);
                        return new Promise(Status.ACCEPTED, copyAcceptedProposal);

                    // case3: incoming id > the maximum 
                    // never accepted proposal, return the incoming one
                    } else {
                        return new Promise(Status.ACCEPTED, null);
                    }
                }
            }
        }

        /*
         * Prepare method that will called by Proposer when trying to get consensus
         */
        @Override
        public Promise prepare(Proposal proposal) {

            // System.out.println(String.format("acceptor#[%d] is preparing id#%s", this.port, 
            // Integer.toString(proposal.id).substring(Integer.toString(proposal.id).length() - 4)));

            // create random failure
            if (Math.random() <= 0.1) {
                System.out.println(String.format("acceptor#[%d] random failure when preparing id#%s",
                this.port,
                Integer.toString(proposal.id).substring(Integer.toString(proposal.id).length()
                - 4)));
                return null;
            }

            // 1. init executor that will execute the callable
            ExecutorService executor = Executors.newSingleThreadExecutor();

            // 2. the callable that will check if the proposal id is valid and return a promise
            PrepareCallable prepareCallable = new PrepareCallable(proposal);
            FutureTask<Promise> futureTask = new FutureTask<>(prepareCallable);

            // 3. execute the task
            try {
                executor.submit(futureTask);
                // will return the promise
                return futureTask.get(10, TimeUnit.SECONDS);
            } catch (Exception e) {
                System.out.println("ERROR acceptor cannot return Promise");
                return null;
            }
        }

        @Override
        public Proposal accept( Proposal proposal) {

            // System.out.println(String.format("acceptor#[%d] is accepting id#%s %s(%d)", 
            // this.port, 
            // Integer.toString(proposal.id).substring(Integer.toString(proposal.id).length() - 4),
            // proposal.operation.opType,
            // proposal.operation.val ));
            if (Math.random() <= 0.1) {
                System.out.println(String.format("acceptor#[%d] random failure when accepting id#%s",
                        this.port,
                        Integer.toString(proposal.id).substring(Integer.toString(proposal.id).length() - 4),
                        proposal.operation.opType,
                        proposal.operation.val));
                return null;
            }

            // 1. init executor that will execute the callable
            ExecutorService executor = Executors.newSingleThreadExecutor();

            // 2. the callable that will check if the proposal id is valid and return an accpted proposal
            AcceptCallable acceptCallable = new AcceptCallable(proposal);
            FutureTask<Proposal> futureAccepting = new FutureTask<>(acceptCallable);

            // 3. execute the task
            try {
                executor.submit(futureAccepting);
                // will return the accepted proposal
                return futureAccepting.get(10, TimeUnit.SECONDS);

            } catch (Exception e) {
                System.out.println("ERROR acceptor cannot execute accepting");
                return null;
            }
        }


        /**
         * This method will called by Proposer and invoked by the acceptor
         * it will call the local putHelper method and commit to its keyValue map
         * will return a result object of the last learner
         * @param proposal
         * @return 
         */
        public Result learn(Proposal proposal) {
            // System.out.println(String.format("acceptor#[%d] is learning id#%s", this.port, 
            // Integer.toString(proposal.id).substring(Integer.toString(proposal.id).length() - 4)));


            KeyValOperation opToCommit = proposal.operation;
            Result commitResult = new Result();
            String command = "no command";

            if (opToCommit.opType == OperationType.PUT) {
                // 2. init result and command
                command = "put";

                // 3. Call your internal put method
                commitResult = putHelper(opToCommit.key, opToCommit.val, opToCommit.reqId);
                

            } else if (opToCommit.opType == OperationType.DELETE) {
                // 2. init result and command
                command = "delete";

                // 3. Call your internal put method here, e.g., put(op.key, op.value)
                commitResult = deleteHelper(opToCommit.key, opToCommit.reqId);
                opToCommit.val = -1;
                
            } else {
                commitResult.status = "ERROR";
                commitResult.value = 0;
                commitResult.msg = "invalid command";
                commitResult.reqId = opToCommit.reqId;
            }

            // 4. print result of put operation by this replica
            System.out.print("------");
            printLog(opToCommit.getKey(),
                    opToCommit.val,
                    opToCommit.reqId,
                    command,
                    commitResult.status,
                    commitResult.msg,
                    opToCommit.clientIp,
                    opToCommit.clientPort);
            return commitResult;

            
        }

        @Override
        public Result put(int key, int val, String reqId, String clientIp, int clientPort) throws TException {


            String command = "put";
            
            // 1. init operation
            KeyValOperation operation = new KeyValOperation(OperationType.PUT, key, val, reqId, clientIp, clientPort);
            
            // 2. generate new proposal
            Proposal newProposal = ProposalExtended.generateProposal(operation);
            
            // 3. get consesnsus for this proposal. 
            // it will return the commit result of only its own port
            // doesn't care if others
            ConsensusResult consRes = proposerRole.getConsensus(newProposal);
            Result commitResult = consRes.proposerCommitResult;

            printLog(key, val, reqId, command, commitResult.status, commitResult.msg, clientIp, clientPort);
            
            return commitResult;
        }
        
        @Override
        public Result delete(int key, String reqId, String clientIp, int clientPort) throws TException {
            String command = "delete";

            // 1. init operation
            KeyValOperation operation = new KeyValOperation(OperationType.PUT, key, -1, reqId, clientIp, clientPort);

            // 2. generate new proposal
            Proposal newProposal = ProposalExtended.generateProposal(operation);

            // 3. get consesnsus for this proposal.
            // it will return the commit result of only its own port
            // doesn't care if others
            ConsensusResult consRes = proposerRole.getConsensus(newProposal);
            Result commitResult = consRes.proposerCommitResult;

            printLog(key, -1, reqId, command, commitResult.status, commitResult.msg, clientIp, clientPort);
            return commitResult;
        }

        @Override
        public Result get(int key, String reqId, String clientIp, int clientPort) throws TException {
            String command = "get";
            Result result = new Result();
            result.reqId = reqId;

            //  1. validate
            try {
                validateKey(key, command);
                validateString(reqId);
            } catch (IllegalArgumentException e) {
                result.msg = e.getMessage();
                result.status = "ERROR";
                result.value = 0;
                
            }
            // System.out.print("\nbefore get>>>>>");
            // System.out.println(keyVal);

            // 2. execute and record result
            int val = keyVal.get(key);
            result.msg = "op successful";
            result.status = "OK";
            result.value = val;

            //  3. print and return result
            printLog(key, -1, reqId, command, result.status, result.msg, clientIp, clientPort);
            return result;
        }

       
        /**
         * 2.Print log on server side
         * @param key   the key requested
         * @param val   the value returned or requested
         * @param reqId the request Id generated by client
         * @param op    the operation command
         * @param msg   the message generated by each command execution
         */
        private void printLog(int key, int val, String reqId, String op, String status, String msg, String clientIp, int clientPort) {
            String currentTimestamp = getDate();
            String reqId4 = reqId.substring(Math.max(reqId.length() - 4, 0)); // get last 5char only
            // put request
            if (val != -1) {
                // put request
                System.out.println(
                        String.format(
                                "[%s] Replica#%d received %s key=%d, val=%d, reqId=...%s from client ip=%s port=%d msg=%s",
                                currentTimestamp,
                                this.port,
                                op, key, val,
                                reqId4,
                                clientIp, clientPort,
                                msg));
                
                // delete and get req
            } else {
                System.out.println(
                        String.format("[%s] Replica#%d received %s reqId=...%s from client ip=%s port=%d for key=%d, msg=%s",
                                currentTimestamp,
                                this.port,
                                op, reqId4,
                                clientIp, clientPort,
                                key,
                                msg));
            }
            System.out.println(keyVal);
            
        }

        private String getDate() {
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");
            long timestamp = System.currentTimeMillis();
            String currentTimestamp = formatter.format(new Date(timestamp));
            return currentTimestamp;
        }

        private boolean validateKey(int key, String command) throws IllegalArgumentException {
            if (key < 0) {
                throw new IllegalArgumentException("Illegal: key cannot be negative");
            } else if (!"put".equals(command) && !keyVal.containsKey(key)) {
                throw new IllegalArgumentException("Illegal: key does not exist");
            }
            return true;
        }

        private boolean validateValue(int value) throws IllegalArgumentException {
            if (value < 0) {
                throw new IllegalArgumentException("Illegal: value cannot be negative");
            }
            return true;
        }

        private boolean validateString(String str) throws IllegalArgumentException {
            if ("".equals(str) || str == null) {
                throw new IllegalArgumentException("string argument cannot be empty");
            }
            return true;

        }
    }

    public void start() {
        try {
            // 1. init socket
            TServerTransport serverTransport = new TServerSocket(port);

            // 2A create procesor
            KeyValueService.Processor processor = new KeyValueService.Processor<>(new KeyValueServiceDefault(port, replicaPorts));

            // 3. set server args
            TThreadPoolServer.Args serverArgs = new TThreadPoolServer.Args(serverTransport);
            serverArgs.processor(processor);

            // 4. create server
            TThreadPoolServer server = new TThreadPoolServer(serverArgs);

            System.out.println("Starting the server on port " + port + "...");
            server.serve();

        } catch (TTransportException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}