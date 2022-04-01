/**
 * Create a new edge connection between two nodes.
 * @author: 210017984
 */
public class Edge {

    // initialise variables.
    private Node first;
    private Node second;

    /**
     * Create a new Edge connection between two nodes.
     * @param first the first node in the edge connection.
     * @param second the second node in the edge connection.
     */
    public Edge(Node first, Node second) {
        this.first = first;
        this.second = second;
        second.addParent(first); // add second as parent.
        first.addChild(second); // add first as child.
    }

    /**
     * Get first node in the connection.
     * @return first node in the edge connection.
     */
    public Node getFirst() {
        return first;
    }

    /**
     * Get second node in the connection.
     * @return second node in the edge connection.
     */
    public Node getSecond() {
        return second;
    }
}
