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
     * @param vals  the values to put in the CPT.
     */
    public Node(String label) {
        parents = new ArrayList<>();
        children = new ArrayList<>();
        this.label = label;
    }

    public void addCPTvalues(double... vals) {
        ArrayList<Node> nodesForLabels = (ArrayList<Node>) parents.clone();
        nodesForLabels.add(this);
        System.out.println("before"+nodesForLabels);
        this.cpt = new CPT(nodesForLabels, vals); // create CPT given the values.
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

    /**
     * Print the CPT.
     */
    public void printCPT() {
        // Get the number of nodes in this CPT.
        int numberNodes = parents.size() > 0 ? parents.size() + 1 : 1;

        int size = (int) Math.pow(2, numberNodes);

        printCPTHead();

        // Create truth tables.
        for (int i = 0; i < size; i++) {
            int repeat = numberNodes - Integer.toBinaryString(i).length();

            String truths = "0".repeat(repeat) + Integer.toBinaryString(i);

            for (char c : truths.toCharArray()) {
                System.out.print(c + "\t");
            }
            System.out.println("|" + cpt.getCptValues().get(i));

        }
    }

    private void printCPTHead() {
        //TODO use the getNodeLabels in CPT!
        String head = "";
        String parentsStr = "";
        // Add parents in the head string.
        for (int i = 0; i < getParents().size(); i++) {
            head += getParents().get(i).label + "\t";
            parentsStr += getParents().get(i).label;
            if (i != getParents().size() - 1) {
                parentsStr += ",";
            }
        }
        if (getParents().size() > 0) {
            head += label + "\t"; // add label in the head string.
            head += "| P(" + label + "|" + parentsStr.replaceAll("\\s+", ",") + ")";
            System.out.println(head);
        } else {
            head += label + "\t";
            head +=("| P(" + label+")");
            System.out.println(head);
        }

    }

    @Override
    public String toString() {
        return "Node{" +
                label +
                '}';
    }

}
