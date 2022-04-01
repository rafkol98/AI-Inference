import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

public class GibbsSampling {

    private ArrayList<String[]> evidences;
    private BayesianNetwork bn;
    private String queried;
    private VariableElimination ve;

    public GibbsSampling(BayesianNetwork bn, String queried, ArrayList<String[]> evidences) {
        this.bn = bn;
        this.queried = queried;
        this.evidences = evidences;
        ve = new VariableElimination();
    }

    public double gibbsAsk(int samples) {
        // a vector of counts for each value of the queried variable - since binary and only one queried variable, the counts size are equal to 2.
        int countTrue, countFalse  = 0;

        ArrayList<Node> nonEvidences = getNonEvidences();

        HashMap<Node, String> nonEvidenceAssignment = assignNonEvidence(nonEvidences); // initialising some random values by random guess.

        for(int i=0; i<samples; i++) {
            // iteratively sample each one of the nodes in the non-evidence set from their FULL conditional (everybody
            // else except itself). Using the previous sample.
            for (Node nonEvidenceNode : nonEvidenceAssignment.keySet()) {
                // Get all CPTs required to look at their full conditional probability.
                ArrayList<CPT> appropriateCPTs = getAppropriateCPTs(nonEvidenceNode, nonEvidences);
                CPT joined = ve.join(appropriateCPTs, nonEvidenceNode.getLabel());
                System.out.println("non-evidence node: "+ nonEvidenceNode);
                System.out.println("CPT table");
                joined.constructAndPrintCPT(true);
                System.out.println("\n");

            }
        }
        return -1;
    }

    public ArrayList<Node> getNonEvidences() {
        ArrayList<Node> nonEvidences = bn.getNodes(); // initialise all the BN nodes.
        ArrayList<Node> evidencesList = new ArrayList<>();

        for (String[] evidence : evidences) {
            evidencesList.add(bn.getNode(evidence[0])); // add all the evidences as nodes in a list.
        }
        nonEvidences.removeAll(evidencesList); // remove all variables that are included in the evidence list.
        return nonEvidences;
    }

    public HashMap<Node, String> assignNonEvidence(ArrayList<Node> nonEvidences) {
        HashMap<Node, String> nonEvidenceAssignment = new HashMap<>();
        String[] possibleValues={"T", "F"};
        Random r=new Random();
        // Assign a random value (out of the true possible) to each non-evidence variable.
        for (Node nonEvidence : nonEvidences) {
            int randomNumber = r.nextInt(possibleValues.length);
            String randomValue = possibleValues[randomNumber];
            nonEvidenceAssignment.put(nonEvidence, randomValue);
        }
        return nonEvidenceAssignment;
    }


    public ArrayList<CPT> getAppropriateCPTs(Node currentNonEv, ArrayList<Node> nonEvidences) {
        ArrayList<CPT> factors = new ArrayList<>();

        // iterate through the nodes of the BN.
        for (Node node : bn.getNodes()) {
            // add all the non evidence factors except itself
            if (!node.equals(currentNonEv)) {
                factors.add(node.getCpt());
            }
        }
        // add the CPTs of the evidence nodes.
        for (String[] evidence : evidences) {
            System.out.println("ev dame"+evidence[0]);
            System.out.println(bn.getNodes());
            Node evidenceNode = bn.getNode(evidence[0]);
            System.out.println(evidenceNode);
            factors.add(evidenceNode.getCpt());
        }

        return factors;
    }

}
