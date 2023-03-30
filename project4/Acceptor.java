
public class Acceptor {
    private int highestSeenProposalNumber;
    private int highestAcceptedProposalNumber;
    private int highestAcceptedValue;
    private int port;

    public Acceptor(int port) {
        this.port =  port;
        highestSeenProposalNumber = 0;
        highestAcceptedProposalNumber = 0;
        highestAcceptedValue = -1;
    }

    public boolean handlePrepare(int proposalNumber) {
        // Check if the proposal number is higher than the highest proposal number seen
        if (proposalNumber > highestSeenProposalNumber) {
            // If so, update the highest proposal number and send a promise to the proposer
            highestSeenProposalNumber = proposalNumber;
            return true; // Promise sent
        } else {
            return false; // Promise not sent
        }
    }

    public int getHighestAcceptedProposalNumber() {
        return this.highestAcceptedProposalNumber;
    }

    public int getHighestAcceptedValue() {
        return this.highestAcceptedValue;
    }

    public boolean handleAccept(int proposalNumber, int proposedValue) {
        // Check if the proposal number is higher than or equal to the highest proposal
        // number seen
        if (proposalNumber >= highestSeenProposalNumber) {
            // If so, accept the proposal, update the highest accepted proposal number and
            // value, and send an accept response to the proposer
            highestAcceptedProposalNumber = proposalNumber;
            highestAcceptedValue = proposedValue;
            return true; // Accept response sent
        } else {
            return false; // Accept response not sent
        }
    }
}