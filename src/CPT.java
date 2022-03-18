import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

public class CPT {
    private String nodeLabel;
    private ArrayList<String> multipleNodesLabels = new ArrayList<>(); // the labels of the nodes used in this CPT.
    private ArrayList<Double> cptValues = new ArrayList<>(); // contains a value for each combination.

    /**
     * CPT for a single node.
     * @param nodeLabel
     */
    public CPT(String nodeLabel, double ... values) {
        this.nodeLabel = nodeLabel;
        this.cptValues = DoubleStream.of(values).boxed().collect(Collectors.toCollection(ArrayList::new));
    }

    //TODO: need to pass the parents.
    /**
     * CPT for multiple nodes.
     * @param multipleNodesLabels
     * @param values
     */
    public CPT(ArrayList<String> multipleNodesLabels, double ... values) {
        this.multipleNodesLabels = multipleNodesLabels;
        this.cptValues = DoubleStream.of(values).boxed().collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Print the CPT.
     */
    public void printCPT() {
        // Get the number of nodes in this CPT.
        int numberNodes =  multipleNodesLabels.size() > 0 ? multipleNodesLabels.size() :  1;

        int size = (int) Math.pow(2, numberNodes);
        // Create truth tables.
        for (int i = 0; i < size; i++) {
            int repeat = numberNodes - Integer.toBinaryString(i).length();

            String truths = "0".repeat(repeat) + Integer.toBinaryString(i);

            for (char c : truths.toCharArray()) {
                System.out.print(c + "\t");
            }
            System.out.println("|" + cptValues.get(i));

        }
    }

}
