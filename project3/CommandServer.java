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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
public class CommandServer {

    private final int port;
    private int[] replicaPorts;

    public CommandServer(int port, int[] replicaPorts) {
        this.port = port;
        this.replicaPorts = replicaPorts;
        
    }
    
    public static class CommandHandler implements Command.Iface {

        private final int port;
        //  1. init keyvalue store
        private Map<Integer, Integer> keyVal = new ConcurrentHashMap<>();

        // 2. list of TTransport to manage communication with other servers
        private List<TTransport> replicaTransports;

        // 3. Array of locks that this server will use. the index represent the key that it will lock
        private Object[] locks = new Object[1000]; 

        // 4. Set of keys that are locked
        private Set<Integer> lockedKeys = Collections.synchronizedSet(new HashSet<>());

        // 5. Operations that this server needs to operate on
        private Map<String, PreparedOperation> preparedOperations = new ConcurrentHashMap<>();


        /**
         * 1. Constructor that will create a new TSocket for each replica server with a different port number than the current server instance
         * @param currPort the port that the current server belongs to
         * @param replicaPorts  array of port numbers representing all the replicated servers, including the current server instance.
         */
        public CommandHandler(int currPort, int[] replicaPorts) {

            // 1. Init TTransport for each replica so current server can communicate with others
            replicaTransports = new ArrayList<>();
            this.port = currPort;
            for (int replicaPort : replicaPorts) {
                if (replicaPort != currPort) {
                    try {
                        TSocket socket = new TSocket("localhost", replicaPort);
                        replicaTransports.add(socket);
                    } catch (TTransportException ex) {

                        // handle other TTransportException
                        System.out.println(ex.getMessage());

                    }
                }
            }

            // 2. Create locks for all of the key
            for (int i = 0; i < locks.length; i++) {
                locks[i] = i;
            }
        }
        
        // 2. Two phase commit helpers
        /**
         * Ensure that all replicas are ready and able to commit the operation.
         * it will call the RPC prepare on each replicas
         */
        private boolean prepareReplicas(int key, int value, String reqId) {

            // 0. for each replica
            for (TTransport transport : replicaTransports) {
                try {
                    // 1. init new request to other replicas
                    transport.open();
                    TBinaryProtocol protocol = new TBinaryProtocol(transport);
                    Command.Client replica = new Command.Client(protocol);

                    // 2. make sure no other replica is modifying this
                    PrepareResult prepResult = replica.prepare(key, value, reqId);

                    if (!"PREPARED".equals(prepResult.msg)) {
                        return false;
                    }
                } catch (TException e) {
                    e.printStackTrace();
                    return false;
                } finally {
                    transport.close();
                }
            }
            return true;
        }
        
        private boolean commitReplicas(int key, String reqId) {
            boolean allAcksReceived = true;

            // 0. for each replica
            for (TTransport transport : replicaTransports) {
                try {
                    transport.open();

                    TBinaryProtocol protocol = new TBinaryProtocol(transport);
                    Command.Client client = new Command.Client(protocol);

                    // Send commit message to the replica
                    CommitResult commitResult = client.commit(reqId);

                    // Check if the replica sent an ACK
                    if (!"ACK".equals(commitResult.msg)) {
                        allAcksReceived = false;
                    }

                } catch (TException e) {
                    allAcksReceived = false;
                    System.out.println("Error while sending commit message: " + e.getMessage());
                } finally {
                    transport.close();
                }
            }

            return allAcksReceived;
        }
        // 3. Override thrifts interface
        /**
         * A replica will prepare itself to perform the operation received by another
         * coordinator
         * It will tell the coordinator whether or not its performing an operatiion on
         * the same key
         * that the coordinator wants to perform
         * 
         * @param key the key
         * @param value the value to add if its a put request, otherwise its null
         * @param reqId the client request ID
         * @return prepResult PreppareResult object that has 3 status, OK, KEY_LOCKED, ERROR
         * @throws TException
         */
        @Override
        public PrepareResult prepare(int key, int value, String reqId) throws TException {
            PrepareResult prepResult = new PrepareResult();
            prepResult.reqId = reqId;

            try {
                // Lock the key for the upcoming operation
                synchronized (locks[key]) {
                    // If the key is not locked by another operation
                    if (!lockedKeys.contains(key)) {
                        // Lock the key
                        lockedKeys.add(key);

                        // init new operation
                        PreparedOperation op = new PreparedOperation(OperationType.PUT, key, value);

                        // add this to operations this server need to operate on
                        preparedOperations.put(reqId, op);

                        // Positive acknowledgment (ACK)
                        prepResult.status = PrepareStatus.OK;
                        prepResult.msg = "PREPARED";
                    } else {
                        // Negative acknowledgment (NACK) - key is locked
                        prepResult.status = PrepareStatus.KEY_LOCKED;
                        prepResult.msg = "Key is locked by another operation";
                    }
                }
            } catch (IllegalArgumentException e) {
                // Negative acknowledgment (NACK) - invalid key or value
                prepResult.status = PrepareStatus.ERROR;
                prepResult.msg = e.getMessage();
            }
            
            return prepResult;
        }

