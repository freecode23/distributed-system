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

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
public class CommandServer {

    private final int port;
    private List<Integer>replicaPorts;


    public CommandServer(int port, List<Integer> replicaPorts) {
        this.port = port;
        this.replicaPorts = replicaPorts;
    }
    
    public static class CommandHandler implements Command.Iface {

        private final int port;
        private Map<Integer, Integer> keyVal = new ConcurrentHashMap<>();
        private PaxosCoordinator paxosCoordinator;


        /**
         * 1. Constructor that will create a new TSocket for each replica server with a different port number than the current server instance
         * @param currPort the port that the current server belongs to
         * @param replicaPorts  array of port numbers representing all the replicated servers, including the current server instance.
         */
        public CommandHandler(int currPort, List<Integer> replicaPorts) {

            this.port = currPort;
            List<Acceptor> acceptors = initializeAcceptors(replicaPorts);
            Learner learner = new Learner();
            this.paxosCoordinator = new PaxosCoordinator(acceptors, learner);
      
        }

        private List<Acceptor> initializeAcceptors(List<Integer> replicaPorts) {
            List<Acceptor> acceptors = new ArrayList<>();
            for (int port : replicaPorts) {
                acceptors.add(new Acceptor(port));
            }
            return acceptors;
        }


        
        @Override
        public Response prepare(Request request) {
            int proposalNumber = request.getProposalNumber();
            List<Promise> promises = paxosCoordinator.coordPrepare(proposalNumber);

            if (!promises.isEmpty()) {
                // Find the highest accepted proposal number among the promises
                int highestProposalNumber = -1;
                int acceptedValue = -1;

                for (Promise promise : promises) {
                    if (promise.getHighestProposalNumber() > highestProposalNumber) {
                        highestProposalNumber = promise.getHighestProposalNumber();
                        acceptedValue = promise.getAcceptedValue();
                    }
                }

                // If there are no accepted values, you may use a default value or decide on a
                // new value
                if (acceptedValue == -1) {
                    // Use a default value or decide on a new value
                    // Example: Use a random value as the new value
                    acceptedValue = new Random().nextInt();
                }

                return new Response(highestProposalNumber, acceptedValue);
            } else {
                return new Response(-1, -1); // Indicate that no promises were sent
            }
        }

        @Override
        public Response accept(Request request) {
            int proposalNumber = request.proposalNumber;
            int proposedValue = request.value;

            int acceptCount = paxosCoordinator.coordAccept(proposalNumber, proposedValue);
            
            Response response = new Response();
            if (acceptCount >= 3) {
                response.highestProposalNumber = proposalNumber;
                return response;
            } else {
                response.highestProposalNumber = -1;
                return response; // Indicate that the accept response was not sent
            }
        }

        @Override
        public void learn(Request request) {
            int proposalNumber = request.proposalNumber;
            int proposedValue = request.value;

            paxosCoordinator.coordLearn(new Proposal(proposalNumber, proposedValue));
        }

        private Result putHelper(int key, int val, String reqId) {
            Result result = new Result();
            result.reqId = reqId;
            result.value = val;
            String command = "put";

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

        @Override
        public Result put(int key, int val, String reqId, String clientIp, int clientPort) throws TException {

            Result result = new Result();
            String command = "put";

          

            // print and return result to client
            printLog(key, val, reqId, command, result.status, result.msg, clientIp, clientPort);
            return result;
        }
        
        @Override
        public Result delete(int key, String reqId, String clientIp, int clientPort) throws TException {
            Result result = new Result();
            String command = "delete";
   
           
            // print and return result to client
            printLog(key, -1, reqId, command, result.status, result.msg, clientIp, clientPort);
            return result;
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
         * 
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
            Command.Processor processor = new Command.Processor<>(new CommandHandler(port, replicaPorts));

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