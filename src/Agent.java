import java.awt.*;
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

        for (String label : order) {
            ArrayList<CPT> toSumOut = getFactorsContainingLabel(label, factors); // get factors containing label.
            factors.removeAll(toSumOut); // remove all factors containing label.
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
            node.printCPT(); // print the CPT.
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
        System.out.println("MEOW: " + label + " " + toSumOut + "\n\n");
        return toSumOut;
    }

//    public CPT joinMarginalise(ArrayList<CPT> toSumOut, String label) {
//
//    }

    public 


    public CPT join(CPT first, CPT second) {
        // collect all the variables without repetition from the two CPT/factors tables.
        ArrayList<String> v1 = variablesBoth(first, second); // variables in both.
        ArrayList<String> v2 = variablesNotInSecond(first, second); // variables in the first but not second.
        ArrayList<String> v3 = variablesNotInSecond(second, first); // variables in the second but not first.
        // Combine three arraylists. TODO: maybe function it.
        ArrayList<String> combined = new ArrayList<String>();
        combined.addAll(v1);
        combined.addAll(v2);
        combined.addAll(v3);

        CPT f3 = new CPT(combined);
//        first


    }

//    public ArrayList<ArrayList<String>> permutations(ArrayList<CPT> toSumOut) {
//        ArrayList<ArrayList<String>> outer = new ArrayList<>();
//    }


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
