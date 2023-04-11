PRERQUISITES:
jar file needed and the version:
libthrift-0.18.0.jar
slf4j-api-2.0.6.jar

Step 0. Generate java files from thrift files (OPTIONAL) and append it to services folder
You need to have thrift installed to do this. If the java file is already generated you can skip this step
thrift --gen java -out ./services services/KeyValueService.thrift

Step 1. make script executable:
chmod +x start_servers.sh start_client.sh kill_servers.sh

Step 2. run multiple servers:
./start_servers.sh

Step 3. run client
./start_client.sh

