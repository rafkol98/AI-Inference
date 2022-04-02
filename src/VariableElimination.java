import java.util.*;
import java.util.stream.Collectors;

/**
 * Create a new Variable Elimination run.
 * @author :210017984
 */
public class VariableElimination {

    // Initialise inputs.
    private BayesianNetwork bn;
    private Node queried;
    private ArrayList<String> order;
    private ArrayList<String[]> evidences;
    private int numberOfOperations = 0;
    private int truthValuesCalculated = 0;

    public VariableElimination() {
    }

    public VariableElimination(BayesianNetwork bn, String queried, String[] order) {
        this.bn = bn;
        this.queried = bn.getNode(queried);
        this.order = new ArrayList<>(Arrays.asList(order));
    }

    public VariableElimination(BayesianNetwork bn, String queried, String[] order, ArrayList<String[]> evidences) {
        this.bn = bn;
        this.queried = bn.getNode(queried);
        this.order = new ArrayList<>(Arrays.asList(order));
        this.evidences = evidences;
    }

    /**
     * Get the number of joinMarginalise operations performed.
     * @return the number of times joinMarginalise was called.
     */
    public int getNumberOfOperations() {
        return numberOfOperations;
    }

    /**
     * Get the number of truth values calculated.
     * @return the number of truth values calculated.
     */
    public int getTruthValuesCalculated() {
        return truthValuesCalculated;
    }

    /**
     * Run the variable elimination algorithm.
     * @param value the value being looked for - True or False?
     * @param evidence the evidence given.
     * @return
     */
    public double runVE(String value, boolean evidence) {
        pruneIrrelevantVariables(evidence);
        ArrayList<CPT> factors = createSetFactors();

        // if we are performing variable elimination with evidence, then project evidence for related factors.
        if (evidence) {
            projectEvidence(factors);
        }
        CPT newFactor = null;
        // if order has at least one element, then perform variable elimination.
        if (order.size() >= 1) {
            for (String label : order) {
                ArrayList<CPT> toSumOut = getFactorsContainingLabel(label, factors); // get factors containing label.
                factors.removeAll(toSumOut); // remove all factors containing label.

                // create a new factor with all variables in factors of ToSumOut but without label.
                newFactor = joinMarginalise(toSumOut, label);
                factors.add(newFactor); // add new factor.
            }

            if (evidence) {
                if (factors.size() > 1) {
                    // the node label should be the same for both - the queried node.
                    newFactor = join(factors, queried.getLabel());
                }
                newFactor.normalize();
            }
        }
        // otherwise just assign the queried node's cpt to newFactor and read its value.
        else if (order.size() == 0 || newFactor == null) {
            newFactor = queried.getCpt();
        }

        // Get the truth value that we are looking.
        int truthLooking = (value.equalsIgnoreCase("T")) ? 1 : 0;
        return newFactor.getCPTSingleProb(truthLooking);
    }

    /**
     *
     * @param factors
     */
    private void projectEvidence(ArrayList<CPT> factors) {
        for (String[] ev : evidences) {
            // find the correspondent factor for current evidence label.
            CPT evFactor = getCorrespondentFactorForLabel(factors, ev[0]);
            boolean truthToChange = (ev[1].equalsIgnoreCase("T")) ? true : false;
            evFactor.setToZero(truthToChange);
        }
    }

    /**
     * Prunes all the irrelevant variables according to our task. If the evidence flag is true, then it deletes
     * ancestors of the evidences. Otherwise it just removes every variable that is not an ancestor of the queried
     * variable.
     *
     * @param evidence
     */
    private void pruneIrrelevantVariables(boolean evidence) {
        ArrayList<String> ancLabels = new ArrayList<>();
        // prune not ancestors of queried node.
        prune(queried, ancLabels);
        // prune not ancestors of evidence nodes.
        if (evidence) {
            for (String[] ev : evidences) {
                Node evNode = bn.getNode(ev[0]); // get node of evidence.
                prune(evNode, ancLabels); // prune order lists based on this evidence ancestors.
            }
        }
        order.retainAll(ancLabels); // retain all elements identified as ancestors.
    }

    /**
     * Performs the pruning by removing every variable that is not an ancestor of a given initial node.
     *
     * @param initialNode the initial node that we search for its ancestors.
     */
    private void prune(Node initialNode, ArrayList<String> ancLabels) {
        ancLabels.add(initialNode.getLabel());

        Stack<Node> ancestors = new Stack<>();
        ancestors.push(initialNode);
        Node currentNode = initialNode;

        // iterate all the ancestors.
        while (!ancestors.isEmpty()) {
            // add all ancestors to the stack.
            if (currentNode.getParents().size() != 0) {
                for (Node node : currentNode.getParents()) {
                    ancestors.push(node);
                    ancLabels.add(node.getLabel());
                }
            }

            currentNode = ancestors.pop(); // take out the first element in the stack.
        }
    }

