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


Assignment Overview:
Picking from the previous stage of our project we implemented 2-Phase Commit (2PC) algorithm. Remember that our 2PC servers have major drawback in that it has a single point of failure. As we mentioned previously if the server that initiates the prepare and commit/abort process fails, the operation might not complete, and other replicas might be left waiting for commit or abort instructions. We can mitigate these risks by implementing a more fault-tolerant consensus algorithm, like the Paxos. In this project we will replace our 2PC with Paxos.

We will also introduce random failure to acceptors threads 

REQUIREMENT 2 Log:
proposer#[9003] proposing id#6270 val=DELETE(1)
proposer#[9003] promise request PROMISED by [9000] attempt=0
acceptor#[9001] random failure when preparing id#6270
proposer#[9003] promise request FAILED by [9001]'s network, attempt=0
proposer#[9003] promise request PROMISED by [9001] attempt=1
acceptor#[9002] random failure when preparing id#6270
proposer#[9003] promise request FAILED by [9002]'s network, attempt=0
proposer#[9003] promise request PROMISED by [9002] attempt=1
proposer#[9003] promise request PROMISED by [9003] attempt=0
proposer#[9003] promise request PROMISED by [9004] attempt=0
proposer#[9003]'s accept request ACCEPTED by [9000], attempt=0
proposer#[9003]'s accept request ACCEPTED by [9001], attempt=0
proposer#[9003]'s accept request ACCEPTED by [9002], attempt=0
proposer#[9003]'s accept request ACCEPTED by [9003], attempt=0
proposer#[9003]'s accept request ACCEPTED by [9004], attempt=0
------[13:27:17.318] Replica#9000 received delete reqId=...bde2 from client ip=127.0.0.1 port=61902 for key=1, msg=op successful
{2=2, 3=3, 4=4, 5=5, 6=6}
------[13:27:17.319] Replica#9001 received delete reqId=...bde2 from client ip=127.0.0.1 port=61902 for key=1, msg=op successful
{2=2, 3=3, 4=4, 5=5, 6=6}
------[13:27:17.320] Replica#9002 received delete reqId=...bde2 from client ip=127.0.0.1 port=61902 for key=1, msg=op successful
{2=2, 3=3, 4=4, 5=5, 6=6}
------[13:27:17.321] Replica#9003 received delete reqId=...bde2 from client ip=127.0.0.1 port=61902 for key=1, msg=op successful
{2=2, 3=3, 4=4, 5=5, 6=6}
------[13:27:17.322] Replica#9004 received delete reqId=...bde2 from client ip=127.0.0.1 port=61902 for key=1, msg=op successful
{2=2, 3=3, 4=4, 5=5, 6=6}
[13:27:17.323] Replica#9003 received delete reqId=...bde2 from client ip=127.0.0.1 port=61902 for key=1, msg=op successful
{2=2, 3=3, 4=4, 5=5, 6=6}