version:
libthrift-0.18.0.jar
slf4j-api-2.0.6.jar

compile:
javac -cp .:./lib/libthrift.jar:./lib/slf4j-api-2.0.6.jar *.java

run 
java -cp .:./lib/libthrift.jar:./lib/slf4j.jar CommandServer
java -cp .:./lib/libthrift.jar:./lib/slf4j.jar CommandClient

