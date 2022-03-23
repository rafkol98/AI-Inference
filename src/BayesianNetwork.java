import java.util.ArrayList;

public class BayesianNetwork {
    private ArrayList<Node> nodes;
    private ArrayList<Edge> edges;

    public BayesianNetwork() {
        nodes = new ArrayList<>();
        edges = new ArrayList<>();
    }

    /**
     * Add a node to the BN.
     * @param label
     * @param vals
     */
    public Node addNode(String label, double ... vals) {
        Node node = new Node(label, vals);
        nodes.add(node);
        return node;
    }

    /**
     * Add an edge to the BN.
     * @param first
     * @param second
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
            node.printCPT();
            System.out.println("\n");
        }
    }




}
