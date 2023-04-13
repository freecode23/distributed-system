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
Building upon the previous stage of our project, where we implemented the 2-Phase Commit (2PC) algorithm for achieving consistency across replicated state machine Key-Value Store servers, we recognized a major drawback: the presence of a single point of failure. As mentioned earlier, if the server initiating the prepare and commit/abort process fails, the operation might not complete, leaving other replicas waiting indefinitely for commit or abort instructions. This could potentially lead to inconsistencies and system-wide disruption. To address these risks, we will implement a more fault-tolerant consensus algorithm, namely Paxos, to replace our 2PC and improve the reliability of our system.

The new goal for this project is to integrate Paxos, which consists of Proposers, Acceptors, and Learners, into our replicated servers, ensuring the continual operation of our Key-Value Store despite replica failures. By implementing Paxos, we aim to achieve consensus in event ordering amongst our replicated servers and handle client requests generated at any time.

In addition, we will introduce random failures to the acceptor threads, simulating real-world conditions and testing the robustness of our Paxos implementation in handling these failures. This will demonstrate how Paxos overcomes replicated server failures, ensuring the system's resilience to faults. By completing this project, we will have a more reliable and fault-tolerant Key-Value Store, ready to handle challenging scenarios and maintain consistency across replicas.


Executive Summary:
1. Creating the models
While working on this project, I started by creating the necessary classes to implement the Paxos algorithm in our Key-Value Store. The main class, KeyValueServer, has a nested KeyValueService class, which handles the core functionality. In addition, we have a separate Proposer class and a ServerDriver class, responsible for managing the proposer logic and driving the server's execution, respectively. We also generated several classes using Thrift files to define data structures and services, such as Result, OperationType, ConsensusResult, KeyValOperation, Status, Promise, Proposal, and KeyValueService.

2. Get Consensus
The getConsensus() method in the Proposer class aims to achieve consensus on a given proposal among the acceptors. It comprises three phases: (1) sending prepare requests, (2) sending accept requests, and (3) sending learn requests. The method initiates by populating a list of acceptor servers and their corresponding transports.

In the first phase, the proposer sends prepare requests to the acceptors. It retries up to a maximum number of attempts in case of failure or rejection. The proposer keeps track of the highest proposal ID it encounters and the count of promises obtained from the acceptors. If the proposer cannot obtain promises from more than half of the acceptors, the consensus process fails.

In the second phase, the proposer sends accept requests to the acceptors. Similar to the prepare phase, it retries multiple times in case of failures. If the proposer cannot get acceptances from more than half of the acceptors, the consensus process fails.

In the third and final phase, the proposer sends learn requests to the acceptors, asking them to learn the accepted proposal. The commit result of the proposer is saved in the consensus result. Once the consensus process is complete, the method closes the communication with the acceptors and returns the consensus result.

Throughout the process, the proposer handles various cases, such as network failures and acceptor rejections, using retries and delays to ensure a robust consensus process.

Illustration of failing acceptor:
The log below demonstrates the consensus process for a DELETE operation on key 1. During the prepare phase, acceptors 9001 and 9002 encounter random failures, but the proposer makes a second attempt (attempt 1) to obtain promises from them. Eventually, all acceptors promise and accept the proposal. The DELETE operation is then successfully executed on all replicas, resulting in the same key-value store state across the system. This highlights the system's resilience to failures and its ability to maintain consistency among replicas despite temporary disruptions.

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


3. The Prepare and Accept Method and their Callables:
In the provided code, we implemented asynchronous behavior using the Callable interface for the PrepareCallable class, which represents a task that computes a Promise object based on a given proposal. The call() method within the PrepareCallable class contains the logic to handle different cases based on the incoming proposal ID and the current state of the acceptor.

The prepare() method is responsible for executing the PrepareCallable task asynchronously. First, it initializes an ExecutorService with a single thread executor. Then, it creates a PrepareCallable instance with the incoming proposal and wraps it in a FutureTask object. This allows us to manage the asynchronous execution of the task and retrieve the Promise object once it's completed.

The executor submits the FutureTask for execution, and the prepare() method awaits the result using the get() method with a specified timeout of 10 seconds. If the task completes successfully within the timeout, the Promise object is returned. If there is an exception or the task does not complete within the specified time, the prepare() method returns null, indicating an error.

This approach enables efficient handling of prepare requests without blocking the main thread, allowing the acceptor to process incoming requests concurrently.

4.
Despite the successful implementation of the Paxos algorithm, there are some limitations in our current design. One such limitation is that instead of acceptors sending the proposal to all learners, the proposer is responsible for this task. This could potentially lead to bottlenecks in the system, as the proposer would have to handle the additional responsibility of communicating with all learners. Furthermore, this approach might not be fully compliant with the original Paxos algorithm, which could result in unexpected behavior under certain circumstances. To address this limitation, we could consider modifying our implementation to have acceptors send the proposal to all learners, thus adhering more closely to the Paxos algorithm and potentially improving the system's performance.