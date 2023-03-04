
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class KeyValueServer {
    private final int port;
    private final Server server;

    public KeyValueServer(int port) throws IOException {
        this(ServerBuilder.forPort(port), port);
    }

    public KeyValueServer(ServerBuilder<?> serverBuilder, int port) {
        this.port = port;
        server = serverBuilder.addService(new KeyValueServiceImpl())
                .build();
    }

    public void start() throws IOException {
        server.start();
        System.out.println("Server started, listening on port >>>" + port);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                KeyValueServer.this.stop();
                System.err.println("*** server shut down");
            }
        });
    }

    public void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    private static class KeyValueServiceImpl extends KeyValueServiceGrpc.KeyValueServiceImplBase {
        private Map<String, String> map = new ConcurrentHashMap<>();

        @Override
        public void put(PutRequest request, StreamObserver<PutResponse> responseObserver) {
            map.put(request.getKey(), request.getValue());
            responseObserver.onNext(PutResponse.newBuilder().setSuccess(true).build());
            responseObserver.onCompleted();
        }

        @Override
        public void get(GetRequest request, StreamObserver<GetResponse> responseObserver) {
            String value = map.get(request.getKey());
            if (value != null) {
                responseObserver.onNext(GetResponse.newBuilder().setValue(value).build());
            }
            responseObserver.onCompleted();
        }

        @Override
        public void delete(DeleteRequest request, StreamObserver<DeleteResponse> responseObserver) {
            boolean success = map.remove(request.getKey()) != null;
            responseObserver.onNext(DeleteResponse.newBuilder().setSuccess(success).build());
            responseObserver.onCompleted();
        }
    }

    public static void main(String[] args) throws Exception {
        KeyValueServer server = new KeyValueServer(50051);
        server.start();
        server.blockUntilShutdown();
    }

    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }
}
