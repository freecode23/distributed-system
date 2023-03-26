PRERQUISITES:
jar file needed and the version:
libthrift-0.18.0.jar
slf4j-api-2.0.6.jar

Step 1. Generate java files from thrift files (OPTIONAL)
You need to have thrift installed to do this. If the java file is already generated you can skip this step
thrift --gen java -out ./ Command.thrift

Step 2. Compile and put all class files to bin folder
javac -cp .:./lib/libthrift.jar:./lib/slf4j.jar -d bin/ *.java

Step 3. Run server. Set to look for the jar file lib directory and class files in bin directory.
note 9090 is the chosen port number for server
e.g:
java -cp ./bin:./lib/libthrift.jar:./lib/slf4j.jar CommandServer

Step 4. Run client add the host address and the port number as argument
e.g:
java -cp ./bin:./lib/libthrift.jar:./lib/slf4j.jar CommandClient 127.0.0.1


Executive Summary
Assignment Overview
The objective of this project is to enhance our system by creating five replica servers, enabling clients to make RPC calls to any of the servers and receive consistent data. This distributed architecture offers several benefits, such as improved scalability. With multiple servers, the system can handle a higher volume of incoming requests by distributing the load among the replicas.

This approach also provides fault tolerance. If one or more replica servers fail, the remaining servers can continue to process client requests, ensuring the system's overall availability.

However, extending the RPC to multiple servers also introduces some challenges. For instance, maintaining data consistency across replicas can be complex, particularly in scenarios involving concurrent writes and network latency. To address this, we implemented a distributed locking mechanism that ensures atomic updates and maintains strict consistency among the replicas.


Technical Impression:
0. Additional classes: 
- ServerDriver: 
Now instead of running a single server we have a ServerDriver class that declares all the ports for the 5 servers. It will then initialize this servers with each having its own port number and it will start the server. These 5 servers are wrapped undera class ReplicatedServer. 
- ReplicatedServer: 
It's a class that contains a list of the 5 servers (a CommandServer object) and has public method startServers() that will be called by the ServerDriver object above.

1. RPC Implementation variables:
Our CommandHandler class which is the RPC service implementation now holds several other variables other than the keyValue store itself. The additional variables are:
- replicaPorts: These are the list of ports of the other servers that the client can connect to
- Object[] locks : This is an array of locks that the server will use to ensure consistency across servers. The indices of the array represent the key that it will lock when it tries to operate on this key. When a server receives a request to perform an operation, it will first lock the key using the synchronized (locks[key]) block. If the key is already locked by another operation within this server, it will send a negative acknowledgment (NACK) with the message "Key is locked by another operation." 
- Set<Integer> lockedKeys: This is another data structure that we will used together with the locks array above. If the key is not used by another thread, we will add this key to the lockedKeys set to ensure that another thread will not use this. 
- Map<String, PreparedOperation> preparedOperations: This is a concurrentHashmap of operations that this server will need to execute. 

2. RPC Additional services:
- PrepareResult prepare(1: i32 key, 2: i32 value, 3: string command, 4: string reqId, 5: string clientIp, 6:i32 clientPort)

- CommitResult commit(1: string reqId)


