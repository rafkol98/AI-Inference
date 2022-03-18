import java.util.ArrayList;

public class BayesianNetwork {
    private ArrayList<Node> nodes;
    private ArrayList<Edge> edges;

    public void addNode(String label, double ... vals) {
        Node node = new Node(label, vals);
        nodes.add(node);
    }

    public void addEdge(Node first, Node second) {
        Edge edge = new Edge(first, second);
        edges.add(edge);
    }


}