    /**
     * Create a set of factors for every node in the Bayesian network that is included in the order or is the queried variable.
     *
     * @return
     */
    private ArrayList<CPT> createSetFactors() {
        ArrayList<CPT> factors = new ArrayList<>();

        // iterate through the nodes of the BN.
        for (Node node : bn.getNodes()) {
            if (order.contains(node.getLabel()) || node.getLabel().equalsIgnoreCase(queried.getLabel())) {
                factors.add(node.getCpt());
            }
        }

        return factors;
    }

    /**
     * Get the factors that contain the passed in label.
     *
     * @param label
     * @param factors
     * @return
     */
    private ArrayList<CPT> getFactorsContainingLabel(String label, ArrayList<CPT> factors) {
        ArrayList<CPT> toSumOut = new ArrayList<>();

        for (CPT factor : factors) {
            if (factor.getNodeLabels().contains(label)) {
                toSumOut.add(factor);
            }
        }
        return toSumOut;
    }

    /**
     * Find the correspondent CPT/factor from the factors for a passed in label.
     *
     * @param label
     * @return
     */
    private CPT getCorrespondentFactorForLabel(ArrayList<CPT> factors, String label) {
        // Iterate through the factors.
        for (CPT factor : factors) {
            Node node = bn.getNode(label);
            // if the corresponding node for that
            if (factor.getCorrespondingNode().equals(node)) {
                return factor;
            }
        }
        return null;
    }

    /**
     * Marginalise a CPT, removing the label.
     * @param newCPT
     * @param label
     * @return
     */
    public CPT marginalise(CPT newCPT, String label) {
        CPT marginalised = new CPT();
        // set node labels everything except current label.
        ArrayList<String> nodeLabels = new ArrayList<>(newCPT.getNodeLabels());
        int index = nodeLabels.indexOf(label);

        if (nodeLabels.size() > 1) {
            nodeLabels.remove(label);
            marginalised.setNodeLabels(nodeLabels);

            // add the marginalised values.
            ArrayList<Double> marginalisedFactorValues = new ArrayList<>();
            ArrayList<ArrayList<Integer>> truthAlreadyTried = new ArrayList<>();

            // iterate through the CPT values.
            for (int i = 0; i < newCPT.getCptValues().size(); i++) {
                ArrayList<Integer> truthValuesForTrue = newCPT.getCombinations().get(i);
                ArrayList<Integer> truthValuesForFalse = newCPT.getCombinations().get(i);
                truthValuesForTrue.set(index, 1); // set value for true.
                truthValuesForFalse.set(index, 0); // set value for false.

                // if truth was not already tried for both true and false combinations, then use them.
                if (!truthAlreadyTried.contains(truthValuesForTrue) && !truthAlreadyTried.contains(truthValuesForFalse)) {
                    double value = newCPT.getCPTProbability(truthValuesForTrue) + newCPT.getCPTProbability(truthValuesForFalse);
                    marginalisedFactorValues.add(value);
                    truthAlreadyTried.add(truthValuesForTrue);
                    truthAlreadyTried.add(truthValuesForFalse);
                    truthValuesCalculated++; // increment counter for truth values calculated.
                }

            }
            Collections.reverse(marginalisedFactorValues); // reverse the values (Added different order).

            marginalised.addCPTvalues(marginalisedFactorValues);
        } else if (nodeLabels.size() == 1) {
            marginalised.setNodeLabels(nodeLabels);
        }
        return marginalised;
    }


    /**
     * Get all the variables in both CPTs.
     * @param first the first CPT.
     * @param second the second CPT.
     * @return common variables.
     */
    private ArrayList<String> variablesBoth(CPT first, CPT second) {
        // return common elements in first and second CPTs.
        return (ArrayList<String>) first.getNodeLabels().stream().filter(second.getNodeLabels()::contains).collect(Collectors.toList());
    }

    /**
     * Get variables present in the first CPT but not in the second.
     * @param first the first CPT.
     * @param second the second CPT.
     * @return the variables not in the second CPT.
     */
    private ArrayList<String> variablesNotInSecond(CPT first, CPT second) {
        return (ArrayList<String>) first.getNodeLabels().stream()
                .filter(element -> !second.getNodeLabels().contains(element))
                .collect(Collectors.toList());
    }

