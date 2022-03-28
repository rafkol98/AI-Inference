import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;
import java.util.stream.Collectors;

public class Agent {

    // Initialise inputs.
    BayesianNetwork bn;
    Node queried;
    ArrayList<String> order;


    public Agent(BayesianNetwork bn, Node queried, ArrayList<String> order) {
        this.bn = bn;
        this.queried = queried;
        this.order = order;


        variableElimination(order);
    }

    public void variableElimination(ArrayList<String> order) {
        pruneIrrelevantVariables();
        ArrayList<CPT> factors = createSetFactors();
        System.out.println("factors"+factors);

        for (String label : order) {
            System.out.println(factors);
            ArrayList<CPT> toSumOut = getFactorsContainingLabel(label, factors); // get factors containing label.
            factors.removeAll(toSumOut); // remove all factors containing label.

            if (toSumOut.size() >= 2) {
                // create a new factor with all variables in factors of ToSumOut but without label.
                CPT newFactor = joinMarginalise(toSumOut, label);
                factors.add(newFactor); // add new factor.
            }
        }
    }

    /**
     * Remove every variable that is not an ancestor of a queried variable.
     */
    public void pruneIrrelevantVariables() {
        ArrayList<String> ancLabels = new ArrayList<>();
        ancLabels.add(queried.getLabel());

        Stack<Node> ancestors = new Stack<>();
        ancestors.push(queried);
        Node currentNode = queried;

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
        order.retainAll(ancLabels); // retain all elements identified as ancestors.
    }

    /**
     * Create a set of factors.
     *
     * @return
     */
    public ArrayList<CPT> createSetFactors() {
        ArrayList<CPT> factors = new ArrayList<>();
        System.out.println(bn.getNodes());
        // iterate through the nodes of the BN.
        for (Node node : bn.getNodes()) {
            factors.add(node.getCpt());
            node.getCpt().constructAndPrintCPT(true);
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
    public ArrayList<CPT> getFactorsContainingLabel(String label, ArrayList<CPT> factors) {
        ArrayList<CPT> toSumOut = new ArrayList<>();

        for (CPT factor : factors) {
            if (factor.getNodeLabels().contains(label)) {
                toSumOut.add(factor);
            }
        }
        return toSumOut;
    }

    // remove variable.
    public void marginalise() {

    }


    public ArrayList<String> variablesBoth(CPT first, CPT second) {
        // return common elements in first and second CPTs.
        return (ArrayList<String>) first.getNodeLabels().stream().filter(second.getNodeLabels()::contains).collect(Collectors.toList());
    }

    public ArrayList<String> variablesNotInSecond(CPT first, CPT second) {
        return (ArrayList<String>) first.getNodeLabels().stream()
                .filter(element -> !second.getNodeLabels().contains(element))
                .collect(Collectors.toList());
    }


    //TODO: change
    private ArrayList<Integer> getFactorTruthCombination(ArrayList<Integer> truthCombination, CPT newFactor, CPT factor) {
        ArrayList<Integer> factorTruth = new ArrayList<>();
        for (int i = 0; i < factor.getNodeLabels().size(); i++) {
            for (int j = 0; j < newFactor.getNodeLabels().size(); j++) {
                if (newFactor.getNodeLabels().get(j).equals(factor.getNodeLabels().get(i))) {
                    factorTruth.add(truthCombination.get(j));
                }

            }
        }
        return factorTruth;
    }

    //TODO: change!
    private CPT joinMarginalise(ArrayList<CPT> toSumOut, String label) {
        System.out.println("\n LABEL " + label);
        CPT newFactor = new CPT(label);
        CPT first = toSumOut.get(0);

        // Join iteratively (two factors at a time).
        for (int i = 1; i < toSumOut.size(); i++) {

            CPT second = toSumOut.get(i);

            // Collect all the variables without repetition from the two CPT/factors tables.
            ArrayList<String> v1 = variablesBoth(first, second); // variables in both.
            ArrayList<String> v2 = variablesNotInSecond(first, second); // variables in the first but not second.
            ArrayList<String> v3 = variablesNotInSecond(second, first); // variables in the second but not first.
            // Combine three arraylists. TODO: maybe function it.
            ArrayList<String> combined = new ArrayList<String>();
            combined.addAll(v1);
            combined.addAll(v2);
            combined.addAll(v3);

            // Truth combinations to calculate.
            newFactor.setNodeLabels(combined);
            // Get all the truth values combinations.
            ArrayList<ArrayList<Integer>> newFactorTruths = newFactor.getCombinations();
            ArrayList<Double> newFactorValues = new ArrayList<>();

            // Iterate through the new factors' truth combinations and calculate their values.
            for (int x = 0; x < newFactorTruths.size(); x++) {
                ArrayList<Integer> truthCombination = newFactorTruths.get(x);

                ArrayList<Integer> f1Truth = getFactorTruthCombination(truthCombination, newFactor,first);
                ArrayList<Integer> f2Truth = getFactorTruthCombination(truthCombination, newFactor, second);

                double value = first.getCPTProbability(f1Truth) * second.getCPTProbability(f2Truth);

                newFactorValues.add(value);
            }

            Collections.reverse(newFactorValues);
            newFactor.addCPTvalues(newFactorValues);
//            newFactor.constructAndPrintCPT(true);
            first = newFactor;
        }

        System.out.println("NEW FACTOR");
        newFactor.constructAndPrintCPT(true);
        // Create a new vector with all variables in factors of toSumOut but Y.
//        CPT joinResult = new CPT(newFactor);

//        return joinResult;
        return newFactor;
    }

}
