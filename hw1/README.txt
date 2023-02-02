To make the connection start the server first before the client
Step 1. To compile the server code:
javac TCPServer.java

Step 2. To run the server:
java TCPServer <port number>

example: 
java TCPServer 3200

Step 3. To compile the client code:
javac TCPClient.java

Step 4. To run the client:
java TCPClient <ip address> <port number>

example:
java TCPClient 10.5.50.217 3200

Step 5. Enter some text in client's terminal to get a response
of the reversed text from the server.