        public CommitResult commit(String reqId) throws TException {
            CommitResult result = new CommitResult();
            result.reqId = reqId;

            PreparedOperation op = preparedOperations.get(reqId);
            if (op != null) {
                if (op.operationType == OperationType.PUT) {
                    // Perform the PUT operation
                    // Call your internal put method here, e.g., put(op.key, op.value)
                } else if (op.operationType == OperationType.DELETE) {
                    // Perform the DELETE operation
                    // Call your internal delete method here, e.g., delete(op.key)
                }

                // Remove the prepared operation from the map
                preparedOperations.remove(reqId);

                // Unlock the key
                // If you are using lockedKeys Set or keyLocks array, unlock the key here

                result.msg = "ACK";
            } else {
                result.msg = "NACK";
            }

            return result;
        }

        private Result putHelper(int key, int val, String reqId, String clientIp, int clientPort) {
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

        @Override
        public Result put(int key, int val, String reqId, String clientIp, int clientPort) throws TException {

            Result result = new Result();
            String command = "put";

            // 2. Perform operation
            if (prepareReplicas(key, val, reqId)) {

                // -0 commit replicas

                // -1 perform local operation
                result = putHelper(key, val, reqId, clientIp, clientPort);


            } else {
                // -3 abortReplicas(key, val, reqId);
                result.status = "ERROR";
                result.msg = "Not acknowledged";
                result.value = 0;
            }

            // print and return result to client
            printLog(key, val, reqId, command, result.status, result.msg, clientIp, clientPort);
            return result;
        }

        @Override
        public Result get(int key, String reqId, String clientIp, int clientPort) throws TException {
            String command = "get";
            Result result = new Result();
            result.reqId = reqId;

            // try {
            //     // sleep for 5 seconds
            //     Thread.sleep(5000);
            // } catch (InterruptedException e) {
            //     e.printStackTrace();
            // }

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

        @Override
        public Result delete(int key, String reqId, String clientIp, int clientPort) throws TException {
            String command = "delete";
            Result result = new Result();
            result.reqId = reqId;

            // 1. validate
            try {
                validateKey(key, command);
                validateString(reqId);
            } catch (IllegalArgumentException e) {
                result.msg = e.getMessage();
                result.value = 0;
                result.status = "ERROR";
                printLog(key, -1, reqId, command, result.status, result.msg, clientIp, clientPort);
                return result;
            }
            // System.out.print("\nbefore del>>>>>");
            // System.out.println(keyVal);

            // 2. execute and record result
            // - remove
            int val = keyVal.get(key);
            keyVal.remove(key);
            result.status = "OK";
            result.msg = "op successful";
            result.value = val;
            
            //  3. print and return
            // System.out.print("after del>>>>>");
            // System.out.println(keyVal);
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
                                "[%s] Replica#%d received %s reqId=...%s from client ip=%s port=%d for key=%d, val=%d, msg=%s",
                                currentTimestamp,
                                this.port,
                                op, reqId4,
                                clientIp, clientPort,
                                key, val,
                                msg));

                // delete and get req
            } else {
                System.out.println(
                        String.format("[%s] Received %s reqId=...%s from client ip=%s port=%d for key=%d, msg=%s",
                                currentTimestamp,
                                op, reqId4,
                                clientIp, clientPort,
                                key,
                                msg));
            }
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