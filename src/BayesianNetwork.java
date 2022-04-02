import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Create a new Bayesian Network instance.
 * @author: 210017984
 */
public class BayesianNetwork {

    // initialise variables.
    private ArrayList<Node> nodes;
    private ArrayList<Edge> edges;

    /**
     * Create a copy of a Bayesian Network.
     */
    public BayesianNetwork(BayesianNetwork copy) {
        this.nodes = copy.nodes;
        this.edges = copy.edges;
    }

    /**
     * Create a new Bayesian Network.
     */
    public BayesianNetwork() {
        this.nodes = new ArrayList<>();
        this.edges = new ArrayList<>();
    }

    /**
     * Get all the nodes of the bayesian network.
     * @return the BN nodes.
     */
    public ArrayList<Node> getNodes() {
        return nodes;
    }

    /**
     * Get the edges of a network.
     * @return
     */
    public ArrayList<Edge> getEdges() {
        return edges;
    }



    /**
     * Get a node in the bayesian network given its label.
     * @param label the label of the node.
     * @return the node in the bayesian network.
     */
    public Node getNode(String label) {
        for (Node node : nodes) {
            if (node.getLabel().equalsIgnoreCase(label)) {
                return node;
            }
        }
        return null;
    }

    /**
     * Creates and adds a new node to the Bayesian Network.
     * @param label the label of the new node to be added.
     */
    public Node addNode(String label) {
        Node node = new Node(label);
        nodes.add(node);
        return node;
    }

    /**
     * Add an edge to the BN.
     * @param first the first node in the edge.
     * @param second the second node in the edge.
     */
    public void addEdge(Node first, Node second) {
        Edge edge = new Edge(first, second);
        edges.add(edge);
    }

    /**
     * Print all the CPT tables in the network.
     */
    public void printNetwork() {
        for (Node node : nodes) {
            node.getCpt().constructAndPrintCPT(true);
            System.out.println("\n");
        }
    }

}
