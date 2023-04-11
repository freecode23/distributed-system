import java.text.SimpleDateFormat;
import java.util.Date;

public class ProposalExtended extends Proposal {

    public ProposalExtended(int proposalId, KeyValOperation operation) {
        super(proposalId, operation);
    }

    // Add your custom method(s) here
    public static synchronized Proposal generateProposal(KeyValOperation operation) {
        // Your implementation
        String s = new SimpleDateFormat("HHmmssSSS").format(new Date());
        Proposal proposal = new Proposal(Integer.parseInt(s), operation);

        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {

        }

        return proposal;
    }
}