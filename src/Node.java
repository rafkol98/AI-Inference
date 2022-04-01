import java.util.ArrayList;
import java.util.Objects;

public class Node {
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

    public String getLabel() {
        return label;
    }

    public ArrayList<Node> getParents() {
        return parents;
    }

    public ArrayList<Node> getChildren() {
        return children;
    }

    public CPT getCpt() {
        return cpt;
    }

    public void addParent(Node parent) {
        parents.add(parent);
    }

    public void addChild(Node child) {
        children.add(child);
    }

    public ArrayList<Node> getCoParents() {
        ArrayList<Node> coParents = new ArrayList<>();
        for (Node child : children) {
            ArrayList<Node> childParents = new ArrayList<>(child.getParents()); // make a copy of the child's parents.
            childParents.remove(this); // remove current node from te childParents list - as we only want the co-parents.
            coParents.addAll(childParents);
        }
        return coParents;
    }

    /**
     * Get all the neighbours (parents and childrens) of the node.
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

    @Override
    public String toString() {
        return "Node{" +
                label +
                '}';
    }

}
