import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransportException;

import java.util.HashMap;
import java.util.Map;

public class CommandServer {

    public static class CommandHandler implements Command.Iface {

        //  1. init keyvalue store
        private Map<Integer, Integer> keyVal = new HashMap<>();

        //  2. ovveride the command we wrote in command thrift file
        @Override
        public Result put(int key, int val) throws TException {
            // 1. add to hashmap stored by ServerObj
            keyVal.put(key, val);

            System.out.print("after put>>>>>");
            System.out.println(keyVal);

            // 2. init return resultStruct and return to client
            Result result = new Result();
            result.msg = "OK";
            result.value = val;
            return result;
        }

        @Override
        public Result get(int key) throws TException {
            System.out.print("before get>>>>>");
            System.out.println(keyVal);

            // 1. init return obj
            Result result = new Result();
            if (!keyVal.containsKey(key)) {

                // - add message
                result.msg = "KEY NOT FOUND";

            } else {

                // - get value
                int val = keyVal.get(key);

                // - add msg and value to return object
                result.msg = "OK";
                result.value = val;

                System.out.print("after gett>>>>>");
                System.out.println(keyVal);
            }
            return result;
        }

        @Override
        public Result delete(int key) throws TException {
 
            System.out.print("before del>>>>>");
            System.out.println(keyVal);

            Result result = new Result();

            if (!keyVal.containsKey(key)) {
                // add error mesg
                result.msg = "KEY NOT FOUND";

            } else {

                // - remove
                keyVal.remove(key);
                System.out.print("after del>>>>>");
                System.out.println(keyVal);

                // add error msg
                result.msg = "OK";

            }
            return result;
        }

    
    }

    public static void main(String[] args) {
        try {
            // 1. init socket
            TServerTransport serverTransport = new TServerSocket(9090);

            // 2. 
            Command.Processor processor = new Command.Processor<>(new CommandHandler());
            TServer server = new TSimpleServer(new TServer.Args(serverTransport).processor(processor));
            System.out.println("Starting the server...");
            server.serve();
        } catch (TTransportException e) {
            e.printStackTrace();
        }
    }
}