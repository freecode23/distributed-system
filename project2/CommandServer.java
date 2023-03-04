import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.server.TThreadPoolServer.Args;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.text.SimpleDateFormat;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Date;
import java.util.Map;
public class CommandServer {


    public static class CommandHandler implements Command.Iface {

        //  1. init keyvalue store
        private Map<Integer, Integer> keyVal = new ConcurrentHashMap<>();

        // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        // private TTransport transport;

        // public CommandHandler(TTransport transport) {
        //     this.transport = transport;
        // }

        private void printLog(int key, int val, String op, String msg) {
            String currentTimestamp = getDate();

            // put request
            if (val != -1) {
                System.out.println(
                        String.format("[%s] Received %s request from client for key=%d, val=%d, msg=%s", currentTimestamp,
                        op, key, val,
                        msg));

            // delete and get req
            } else {
                System.out.println(
                        String.format("[%s] Received %s request from client for key=%d, msg=%s",
                                currentTimestamp,
                                op, key,
                                msg));
            }
            // // Get client IP address and port number
            // SocketAddress clientAddr = ((TSocket) transport).getSocket().getRemoteSocketAddress();
            // String clientIp = ((InetSocketAddress) clientAddr).getAddress().getHostAddress();
            // int clientPort = ((InetSocketAddress) clientAddr).getPort();
            // System.out.println(
            //         String.format("Received %s request from client %s:%d for key=%d, value=%d", command, clientIp,
            //                 clientPort, key, val));
        }
        // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<


        public String getDate() {
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

        //  2. override the command we wrote in command thrift file
        @Override
        public Result put(int key, int val) throws TException {
            String command = "put";
            try {
                validateKey(key, command);
                validateValue(val);
            } catch (IllegalArgumentException e) {
                printLog(key, val, command, e.getMessage());
                Result result = new Result();
                result.msg = "ERROR";
                result.value = val;
                return result;
            }
            
            // 1. add to hashmap stored by ServerObj
            // System.out.print("\nbefore put>>>>>");
            // System.out.println(keyVal);
            
            // 2. execute and record result
            keyVal.put(key, val);
            Result result = new Result();
            result.msg = "OK";
            result.value = val;

            // 3. print and return result
            // System.out.print("after put>>>>>");
            // System.out.println(keyVal);
            printLog(key, val, "put", "op successful");
            return result;
        }

        @Override
        public Result get(int key) throws TException {
            String command = "get";

            //  1. validate
            try {
                validateKey(key, command);
            } catch (IllegalArgumentException e) {
                printLog(key, -1, command, e.getMessage());
                Result result = new Result();
                result.msg = "ERROR";
                result.value = 0;
                return result;
            }
            // System.out.print("\nbefore get>>>>>");
            // System.out.println(keyVal);

            // 2. execute and record result
            Result result = new Result();
            int val = keyVal.get(key);
            result.msg = "OK";
            result.value = val;

            //  3. print and return result
            // System.out.print("after gett>>>>>");
            // System.out.println(keyVal);
            printLog(key, -1, command, "op successful");
            return result;
        }

        @Override
        public Result delete(int key) throws TException {
            String command = "delete";

            // 1. validate
            try {
                validateKey(key, command);
            } catch (IllegalArgumentException e) {
                printLog(key, -1, "delete", e.getMessage());
                Result result = new Result();
                result.msg = "ERROR";
                result.value = 0;
                return result;
            }
            // System.out.print("\nbefore del>>>>>");
            // System.out.println(keyVal);

            // 2. execute and record result
            Result result = new Result();
            // - remove
            keyVal.remove(key);
            result.msg = "OK";
            
            //  3. print and return
            // System.out.print("after del>>>>>");
            // System.out.println(keyVal);
            printLog(key, -1, command, "op sucessful");
            return result;
        }

    }

    public static void main(String[] args) {
        try {
            if (args.length != 1) {
                System.out.println(
                        "please enter the argument of the port number");
                return;
            }

            // 1. init socket
            int port = Integer.parseInt(args[0]);
            TServerTransport serverTransport = new TServerSocket(port);

            // 2A create procesor
            Command.Processor processor = new Command.Processor<>(new CommandHandler());
            
            // 3. set server args
            TThreadPoolServer.Args serverArgs = new TThreadPoolServer.Args(serverTransport);
            serverArgs.processor(processor);

            // 4. create server
            TThreadPoolServer server = new TThreadPoolServer(serverArgs);

            System.out.println("Starting the server...");
            server.serve();

            
        } catch (TTransportException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}