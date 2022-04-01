import java.util.ArrayList;
import java.util.Objects;

/**
 * Create a new Node of a BN.
 * @author: 210017984
 */
public class Node {

    // Initialise variables.
    private ArrayList<Node> parents;
    private ArrayList<Node> children;
    private String label;
    private CPT cpt;

    /**
     * Create a new node.
     *
     * @param label the label of the node.
     */
    public Node(String label) {
        parents = new ArrayList<>();
        children = new ArrayList<>();
        this.label = label;
        this.cpt = new CPT(this);
    }

    /**
     * Get label of the node.
     * @return label of the node.
     */
    public String getLabel() {
        return label;
    }

    /**
     * Get all the node's parents.
     * @return node's parents.
     */
    public ArrayList<Node> getParents() {
        return parents;
    }

    /**
     * Get all the node's children.
     * @return node's children.
     */
    public ArrayList<Node> getChildren() {
        return children;
    }

    /**
     * Get the cpt table associated with this node.
     * @return the CPT table of this node.
     */
    public CPT getCpt() {
        return cpt;
    }

    /**
     * Add a new parent node.
     * @param parent the node to be added as a parent to this node.
     */
    public void addParent(Node parent) {
        parents.add(parent);
    }

    /**
     * Add a new child node.
     * @param child the node to be added as child.
     */
    public void addChild(Node child) {
        children.add(child);
    }

    /**
     * Get all the co-parents of the node (for all of its children).
     * @return the co-parents of all children nodes.
     */
    public ArrayList<Node> getCoParents() {
        ArrayList<Node> coParents = new ArrayList<>();
        // iterate through the children of the node and add its co-parents.
        for (Node child : children) {
            ArrayList<Node> childParents = new ArrayList<>(child.getParents()); // make a copy of the child's parents.
            childParents.remove(this); // remove current node from te childParents list - as we only want the co-parents.
            coParents.addAll(childParents);
        }
        return coParents;
    }

    /**
     * Get all the neighbours (parents, childrens, and co-parents) of the node.
     * @return
     */
    public ArrayList<Node> getAllNeighbours() {
        ArrayList<Node> allNeighbours = new ArrayList<>();
        // add both children and parents as all neighbours of the node.
        allNeighbours.addAll(children);
        allNeighbours.addAll(parents);
        allNeighbours.addAll(getCoParents()); // add all the co-parents.
        return allNeighbours;
    }

    /**
     * Prints a node to a String - mostly used for debugging/development
     * purposes.
     * @return the node as a string including its label.
     */
    @Override
    public String toString() {
        return "Node{" +
                label +
                '}';
    }

}
