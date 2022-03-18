import java.util.ArrayList;

public class BayesianNetwork {
    private ArrayList<Node> nodes;
    private ArrayList<Edge> edges;

    /**
     * Add a node to the BN.
     * @param label
     * @param vals
     */
    public void addNode(String label, double ... vals) {
        Node node = new Node(label, vals);
        nodes.add(node);
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


}
