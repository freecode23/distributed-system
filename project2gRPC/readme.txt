# generate gRPC client and server stubs for your key_value.proto file:
protoc --proto_path=. --java_out=src --grpc-java_out=src key_value.proto
