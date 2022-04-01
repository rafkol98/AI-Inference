import java.util.*;

import static java.util.stream.Collectors.toList;

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
        System.out.println("NON EVIDENCES "+ nonEvidences);
        HashMap<String, Integer> nonEvidenceAssignment = assignNonEvidence(nonEvidences); // initialising some random values by random guess.
        System.out.println(nonEvidenceAssignment);
        for(int i=0; i<samples; i++) {
            // iteratively sample each one of the nodes in the non-evidence set from their FULL conditional (everybody
            // else except itself). Using the previous sample.
            for (Node nonEvidenceNode : nonEvidences) {
                System.out.println("Non evidence node: "+ nonEvidenceNode);
                // Get all CPTs required to look at their full conditional probability.
                ArrayList<CPT> appropriateCPTs = getAppropriateCPTs(nonEvidences);
                CPT fullCPT = ve.joinMarginalise(appropriateCPTs, nonEvidenceNode.getLabel());
                fullCPT.constructAndPrintCPT(true);

                double probConds = fullCPT.getCPTProbability(getTruthValuesForCondition(nonEvidenceNode, fullCPT,nonEvidenceAssignment));
                nonEvidenceNode.getCpt().constructAndPrintCPT(true);
                System.out.println("NODE LABELS CPT: "+ fullCPT.getNodeLabels());
                System.out.println("PROB FOR CONDITIONS: "+ getTruthValuesForCondition(nonEvidenceNode, fullCPT,nonEvidenceAssignment));
                System.out.println("value for one: "+nonEvidenceNode.getCpt().getCorrespondingNodeTruthValue(1));
                double postTrue = nonEvidenceNode.getCpt().getCorrespondingNodeTruthValue(1) * probConds;
                double postFalse = nonEvidenceNode.getCpt().getCorrespondingNodeTruthValue(0) * probConds;
                System.out.println("POST tRUE: "+postTrue);
                System.out.println("POST FALSE: "+postFalse);

                getTruthValuesForCondition(nonEvidenceNode, fullCPT,nonEvidenceAssignment);
            }
        }
        return -1;
    }

    public ArrayList<Node> getNonEvidences() {
        ArrayList<Node> nonEvidences = new ArrayList<>(bn.getNodes()); // initialise all the BN nodes.
        ArrayList<Node> evidencesList = new ArrayList<>();

        for (String[] evidence : evidences) {
            evidencesList.add(bn.getNode(evidence[0])); // add all the evidences as nodes in a list.
        }
        nonEvidences.removeAll(evidencesList); // remove all variables that are included in the evidence list.
        return nonEvidences;
    }

    public HashMap<String, Integer> assignNonEvidence(ArrayList<Node> nonEvidences) {
        HashMap<String, Integer> nonEvidenceAssignment = new HashMap<>();
        Integer[] possibleValues={0, 1};
        Random r=new Random();
        // Assign a random value (out of the true possible) to each non-evidence variable.
        for (Node nonEvidence : nonEvidences) {
            int randomNumber = r.nextInt(possibleValues.length);
            int randomValue = possibleValues[randomNumber];
            nonEvidenceAssignment.put(nonEvidence.getLabel(), randomValue);
        }
        return nonEvidenceAssignment;
    }


    public ArrayList<CPT> getAppropriateCPTs(ArrayList<Node> nonEvidences) {
        ArrayList<CPT> factors = new ArrayList<>();
        // Create a new LinkedHashSet
        Set<CPT> set = new LinkedHashSet<>();

        // iterate through the nodes of the BN.
        for (Node node : nonEvidences) {
            set.add(node.getCpt());
        }

        // add the CPTs of the evidence nodes.
        for (String[] evidence : evidences) {
            Node evidenceNode = bn.getNode(evidence[0]);
            set.add(evidenceNode.getCpt());
        }

        factors.addAll(set);

        return factors;
    }

    public ArrayList<Integer> getTruthValuesForCondition(Node currentEvidence, CPT fullCPT, HashMap<String, Integer> nonEvidenceAssignment) {
        // Get the labels in the correct order.
        ArrayList<String> conditionsUsed = (ArrayList<String>) bn.getNodes().stream().map(e -> e.getLabel()).collect(toList());
        conditionsUsed.remove(currentEvidence.getLabel());
        conditionsUsed.sort(Comparator.comparingInt(fullCPT.getNodeLabels()::indexOf));

       return assignTruth(conditionsUsed, nonEvidenceAssignment);
    }

    public ArrayList<Integer> assignTruth(ArrayList<String> conditionsUsed, HashMap<String, Integer> nonEvidenceAssignment) {
        ArrayList<Integer> truthValues = new ArrayList<>();

        // Add all the binary values from the nonEvidence assignment map.
        for (String condition: conditionsUsed) {
            if (nonEvidenceAssignment.keySet().contains(condition)) {
                truthValues.add(nonEvidenceAssignment.get(condition));
            }
            // add all the values in the evidences array.
            else {
                for (String[] evidence : evidences) {
                    if (evidence[0].equalsIgnoreCase(condition)) {
                        int truthLooking = (evidence[1].equalsIgnoreCase("T")) ? 1 : 0;
                        truthValues.add(truthLooking);
                    }
                }
            }
        }
        return truthValues;
    }

//    public double getProbabilityInOwnTable(probConds) {
//
//    }
}
