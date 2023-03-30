public class Promise {
    private int proposalNumber;
    private int acceptedProposalNumber;
    private int acceptedValue;

    public Promise(int proposalNumber, int acceptedProposalNumber, int acceptedValue) {
        this.proposalNumber = proposalNumber;
        this.acceptedProposalNumber = acceptedProposalNumber;
        this.acceptedValue = acceptedValue;
    }

    public int getProposalNumber() {
        return proposalNumber;
    }

    public int getHighestProposalNumber() {
        return acceptedProposalNumber;
    }

    public int getAcceptedValue() {
        return acceptedValue;
    }
}