version:
libthrift-0.18.0.jar
slf4j-api-2.0.6.jar

1. generate java files from thrift files:
thrift --gen java -out ./ Command.thrift

2. compile and put all classes to bin folder
javac -cp .:./lib/libthrift.jar:./lib/slf4j.jar -d bin/ *.java


3. run and look in bin and lib folder. note 9090 is the chosen port number
java -cp ./bin:./lib/libthrift.jar:./lib/slf4j.jar CommandServer 9090
java -cp ./bin:./lib/libthrift.jar:./lib/slf4j.jar CommandClient 9090