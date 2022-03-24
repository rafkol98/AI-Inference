import java.util.ArrayList;

public class BayesianNetwork {
    private ArrayList<Node> nodes;
    private ArrayList<Edge> edges;

    public BayesianNetwork() {
        nodes = new ArrayList<>();
        edges = new ArrayList<>();
    }

    public ArrayList<Node> getNodes() {
        return nodes;
    }

    public Node getNode(String label) {
        for (Node node : nodes) {
            if (node.getLabel() == label) {
                return node;
            }
        }
        return null;
    }

    /**
     * Add a node to the BN.
     * @param label
     */
    public Node addNode(String label) {
        Node node = new Node(label);
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
            node.getCpt().printCPT();
            System.out.println("\n");
        }
    }

}
