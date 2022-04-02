import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Ordering {
    ArrayList<Node> nodes;
    ArrayList<Edge> edges;

    public Ordering(ArrayList<Node> nodes, ArrayList<Edge> edges) {
        this.nodes = nodes;
        this.edges = edges;
    }

    /**
     * The maximum cardinality search algorithm for deciding order.
     * @param queriedLabel the label of the queried label.
     * @return the best visiting order derived with this algorithm.
     */
    public String[] maximumCardinalitySearch(String queriedLabel) {
        // we are using the bayesian network as an undirected graph as we are working with neighbours, not children
        // and parents. Here there is no point in using a temporary BN as we are not adding new edges.
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


    /**
     * Greedy edge algorithm.
     * @param queriedLabel the label we are querying.
     * @return the best order derived
     */
    public String[] greedyMinEdgesSearch(String queriedLabel) {
        // we are using the bayesian network as an undirected graph as we are working with neighbours, not children
        // and parents. Here there is no point in using a temporary BN as we are not adding new edges.
        ArrayList<Edge> undirectedGraph = new ArrayList<>(edges);

        Node queried = getNode(queriedLabel); // get queried node.
        ArrayList<Node> unmarked = new ArrayList<>(nodes);
        ArrayList<Node> marked = new ArrayList<>();
        ArrayList<String> order = new ArrayList<>(); // store the order.

        // the queried node is the starting node.
        marked.add(queried);

        for (int i = 0; i < nodes.size(); i++) {
            Node labelWithMinNeighbours = findNodeWithMinimumNeighbours(unmarked, marked);
            order.add(labelWithMinNeighbours.getLabel());
            System.out.println("NODE: "+ labelWithMinNeighbours.getLabel());
            unmarked.remove(labelWithMinNeighbours); // remove label with maximum number of marked neighbours from unmarked.
            marked.add(labelWithMinNeighbours); // add label to the marked list.

            // if i is not the last, then generate possible link pairs.
            if (i != nodes.size()-1) {
                generateLinksPossiblePairs(undirectedGraph, labelWithMinNeighbours.getAllNeighbours());
            }

        }
        order.remove(queried.getLabel()); // remove queried label from the order list.

        return order.toArray(new String[order.size()]);
    }

    /**
     * Find the node with the minimum number of neighbours.
     * @param unmarked
     * @param marked
     * @return
     */
    public Node findNodeWithMinimumNeighbours(ArrayList<Node> unmarked, ArrayList<Node> marked) {
        HashMap<Node, Integer> map = new HashMap<>(); // create a new map to store the results.
        // Iterate through the unmarked neighbours.
        for (Node node : unmarked) {
            ArrayList<Node> allNeighbours = node.getAllNeighbours(); // Get all the neighbours of the node.
            map.put(node, allNeighbours.size());  // Store the number of marked neighbours as the map entry value.
        }
        // return the string (label) in the hashmap with the minimum key value (marked neighbours).
        return Collections.min(map.entrySet(), Map.Entry.comparingByValue()).getKey();
    }


    /**
     * Generate all the links for the possible pairs.
     * @param undirected
     * @param neighbours
     */
    public void generateLinksPossiblePairs(ArrayList<Edge> undirected, ArrayList<Node> neighbours) {
        for (int i=0; i<neighbours.size();i++) {
            for (int x=1; x<neighbours.size()-1; x++) {
                Node first = neighbours.get(i);
                Node second = neighbours.get(x);

                // if the graph does not contain a link between the two parents already, create one.
                if (!(containEdge(undirected, first, second))) {
                    System.out.println("ADDING EDGE: nei i:"+neighbours.get(i)+" nei x:"+ neighbours.get(x));
                    // ordering does not matter here! we are working with the neighbours of a node.
                    Edge newEdge = new Edge(first,second);
                    undirected.add(newEdge);
                }
            }
        }
    }

    /**
     * Get a specific node.
     * @param nodeLabel
     * @return
     */
    public Node getNode(String nodeLabel) {
        for (Node node : nodes) {
            if (node.getLabel().equalsIgnoreCase(nodeLabel)) {
                return node;
            }
        }
        return null;
    }

    /**
     * Whether it contains an edge.
     * @param edgesIn
     * @param first
     * @param second
     * @return
     */
    public boolean containEdge(ArrayList<Edge> edgesIn, Node first, Node second) {
        for (Edge edge : edgesIn) {
            if ((edge.getFirst().getLabel().equals(first.getLabel()) && edge.getSecond().getLabel().equals(second.getLabel())) || (edge.getFirst().getLabel().equals(second.getLabel()) && edge.getSecond().getLabel().equals(first.getLabel()))) {
                return true;
            }
        }
        return false;
    }
}