    /**
     * Get truth combinations of CPTs between two CPTs.
     * @param truthCombination the truth combinations examined.
     * @param newCPT the new cpt.
     * @param cpt the previous cpt.
     * @return an arraylist containing all the possible CPT truth combinations.
     */
    private ArrayList<Integer> getCPTTruthCombination(ArrayList<Integer> truthCombination, CPT newCPT, CPT cpt) {
        ArrayList<Integer> combinations = new ArrayList<>();
        // iterate through all the node labels of both cpt and new cpt.
        for (int i = 0; i < cpt.getNodeLabels().size(); i++) {
            for (int x = 0; x < newCPT.getNodeLabels().size(); x++) {
                // if two labels are equal, then add the value of truth combination in the combinations.
                if (newCPT.getNodeLabels().get(x).equalsIgnoreCase(cpt.getNodeLabels().get(i))) {
                    combinations.add(truthCombination.get(x));
                }

            }
        }
        return combinations;
    }

    /**
     * The JoinMarginalise operation creates a new CPT with all
     * variables in factors of ToSumOut but without Y/label
     * @param toSumOut the factors/cpts to sumout.
     * @param label the label to marginalise.
     * @return
     */
    public CPT joinMarginalise(ArrayList<CPT> toSumOut, String label) {
        numberOfOperations++; // increment number of operations counter.

        CPT newFactor;
        if (toSumOut.size() > 1) {
            newFactor = join(toSumOut, label);
        } else {
            newFactor = toSumOut.get(0);
        }
        CPT marginalisedNewFactor = marginalise(newFactor, label);

        return marginalisedNewFactor;
    }

    /**
     * Join two CPTs together.
     * @param toSumOut the variables to sum out.
     * @param label the label to use for the new CPT.
     * @return the new joined CPT>
     */
    private CPT join(ArrayList<CPT> toSumOut, String label) {
        CPT newCPT = new CPT(label);
        CPT first = toSumOut.get(0);
        // Join iteratively (two factors at a time).
        for (int i = 1; i < toSumOut.size(); i++) {
            CPT second = toSumOut.get(i);

            ArrayList<String> combined = getCombined(first, second);
            // Truth combinations to calculate.
            newCPT.setNodeLabels(combined);

            ArrayList<Double> newCPTValues = calculateNewCPTValues(newCPT, first, second);

            // reverse values orders.
            Collections.reverse(newCPTValues);
            newCPT.addCPTvalues(newCPTValues);

            first = new CPT(newCPT); // make a deep copy.
        }
        return newCPT;
    }

    /**
     * Calculate new CPT values after joining two CPTs together.
     * @param newCPT the newCPT that holds the addition.
     * @param first the first cpt.
     * @param second the second cpt.
     * @return the new values to be added.
     */
    public ArrayList<Double> calculateNewCPTValues(CPT newCPT, CPT first, CPT second) {
        // Get all the truth values combinations.
        ArrayList<ArrayList<Integer>> newFactorTruths = newCPT.getCombinations();
        ArrayList<Double> newCPTValues = new ArrayList<>();

        // Iterate through the new factors' truth combinations and calculate their values.
        for (int x = 0; x < newFactorTruths.size(); x++) {
            ArrayList<Integer> truthCombination = newFactorTruths.get(x);
            ArrayList<Integer> f1Truth = getCPTTruthCombination(truthCombination, newCPT, first);
            ArrayList<Integer> f2Truth = getCPTTruthCombination(truthCombination, newCPT, second);

            double value = first.getCPTProbability(f1Truth) * second.getCPTProbability(f2Truth);
            newCPTValues.add(value);
            truthValuesCalculated++; // increment counter for truth values calculated.
        }

        return newCPTValues;
    }

    /**
     * // Collect all the variables without repetition from the two CPT/factors tables.
     *
     * @param first
     * @param second
     * @return
     */
    private ArrayList<String> getCombined(CPT first, CPT second) {
        ArrayList<String> v1 = variablesBoth(first, second); // variables in both.
        ArrayList<String> v2 = variablesNotInSecond(first, second); // variables in the first but not second.
        ArrayList<String> v3 = variablesNotInSecond(second, first); // variables in the second but not first.
        // Combine three arraylists.
        ArrayList<String> combined = new ArrayList<String>();
        combined.addAll(v1);
        combined.addAll(v2);
        combined.addAll(v3);

        Collections.reverse(combined);
        return combined;
    }
}