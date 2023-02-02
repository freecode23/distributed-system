To make the connection start the server first before the client
Step 1. Compile the server adn client code:
javac ServerMultiThread.java
javac ClientMultiThread.java

Step 2. Run the server:
java ServerMultiThread <port number>

example: 
java ServerMultiThread 3200

Step 4. Run the client:
java ClientMultiThread <ip address> <port number>

example:
java ClientMultiThread 10.5.50.217 3200

Note on mac you can get the ip address by running:
ifconfig en0 | grep inet
it will return :
inet 10.5.50.217 netmask 0xffffff00 broadcast 10.5.50.255
use `10.5.50.217` 

Step 5. Enter some text in client's terminal to get a response
of the reversed text from the server.