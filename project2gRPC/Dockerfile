FROM openjdk:8-jdk-alpine

RUN apk --no-cache add curl
RUN apk --no-cache add build-base
RUN apk --no-cache add git
RUN curl -Lo /usr/bin/protoc https://github.com/protocolbuffers/protobuf/releases/download/v3.10.0/protoc-3.10.0-linux-x86_64.zip && \
    unzip /usr/bin/protoc -d /usr/bin/ && \
    chmod +x /usr/bin/protoc && \
    mkdir -p /app/src/main/proto

COPY . /app

WORKDIR /app

RUN git clone https://github.com/grpc/grpc-java.git
RUN git clone https://github.com/grpc/grpc-java.git /usr/src/grpc-java

WORKDIR /usr/src/grpc-java/compiler
RUN ../gradlew java_pluginExecutable

WORKDIR /app

RUN mkdir -p /usr/include/grpc && \
    cp -r /usr/src/grpc-java/compiler/build/exe/java_plugin/ /usr/include/grpc/ && \
    cp -r /usr/src/grpc-java/compiler/build/exe/java_plugin/ /usr/bin/ && \
    cp -r /usr/src/grpc-java/compiler/build/exe/java_plugin/ /usr/local/bin/ && \
    rm -rf /usr/src/grpc-java

RUN protoc --proto_path=/app/src/main/proto --java_out=/app/src/main/java --grpc-java_out=/app/src/main/java /app/src/main/proto/*.proto

RUN ./gradlew build

EXPOSE 50051

CMD ["/bin/bash"]
