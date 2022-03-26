import java.util.ArrayList;
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


        variableElimination(bn, queried, order);
    }

    public void variableElimination(BayesianNetwork bn, Node queried, ArrayList<String> order) {
        pruneIrrelevantVariables();
        ArrayList<CPT> factors = createSetFactors();
        join(factors.get(2), factors.get(3));


//        ArrayList<Double> t = factors.get(3).get("L",0);
//        factors.get(3).printMap();
//        System.out.println(t);
//        ArrayList<String> labels = new ArrayList<>();
//        labels.add("K");
//        labels.add("M");
//        labels.add("L");
//        ArrayList<Integer> truthValues = new ArrayList<>();
//        truthValues.add(1);
//        truthValues.add(0);
//        truthValues.add(0);
//        ArrayList<Double> t = factors.get(3).getValues(labels, truthValues);
//        System.out.println(t);
        for (String label : order) {
            ArrayList<CPT> toSumOut = getFactorsContainingLabel(label, factors); // get factors containing label.
            factors.removeAll(toSumOut); // remove all factors containing label.

            // create a new factor with all variables in factors of ToSumOut but without label.
//            CPT newFactor = joinMarginalise(toSumOut, label);
//            factors.add(newFactor); // add new factor.
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



//    public CPT joinMarginalise(ArrayList<CPT> toSumOut, String label) {
//        ArrayList<String> temp = new ArrayList<>();
//        //TODO: pass in pair combinations.
//        return new CPT()
//    }

    /**
     * Put all the tables together.
     * @param first
     * @param second
     * @return
     */
    public CPT join(CPT first, CPT second) {
        // Collect all the variables without repetition from the two CPT/factors tables.
        ArrayList<String> v1 = variablesBoth(first, second); // variables in both.
        ArrayList<String> v2 = variablesNotInSecond(first, second); // variables in the first but not second.
        ArrayList<String> v3 = variablesNotInSecond(second, first); // variables in the second but not first.
        // Combine three arraylists. TODO: maybe function it.
        ArrayList<String> combined = new ArrayList<String>();
        combined.addAll(v1);
        combined.addAll(v2);
        combined.addAll(v3);

        CPT newFactor = new CPT(combined);

        System.out.println("\n\n "+ combined);
        newFactor.constructAndPrintCPT(true);

        ArrayList<Double> valuesJoined = new ArrayList<>();
        for (ArrayList<Integer> row : newFactor.getValuesMap().keySet()) {
            System.out.println("\n\nrow"+row);
            ArrayList<Double> valuesF1 = first.getValues(combined,row);
            ArrayList<Double> valuesF2 = second.getValues(combined,row);
            System.out.println(first.getNodeLabels());
            System.out.println("First: "+valuesF1);
            System.out.println(second.getNodeLabels());
            System.out.println("Second: "+valuesF2);
            System.out.println();

//            double multiplied = valuesF1 * valuesF2;
//            valuesJoined.add();


        }


        return newFactor;
    }

//    public ArrayList<ArrayList<String>> permutations(ArrayList<CPT> toSumOut) {
//        ArrayList<ArrayList<String>> outer = new ArrayList<>();
//    }

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


}
