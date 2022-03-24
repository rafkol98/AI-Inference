import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

public class CPT {
    private Node node;
    private ArrayList<String> nodeLabels;
    private ArrayList<Double> cptValues; // contains a value for each combination.

    public CPT(ArrayList<String> nodeLabels) {
        this.nodeLabels = nodeLabels;
    }

    public CPT(Node node) {
        this.nodeLabels = new ArrayList<>();
        this.node = node;
    }

    public ArrayList<String> getNodeLabels() {
        return nodeLabels;
    }

    public ArrayList<Double> getCptValues() {
        return cptValues;
    }

    public void addCPTvalues(double... values) {
        ArrayList<Node> nodesUsedForLabels = new ArrayList<>();
        nodesUsedForLabels.addAll(node.getParents());
        nodesUsedForLabels.add(node);
        // populate node labels ArrayList.
        for (Node n : nodesUsedForLabels) {
            this.nodeLabels.add(n.getLabel());
        }
        this.cptValues = DoubleStream.of(values).boxed().collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Print the CPT.
     */
    public void printCPT() {
        // Get the number of nodes in this CPT.
        int numberNodes = node.getParents().size() > 0 ? node.getParents().size() + 1 : 1;

        int size = (int) Math.pow(2, numberNodes);

        printCPTHead();

        // Create truth tables.
        for (int i = 0; i < size; i++) {
            int repeat = numberNodes - Integer.toBinaryString(i).length();

            String truths = "0".repeat(repeat) + Integer.toBinaryString(i);

            for (char c : truths.toCharArray()) {
                System.out.print(c + "\t");
            }
            System.out.println("|" + node.getCpt().getCptValues().get(i));

        }
    }


    private void printCPTHead() {
        String head = "";
        String parentsStr = "";
        // iterate until the previous to the last (last is always the element of the CPT).
        for (int i = 0; i < nodeLabels.size() - 1; i++) {
            head += nodeLabels.get(i) + "\t";
            parentsStr += nodeLabels.get(i);
            // add comma connector.
            if (i != nodeLabels.size() - 2) {
                parentsStr += ",";
            }
        }
        // handle the case when node has parents.
        if (nodeLabels.size() > 1) {
            head += node.getLabel() + "\t"; // add label in the head string.
            head += "| P(" + node.getLabel() + "|" + parentsStr.replaceAll("\\s+", ",") + ")";
            System.out.println(head);
        }
        // single element CPT.
        else {
            head += node.getLabel() + "\t";
            head += ("| P(" + node.getLabel() + ")");
            System.out.println(head);
        }

    }

    @Override
    public String toString() {
        return "CPT{" +
                "nodeLabels=" + nodeLabels +
                '}';
    }
}
