PRERQUISITES:
jar file needed and the version:
libthrift-0.18.0.jar
slf4j-api-2.0.6.jar

Step 0. Generate java files from thrift files (OPTIONAL)
You need to have thrift installed to do this. If the java file is already generated you can skip this step
thrift --gen java -out ./services services/KeyValueService.thrift

Step 1. Start servers
make script executable:
chmod +x kill_servers.sh

run multiple servers:
./start_servers.sh

run client:
java -cp ./bin:./lib/libthrift.jar:./lib/slf4j.jar Client 127.0.0.1 


