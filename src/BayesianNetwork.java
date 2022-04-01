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

    /**
     * The maximum cardinality search algorithm for deciding order.
     * @param queriedLabel the label of the queried label
     * @return the best visiting order derived with this algorithm.
     */
    public String[] maximumCardinalitySearch(String queriedLabel) {
        Node queried = getNode(queriedLabel); // get queried node.
        ArrayList<Node> unmarked = new ArrayList<>(nodes);
        ArrayList<Node> marked = new ArrayList<>();
        ArrayList<String> order = new ArrayList<>(); // store the order.

        // the queried node is the starting node.
        marked.add(queried);

        // iterate through all the network's nodes and mark every time the label with the maximum marked neighbours.
        for (int i = 0; i < nodes.size(); i++) {
            Node labelWithMaxMarkedNeighbours = findMaximumNumberOfMarkedNeighbours(unmarked, marked);
            order.add(labelWithMaxMarkedNeighbours.getLabel());
            unmarked.remove(labelWithMaxMarkedNeighbours); // remove label with maximum number of marked neighbours from unmarked.
            marked.add(labelWithMaxMarkedNeighbours); // add label to the marked list.
        }
        Collections.reverse(order); // reverse order
        order.remove(queriedLabel); // remove queried label from the order list.

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


    //TODO
//    public String[] greedyMinEdgesSearch(String queriedLabel) {
//        Node queried = getNode(queriedLabel); // get queried node.
//        ArrayList<Node> unmarked = new ArrayList<>(nodes);
//        ArrayList<Node> marked = new ArrayList<>();
//        ArrayList<String> order = new ArrayList<>(); // store the order.
//
//        // the queried node is the starting node.
//        marked.add(queried);
//
//        for (int i = 0; i < nodes.size(); i++) {
//            Node labelWithMaxMarkedNeighbours = findMaximumNumberOfMarkedNeighbours(unmarked, marked);
//            order.add(labelWithMaxMarkedNeighbours.getLabel());
//            unmarked.remove(labelWithMaxMarkedNeighbours); // remove label with maximum number of marked neighbours from unmarked.
//            marked.add(labelWithMaxMarkedNeighbours); // add label to the marked list.
//        }
//        Collections.reverse(order); // reverse order
//        order.remove(queried.getLabel()); // remove queried label from the order list.
//
//        return order.toArray(new String[order.size()]);
//    }



}
