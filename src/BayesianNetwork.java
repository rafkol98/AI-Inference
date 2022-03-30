import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
            if (node.getLabel().equalsIgnoreCase(label)) {
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
            node.getCpt().constructAndPrintCPT(true);
            System.out.println("\n");
        }
    }

    //TODO: maybe move them in another class.
    /**
     * The maximum cardinality search algorithm for deciding order.
     * @return
     */
    public String[] maximumCardinalitySearch(String queriedLabel) {
        Node queried = getNode(queriedLabel); // get queried node.
        ArrayList<Node> unmarked = new ArrayList<>(nodes);
        ArrayList<Node> marked = new ArrayList<>();
        ArrayList<String> order = new ArrayList<>(); // store the order.

        // the queried node is the starting node.
        marked.add(queried);

        for (int i = 0; i < nodes.size(); i++) {
            Node labelWithMaxMarkedNeighbours = findMaximumNumberOfMarkedNeighbours(unmarked, marked);
            order.add(labelWithMaxMarkedNeighbours.getLabel());
            unmarked.remove(labelWithMaxMarkedNeighbours); // remove label with maximum number of marked neighbours from unmarked.
            marked.add(labelWithMaxMarkedNeighbours); // add label to the marked list.
        }
        Collections.reverse(order); // reverse order
        order.remove(queried.getLabel()); // remove queried label from the order list.

        return order.toArray(new String[order.size()]);
    }

    /**
     * Find the node with the maximum number of marked neighbours.
     * @param unmarked
     * @param marked
     * @return
     */
    public Node findMaximumNumberOfMarkedNeighbours(ArrayList<Node> unmarked, ArrayList<Node> marked) {
        HashMap<Node, Integer> map = new HashMap<>(); // create a new map to store the results.

        for (Node node : unmarked) {
            ArrayList<Node> allNeighbours = node.getAllNeighbours(); // Get all the neighbours of the node.
            allNeighbours.retainAll(marked); // Retain all the neighbours that are marked.
            map.put(node, allNeighbours.size());  // Store the number of marked neighbours as the map entry value.
        }
        // return the string (label) in the hashmap with the maximum key value (marked neighbours)
        return Collections.max(map.entrySet(), Map.Entry.comparingByValue()).getKey();
    }

}
