import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

public class CommandClient {
    public static void main(String[] args) {
        try {
            TTransport transport = new TSocket("localhost", 9090);
            transport.open();
            TBinaryProtocol protocol = new TBinaryProtocol(transport);
            Command.Client client = new Command.Client(protocol);

            int key = 1;
            int val = 1;

            Result res= client.put(key, val);
            System.out.printf("Put %d and %d is %s\n", key, val, res.msg);

            res = client.get(key);
            System.out.printf("Get %d is %s\n", key, res.msg);

            res = client.delete(key);
            System.out.printf("Delete %d is %s\n", key, res.msg);

            transport.close();
        } catch (TTransportException ex) {
            ex.printStackTrace();
        } catch (TException ex) {
            ex.printStackTrace();
        }
    }
}