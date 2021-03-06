import java.util.*;

import static java.util.stream.Collectors.toList;

/**
 * Create a new Gibbs Sampling class.
 * @author :210017984
 */
public class GibbsSampling {

    // initialise variables.
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

    /**
     * The gibbsAsk algorithm.
     * @param samples how many samples to try and generate.
     * @param value the turth value that we are looking for.
     * @return the estimated probability.
     */
    public double gibbsAsk(int samples, String value) {
        // a vector of counts for each value of the queried variable - since binary and only one queried variable, the counts size are equal to 2.
        HashMap<String, ArrayList<Integer>> allValuesAssigned = new HashMap<>();
        ArrayList<Node> nonEvidences = getNonEvidences();
        HashMap<String, Integer> nonEvidenceAssignment = assignNonEvidence(allValuesAssigned, nonEvidences); // initialising some random values by random guess.

        for(int i=0; i<samples; i++) {
            // iteratively sample each one of the nodes in the non-evidence set from their FULL conditional (everybody
            // else except itself). Using the previous sample.
            for (Node nonEvidenceNode : nonEvidences) {
                // Get all CPTs required to look at their full conditional probability.
                ArrayList<CPT> appropriateCPTs = getAppropriateCPTs(nonEvidences);
                CPT fullCPT = ve.joinMarginalise(appropriateCPTs, nonEvidenceNode.getLabel());

                double probConds = fullCPT.getCPTProbability(getTruthValuesForCondition(nonEvidenceNode, fullCPT,nonEvidenceAssignment));

                double postTrue = nonEvidenceNode.getCpt().getCorrespondingNodeTruthValue(1) * probConds;
                double postFalse = nonEvidenceNode.getCpt().getCorrespondingNodeTruthValue(0) * probConds;

                double[] normalizedProb = normalizedProbs(postTrue, postFalse);
                double prediction = Math.random();
                int sampleValue = prediction <= normalizedProb[0] ? 1 : 0;
                nonEvidenceAssignment.put(nonEvidenceNode.getLabel(), sampleValue);
                ArrayList<Integer> valuesSoFar = allValuesAssigned.get(nonEvidenceNode.getLabel());
                // add new value sampled.
                valuesSoFar.add(sampleValue);
                // update arraylist of map to reflect to include the enw sample.
                allValuesAssigned.put(nonEvidenceNode.getLabel(), valuesSoFar);
            }
        }
        // count number of occurences.
        int numberOfOccurences = getNumberOfOccurences(allValuesAssigned, queried, value);


        double returnVal = (double) numberOfOccurences/ (double) samples;
        return returnVal;
    }

    /**
     * Get all the non evidences variables.
     * @return
     */
    public ArrayList<Node> getNonEvidences() {
        ArrayList<Node> nonEvidences = new ArrayList<>(bn.getNodes()); // initialise all the BN nodes.
        ArrayList<Node> evidencesList = new ArrayList<>();

        for (String[] evidence : evidences) {
            evidencesList.add(bn.getNode(evidence[0])); // add all the evidences as nodes in a list.
        }
        nonEvidences.removeAll(evidencesList); // remove all variables that are included in the evidence list.
        return nonEvidences;
    }

    public HashMap<String, Integer> assignNonEvidence(HashMap<String, ArrayList<Integer>> valuesAssignedForEach, ArrayList<Node> nonEvidences) {
        HashMap<String, Integer> nonEvidenceAssignment = new HashMap<>();
        Integer[] possibleValues={0, 1};
        Random r=new Random();
        // Assign a random value (out of the true possible) to each non-evidence variable.
        for (Node nonEvidence : nonEvidences) {
            int randomNumber = r.nextInt(possibleValues.length);
            int randomValue = possibleValues[randomNumber];
            nonEvidenceAssignment.put(nonEvidence.getLabel(), randomValue);

            // New ArrayList is created to store the value - happens here because its the first assignment.
            ArrayList<Integer> valueAdded = new ArrayList<>();
            valueAdded.add(randomValue);
            valuesAssignedForEach.put(nonEvidence.getLabel(), valueAdded);
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

    public double[] normalizedProbs(double probTrue, double probFalse) {
        double[] normalized = new double[2];
        normalized[0] = probTrue / (probTrue + probFalse); // normalized true.
        normalized[1] = probFalse / (probTrue + probFalse); // normalized false.

        return normalized;
    }

    public int getNumberOfOccurences(HashMap<String, ArrayList<Integer>> allValuesAssigned, String queried, String value) {
        int truthLooking = (value.equalsIgnoreCase("T")) ? 1 : 0;
        int occurrences = Collections.frequency(allValuesAssigned.get(queried), truthLooking);
        return occurrences;
    }
}
