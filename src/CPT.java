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

    public CPT(ArrayList<Node> nodeLabels, double ... values) {
        this.nodeLabels = new ArrayList<>();

        // get the cpt values.
        this.cptValues = DoubleStream.of(values).boxed().collect(Collectors.toCollection(ArrayList::new));
        // get the node labels.

        for (Node node: nodeLabels) {
            this.nodeLabels.add(node.getLabel());
        }

        System.out.println("dame"+nodeLabels);
    }

}
