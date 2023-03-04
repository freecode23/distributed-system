version:
libthrift-0.18.0.jar
slf4j-api-2.0.6.jar

compile:
javac -cp .:./lib/libthrift.jar:./lib/slf4j.jar *.java
javac -cp .:./lib/libthrift.jar:./lib/slf4j.jar -d bin/ *.java


run and look in bin and lib folder
java -cp ./bin:./lib/libthrift.jar:./lib/slf4j.jar CommandServer
java -cp ./bin:./lib/libthrift.jar:./lib/slf4j.jar CommandClient