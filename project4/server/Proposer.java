import java.util.HashSet;
import java.util.List;
public class Proposer {
    int currPort;
    List<Integer>replicaPorts;

    public Proposer( int currPort, List<Integer> replicaPorts) {
        this.currPort = currPort;
        this.replicaPorts = replicaPorts;
    }

    public boolean getConsensus(Proposal proposal) {

        return true;
    }

}
