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