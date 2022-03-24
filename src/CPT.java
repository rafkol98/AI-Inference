import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

public class CPT {
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
        ArrayList<Node> nodesUsedForLabels = new ArrayList<>();
        nodesUsedForLabels.addAll(node.getParents());
        nodesUsedForLabels.add(node);
        // populate node labels ArrayList.
        for (Node n: nodesUsedForLabels) {
            this.nodeLabels.add(n.getLabel());
        }
    }

    public void addCPTvalues(double ... values) {
        this.cptValues = DoubleStream.of(values).boxed().collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public String toString() {
        return "CPT{" +
                "nodeLabels=" + nodeLabels +
                '}';
    }
}
