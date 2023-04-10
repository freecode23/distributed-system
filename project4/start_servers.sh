#!/bin/zsh

# Step 1. Compile and put all class files to bin folder
javac -cp .:./lib/libthrift.jar:./lib/slf4j.jar -d bin/ *.java


# Step 2. Run server. Set to look for the jar file lib directory and class files in bin directory.
ports=(9000 9001 9002 9003 9004)

for port in $ports; do
  other_servers=("${ports[@]/$port}")
  echo $'\nStarting server on port $port with other servers ${other_servers[@]}'
  java -cp ./bin:./lib/libthrift.jar:./lib/slf4j.jar ServerDriver $port ${other_servers[@]} &
  sleep 1
done

# Wait for all background jobs to complete
wait