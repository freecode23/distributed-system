package project2sample;
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

            int num1 = 100;
            int num2 = 200;

            int sum = client.add(num1, num2);
            System.out.printf("Result of adding %d and %d is %d\n", num1, num2, sum);

            int diff = client.subtract(num1, num2);
            System.out.printf("Result of subtracting %d from %d is %d\n", num2, num1, diff);

            int prod = client.multiply(num1, num2);
            System.out.printf("Result of multiplying %d and %d is %d\n", num1, num2, prod);

            int quotient = (int)client.divide(num2, num1);
            System.out.printf("Result of dividing %d by %d is %d\n", num2, num1, quotient);

            transport.close();
        } catch (TTransportException ex) {
            ex.printStackTrace();
        } catch (TException ex) {
            ex.printStackTrace();
        }
    }
}