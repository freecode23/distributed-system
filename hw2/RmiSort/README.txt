To make the connection start the server first before the client
Step 1. Compile server, client, and sort code:
javac Server.java
javac Client.java
javac Sort.java

Step 2. Run the registry:
rmiregistry

Step 3. On another terminal run the Server:
java Server

Step 4. On another terminal run the Client:
java Client