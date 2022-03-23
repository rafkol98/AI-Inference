import java.util.ArrayList;

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
    public Node(String label, double... vals) {
        parents = new ArrayList<>();
        children = new ArrayList<>();
        this.label = label;
        this.cpt = new CPT(vals); // create CPT given the values.
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

    public void printCPTHead() {
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
//        head += parentsStr;
        head += label + "\t"; // add label in the head string.
        head += "| P(" + label + "|" + parentsStr.replaceAll("\\s+", ",") + ")";
        System.out.println(head);
    }

    @Override
    public String toString() {
        return "Node{" +
                label + '\'' +
                '}';
    }
}
