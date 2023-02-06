To make the connection start the server first before the client
Step 1. Compile the server and client code:
inside the server directory do:
javac -cp ../lib/gson-2.10.1.jar *.java
repeat inisde the client directory. Make sure you have the jar file inside the lib directory

Step 2. Run the server using TCP:
java -cp .:../lib/gson-2.10.1.jar ServerDriver <port number> <0>
where 3200 is the port number and 0 for tcp or 1 for udp

example: 
java -cp .:../lib/gson-2.10.1.jar ServerDriver 3200 0

Step 3. Run the client using TCP:
java -cp .:../lib/gson-2.10.1.jar ClientDriver <ip address> <port number > <0>

example:
java -cp .:../lib/gson-2.10.1.jar ClientDriver 127.0.0.1 3200 0

Step 4. Run the client using UDP:
To run both client and server using UDP just change the last argument to 1 or anything that is not 0.

example:
java -cp .:../lib/gson-2.10.1.jar ServerDriver 3200 1
java -cp .:../lib/gson-2.10.1.jar ClientDriver 127.0.0.1 3200 1


