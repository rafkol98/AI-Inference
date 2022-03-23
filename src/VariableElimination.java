import java.util.ArrayList;
import java.util.Stack;

public class VariableElimination {

    // Initialise inputs.
    BayesianNetwork bn;
    Node queried;
    ArrayList<Node> order;

    public VariableElimination(BayesianNetwork bn, Node queried, ArrayList<Node> order) {
        this.bn = bn;
        this.queried = queried;
        this.order = order;

        pruneIrrelevantVariables();
        createSetFactors();
    }

    public ArrayList<Node> pruneIrrelevantVariables() {
        ArrayList<Node> anc = new ArrayList<>();
        Stack<Node> ancestors = new Stack<>();
        ancestors.push(queried);
        Node currentNode = queried;

        boolean top = false; // flag to see if we reached the top ancestor.
        while (!ancestors.isEmpty()) {
            // add all ancestors to the stack.
            if (currentNode.getParents().size() != 0) {
                for (Node node: currentNode.getParents()) {
                    ancestors.push(node);
                    anc.add(node);
                }
            }

            currentNode = ancestors.pop();
        }

        order.retainAll(anc); // retain all elements identified as ancestors.
        return anc;
    }

    public void createSetFactors() {

    }


}
