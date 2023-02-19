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

        private Map<Integer, Integer> keyVal = new HashMap<>();

        @Override
        public int add(int num1, int num2) throws TException {
            return num1 + num2;
        }

        @Override
        public int subtract(int num1, int num2) throws TException {
            return num1 - num2;
        }

        @Override
        public int multiply(int num1, int num2) throws TException {
            return num1 * num2;
        }

        @Override
        public double divide(double num1, double num2) throws TException {
            if (num2 == 0) {
                throw new TException("Cannot divide by zero");
            }
            return num1 / num2;
        }
    }

    public static void main(String[] args) {
        try {
            TServerTransport serverTransport = new TServerSocket(9090);
            Command.Processor processor = new Command.Processor<>(new CommandHandler());
            TServer server = new TSimpleServer(new TServer.Args(serverTransport).processor(processor));
            System.out.println("Starting the server...");
            server.serve();
        } catch (TTransportException e) {
            e.printStackTrace();
        }
    }
}