PRERQUISITES:
jar file needed and the version:
libthrift-0.18.0.jar
slf4j-api-2.0.6.jar

Step 1. Generate java files from thrift files (OPTIONAL)
You need to have thrift installed to do this. If the java file is already generated you can skip this step
thrift --gen java -out ./ KeyValueService.thrift

Step 2. Compile and put all class files to bin folder
javac -cp .:./lib/libthrift.jar:./lib/slf4j.jar -d bin/ *.java

Step 3. Run server. Set to look for the jar file lib directory and class files in bin directory.
e.g:
java -cp ./bin:./lib/libthrift.jar:./lib/slf4j.jar ServerDriver

Step 4. Run client add the host address
e.g:
java -cp ./bin:./lib/libthrift.jar:./lib/slf4j.jar CommandClient 127.0.0.1


Executive Summary
Assignment Overview:
The objective of this project is to enhance our system by creating five replica servers, enabling clients to make RPC calls to any of the servers and receive consistent data. This distributed architecture offers several benefits, such as improved scalability. With multiple servers, the system can handle a higher volume of incoming requests by distributing the load among the replicas.

This approach also provides fault tolerance. If one or more replica servers fail, the remaining servers can continue to process client requests, ensuring the system's overall availability.

However, extending the RPC to multiple servers also introduces some challenges. For instance, maintaining data consistency across replicas can be complex, particularly in scenarios involving concurrent writes and network latency. To address this, we implemented a distributed locking mechanism that ensures atomic updates and maintains strict consistency among the replicas.


Technical Impression:
0. Additional classes: 
- ServerDriver: 
Now instead of running a single server we have a ServerDriver class that declares all the ports for each of the 5 servers. It will then initialize these servers and start them. These 5 servers are wrapped under the class ReplicatedServer. 
- ReplicatedServer: 
It's a class that contains a list of the 5 servers (a CommandServer object) and has public method startServers() that will be called by the ServerDriver object above.

1. RPC Implementation variables:
Our CommandHandler class which is the RPC service implementation now holds several other variables other than the keyValue store itself. The additional variables are:
- replicaPorts: These are the list of ports of the other servers that the client can connect to
- Object[] locks : This is an array of locks that the server will use to ensure consistency across servers. The indices of the array represent the key that it will lock when it tries to operate on this key. When a server receives a request to perform an operation, it will first lock the key using the synchronized (locks[key]) block. If the key is already locked by another operation within this server, it will send a negative acknowledgment (NACK) with the message "Key is locked by another operation." 
- Set<Integer> lockedKeys: This is another data structure that we will used together with the locks array above. If the key is not used by another thread, we will add this key to the lockedKeys set to ensure that another thread will not use this. 
- Map<String, PreparedOperation> preparedOperations: This is a concurrentHashmap of operations that this server will need to execute or commit. 

2. RPC Additional services:
In addition to the put, delete, and get method that are services for the clients, we also need to add prepare, commit, and abort methods in our RPC services so that the selected coordinator server can call these remote functions. Here are the explanations for each of them:
- PrepareResult prepare(1: i32 key, 2: i32 value, 3: string command, 4: string reqId, 5: string clientIp, 6:i32 clientPort)
This function is used to prepare an operation on a given key with the specified value. It takes in the command (e.g., "put" or "delete"), a unique request ID, and the client's IP and port. It checks if the key is locked by another operation; if not, it locks the key and prepares the operation. The function returns a PrepareResult that contains the status of the preparation (OK or KEY_LOCKED) and a message indicating the result.
- CommitResult commit(1: string reqId)
This function is called to commit a previously prepared operation identified by the provided request ID. It executes the operation (e.g., put or delete) on the key-value store and releases the lock on the key. It then returns a CommitResult that contains the status of the commit (SUCCESS, KEY_NOT_FOUND, or OPERATION_NOT_PREPARED) and a message indicating the result.
- void abort(1: string reqId)
This function is used to abort a previously prepared operation identified by the provided request ID. It cancels the operation, releases the lock on the key, and removes the prepared operation from the server. This can be used in scenarios where an operation needs to be canceled due to another server replica is already operating on the same key it wants to operate on

3. How it all works:
Now that we have defined all of the changes lets take an example of what happen when a client decide to make "put 1 1" request to server at port 9000. Let's call this coordinator server9000:
    1. Client will call the remote "put" method of server9000. 
    2. server9000 will prepare all the other replicas (server9001 to server 9004). This is to say that it will call the prepare() function of the other 4 servers and pass them the request id, key, value, and other info needed for them to perform this operation for their own keyValue store. 
    3. Each of the 4 replica servers will then check if the key that server9001 wants to operate on is already locked. If it's already locked it will send a NACK and server9001 will simply abort the whole "put 1 1" operation. If it's not already locked, the replica will lock this key and create a PreparedOperation object, add this object to its own list of preparedOperations for it to commit later on.
    4. If server9000 receive ACKs from all of the replicas at the prepareReplicas(), it will go to the next step which is commitReplicas()
    5. In commitReplicas(), server9000 will call every replica's commit() remote function to execute the operation with the requestID it sends. In this case the requestId will correspond to the "put 1 1" operation. 
    6. All the replicas (server9001 to server9004) will grab this PreparedOperation object from hashmap and perform the operation using its local putHelper method. Once this operation is done, it will unlock this key so it can be used by other operations. 
    7. If all the replicas successfully committed this operation, the chosen coordinator server9001 will also call a local putHelper() method to put 1 1 to its own keyValue store. It will then print a log and return the result object as before to the client. 
    8. If any of the replicas cannot commit to this operation it will abort this operation and send an error message back to the client

4. Conclusion:
With this 2 phase commit method we are able to maintain consistency of our keyvalue store across servers. However there are some potential single points of failure. If the server that initiates the prepare and commit/abort process fails, the operation might not complete, and other replicas might be left waiting for commit or abort instructions. We can mitigate these risks by implementing a more fault-tolerant consensus algorithm, like the Paxos. This algorithm is designed to handle failures and ensure consistent operations across multiple replicas.

