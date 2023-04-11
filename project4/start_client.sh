# Step 1: Compile client:
javac -cp ./bin/services/:./lib/libthrift.jar:./lib/slf4j.jar -d bin/client client/Client.java


# Step 2: Run client (include the current path since its where we put our client class file)
java -cp ./bin/client:./bin/services:./lib/libthrift.jar:./lib/slf4j.jar Client 127.0.0.1