import java.util.List;
public class Proposer {
    private int proposalNumber;
    private int roundNumber;
    private List<Acceptor> acceptors;
    private Learner learner;

    public Proposer(List<Acceptor> acceptors, Learner learner) {
        this.acceptors = acceptors;
        this.learner = learner;
        this.proposalNumber = 0;
        this.roundNumber = 0;
    }

    public void incrementProposalNumber() {
        // Increment the round number
        proposalNumber = (roundNumber * acceptors.size()) + 1;
        roundNumber++;
    }

    public void sendPrepareRequest() {
        // Send prepare request to a quorum of acceptors
        int quorum = (acceptors.size() / 2) + 1;
        int prepareResponses = 0;

        incrementProposalNumber();

        for (Acceptor acceptor : acceptors) {
            if (acceptor.handlePrepare(proposalNumber)) {
                prepareResponses++;
                if (prepareResponses >= quorum) {
                    handlePrepareResponse();
                    break;
                }
            }
        }
    }

    public void handlePrepareResponse() {
        // Handle prepare response from acceptors
        // In this simple implementation, we don't need to do anything here
        sendAcceptRequest();
    }

    public void sendAcceptRequest() {
        // Send accept request to a quorum of acceptors with the chosen proposal
        int quorum = (acceptors.size() / 2) + 1;
        int acceptResponses = 0;

    }

}
