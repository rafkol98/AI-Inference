import java.util.ArrayList;
import java.util.Arrays;

public class GibbsSampling {

    ArrayList<String[]> evidences;
    private BayesianNetwork bn;
    private String queried;
    private String[] order;

    public GibbsSampling(BayesianNetwork bn, String queried, String[] order, ArrayList<String[]> evidences) {
        this.bn = bn;
        this.queried = queried;
        this.order = order;
        this.evidences = evidences;
    }

    public double gibbsAsk() {
        double[] counts = new double[2]; // a vector of counts for each value of the queried variable - since binary, the counts size are equal to 2.
        ArrayList<Node> nonEvidences = bn.getNodes(); // initialise all the BN nodes.
        ArrayList<Node> evidencesList = new ArrayList<>();
        for (int i = 0; i < evidences.size(); i++) {
            evidencesList.add(bn.getNode(evidences.get(i)[0])); // add all the evidences as nodes in a list.
        }
        nonEvidences.removeAll(evidencesList); // remove all variables that are included in the evidence list.

    }
}
