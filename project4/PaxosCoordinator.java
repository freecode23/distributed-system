import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class PaxosCoordinator {
    private Proposer proposer;
    private List<Acceptor> acceptors;
    private Learner learner;

    // 3. Each consensus instance decides on the value of a single log entry
    private AtomicInteger logPosition = new AtomicInteger(0);

    // 4. Variables for proposers
    private Map<String, Integer> proposals = new ConcurrentHashMap<>();

    // 5. Variables for acceptors
    private Map<String, Promise> promises = new ConcurrentHashMap<>();
    private Map<Integer, Integer> acceptedValues = new ConcurrentHashMap<>();

    public PaxosCoordinator(List<Acceptor> acceptors,Learner learner) {
        this.acceptors = acceptors;
        this.learner = learner;
        this.proposer = new Proposer(acceptors, learner);
    }
    
    public List<Promise> coordPrepare(int proposalNumber) {
        List<Promise> promises = new ArrayList<>();

        for (Acceptor acceptor : acceptors) {
            boolean promiseSent = acceptor.handlePrepare(proposalNumber);

            if (promiseSent) {
                promises.add(
                        new Promise(proposalNumber, acceptor.getHighestAcceptedProposalNumber(), acceptor.getHighestAcceptedValue()));
            }
        }

        return promises;
    }
    
    public int coordAccept(int proposalNumber, int proposedValue) {
        int acceptResponseCount = 0;

        for (Acceptor acceptor : acceptors) {
            boolean acceptResponseSent = acceptor.handleAccept(proposalNumber, proposedValue);

            if (acceptResponseSent) {
                acceptResponseCount++;
            }
        }

        // Check if a majority of acceptors have accepted the proposal
        if (acceptResponseCount > acceptors.size() / 2) {
            // Inform the learner about the accepted value
            Proposal acceptedProposal = new Proposal(proposalNumber, (Integer) proposedValue);
            learner.learn(acceptedProposal);
        }

        return acceptResponseCount;
    }

    public void coordLearn(Proposal proposal) {
        learner.learn(proposal);
        // Apply the learned value to the data store or other application-specific logic
    }
}