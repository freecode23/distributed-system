To make the connection start the server first before the client
Step 1. Compile the server and client code:
inside the server directory do:
javac -cp ../lib/gson-2.10.1.jar *.java

repeat for the client


Step 2. Run the server using tcp:
java -cp .:../lib/gson-2.10.1.jar ServerDriver 3200 0
where 3200 is the port number and 0 stands for tcp

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