#!/bin/zsh

# Step 1. Compile java files from services dir and put all class files to  bin/services folder
# javac -cp <./dependcy_path1>:<./dependcy_path2>:<./dependcy_path3> -d <output_path> <input_path>/*.java
javac -cp ./lib/libthrift.jar:./lib/slf4j.jar -d bin/services services/*.java

# Step 2. Compile java files from server dir and put all class files to  bin/server folder
javac -cp ./services:./lib/libthrift.jar:./lib/slf4j.jar -d bin/server server/*.java

# Step 3. Run server. Set to look for the jar file lib directory and class files in bin directory.
ports=(9000 9001 9002 9003 9004)

for port in $ports; do
  other_servers=("${ports[@]/$port}")
  echo
  echo $"\nStarting server on port $port with other servers ${other_servers[@]}"
    java -cp ./bin/server:./bin/services:./lib/libthrift.jar:./lib/slf4j.jar ServerDriver $port ${other_servers[@]} &
  sleep 1
done

# Wait for all background jobs to complete
wait