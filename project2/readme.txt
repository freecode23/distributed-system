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
java -cp ./bin:./lib/libthrift.jar:./lib/slf4j.jar CommandServer 9090

Step 4. Run client add the host address and the port number as argument
e.g:
java -cp ./bin:./lib/libthrift.jar:./lib/slf4j.jar CommandClient 127.0.0.1 9090


Executive Summary
Assignment Overview
The goal of this project is to replace our basic TCP/UDP client server communication with RPC communication. There are several advantage of this:

1. Abstraction
RPC provides a more structured way of communication between a client and server. In our previous simple client-server model, the client sends a string command to the server, which then processes the command and returns the result as a string. This approach can be error-prone and less flexible, as the client and server need to agree on a protocol for encoding and decoding the commands and results. 

2. Not needing to create a new thread for every client request
Previously, in the simple client-server TCP model, when the server is multithreaded, it can handle multiple client connections simultaneously by creating a new thread for each incoming client request. However, each thread is responsible for handling the entire request, including parsing the request and generating a response.

In contrast, in an RPC implementation, a thread is not generated for each incoming client request. Instead, a thread pool is created with a fixed number of threads (default is 5) that can handle multiple client requests concurrently. When a new client request comes in, it is assigned to one of the available threads in the thread pool. This allows the server to handle multiple client requests simultaneously without having to create a new thread for each request, which can be resource-intensive and cause performance issues.

There are also additional requirements that we need to implement for this project. The server needs to be able to handle concurrent client requests. Due to this, the server now also needs to also handle mutual exclusion to avoid deadlock, and race conditions which could lead to incorrect results. Mutual exclusion also ensures that we can prevent inconsistent data. Without proper synchronization, multiple clients could read or write to the same data concurrently, leading to inconsistent data and incorrect results.


Technical Impression:
1. Thrift
I decided to use Apache Thrift as I want to learn a framework that can be used for multiple languages which is one of the major reasons why we use RPC in the first place. 
It also supports a range of network protocols (e.g., TCP, HTTP, etc.) whereas RMI is tied to Java Remote Method Protocol (JRMP) and Java Naming and Directory Interface (JNDI). To use thrift, I first have to download the thrift binaries in my local machine then build the java’s jar file using gradle. 

I then just need to create the interface for the Result object that I want the server to return to the client as well as the interface for the methods which is called ‘service’ in Thrift. It’s as simple as this:
struct Result {
  1: string reqId
  2: i32 value,
  3: string msg,
}

service Command {
  Result put(1: i32 key, 2: i32 value, 3: string reqId),
  Result get(1: i32 key, 2: string reqId),
  Result delete(1: i32 key, 2: string reqId)
}

Once we are done configuring our thrift service and struct, we can use it to auto generate Result.java and Command.java files.
Now we can implement the Command service in our Command server class and then just compile our server and client file and make sure we include all the jar files needed.
Compiling them will create over 50 class files that typically include classes that implement the server and client interfaces defined in the IDL file, classes for serializing and deserializing the data sent over the network, and classes for handling network connections and communication.

This makes it much easier for me as I don’t have to worry about the low level implementation of the UDP / TCP server and start the server within 4 lines of code. 
It even has thread pool implementation that allows us to avoid creating new thread every time for every new client request as mentioned above. Creating a threadpool and serving it only requires another two lines of code

2. ConcurrentHashmap vs lock
In handling mutual exclusion, there are two ways that I can take and both are from Java built-in libraries; concurrentHashmap or lock. I choose concurrentHashmap for a couple of reasons: 
Fine-grained locking: ConcurrentHashMap achieves concurrency by dividing the data structure into segments, each of which can be locked independently. This allows multiple threads to read and write to different segments of the map simultaneously, which can result in better performance than a lock-based approach where all threads contend for a single lock.
Read-only operations are lock-free: ConcurrentHashMap provides read-only operations that are lock-free, which means that they don't require any synchronization overhead. This can be useful in scenarios where reads are much more frequent than writes.

3. No need JSON
In our simple UDP/TCP implementation, we had to first convert the response hashmap from the server as a Json string before sending it to the client. And the client has to do its own deserialization using the Gson library. 
In our RPC’s implementation, the serialization and deserialization of data is performed automatically by the generated code when we compile Result.java and Command.java, so us as a programmer does not need to worry about the details of serialization and deserialization.
Further, Thrift's serialization and deserialization can be more efficient than Gson's, especially for larger and more complex data structures. Thrift is specifically designed for high-performance and efficient serialization and deserialization, and includes features like compact binary encoding and protocol-level optimizations.

4. Clear responsibility, server doesn’t need to re-validate command string
In our simple UDP/TCP implementation, since we are sending the string command to the server and let the server deal with parsing the command, we had to validate the command both on the server and client’s side. 

With RPC, there is no need to do this as the client is allowed to call the method directly and not have to send a JSON string. 

5. Cannot get client’s ip and port
I was not able to find a way to get the client’s ip and port. The stackoverflow posts on thrift are over around 10 years old. This could be one of the drawbacks with easy to use libraries with everything given to us. 
