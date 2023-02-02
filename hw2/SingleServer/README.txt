To make the connection start the server first before the client
Step 1. Compile the server adn client code:
javac ServerSingleThread.java
javac ClientSingleThread.java

Step 2. Run the server:
java ServerSingleThread <port number>

example: 
java ServerSingleThread 3200

Step 4. Run the client:
java ClientSingleThread <ip address> <port number>

example:
java ClientSingleThread 10.5.50.217 3200

Note on mac you can get the ip address by running:
ifconfig en0 | grep inet
it will return :
inet 10.5.50.217 netmask 0xffffff00 broadcast 10.5.50.255
use `10.5.50.217` 

Step 5. Enter some text in client's terminal to get a response
of the reversed text from the server.