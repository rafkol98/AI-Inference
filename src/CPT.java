import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

public class CPT {
    private Node node;
    private ArrayList<String> nodeLabels;
    private ArrayList<Double> cptValues; // contains a value for each combination.

    public ArrayList<String> getNodeLabels() {
        return nodeLabels;
    }

    public ArrayList<Double> getCptValues() {
        return cptValues;
    }

    public CPT(ArrayList<String> nodeLabels) {
        this.nodeLabels = nodeLabels;
    }

    public CPT(Node node) {
        this.nodeLabels = new ArrayList<>();
        this.node = node;
    }

    public void addCPTvalues(double ... values) {
        ArrayList<Node> nodesUsedForLabels = new ArrayList<>();
        nodesUsedForLabels.addAll(node.getParents());
        nodesUsedForLabels.add(node);
        // populate node labels ArrayList.
        for (Node n: nodesUsedForLabels) {
            System.out.println("excuted");
            this.nodeLabels.add(n.getLabel());
        }
        this.cptValues = DoubleStream.of(values).boxed().collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public String toString() {
        return "CPT{" +
                "nodeLabels=" + nodeLabels +
                '}';
    }
}
