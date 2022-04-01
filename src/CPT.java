import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

/**
 * Create a new CPT table.
 * @author: 210017984
 */
public class CPT {

    // initialise variables.
    private Node correspondingNode;
    private String nodeGivenLabel; // label of the given node.
    private ArrayList<String> nodeLabels;
    private ArrayList<Double> cptValues; // contains a value for each combination.
    private LinkedHashMap<ArrayList<Integer>, Double> valuesMap;
    private VariableElimination ve;

    /**
     * Create a new CPT.
     */
    public CPT() {
        this.nodeLabels = new ArrayList<>();
        this.valuesMap = new LinkedHashMap<>();
    }

    /**
     * Create a deep copy of an existing CPT.
     */
    public CPT(CPT copy) {
        this.correspondingNode = copy.correspondingNode;
        this.nodeGivenLabel = copy.nodeGivenLabel;
        this.nodeLabels = copy.nodeLabels;
        this.cptValues = copy.cptValues;
        this.valuesMap = copy.valuesMap;
        populateMap();
    }

    /**
     * Create a new CPT given a node label. Used to create temporary CPTs
     * in the join operation.
     * @param nodeGivenLabel a node's label.
     */
    public CPT(String nodeGivenLabel) {
        this.nodeLabels = new ArrayList<>();
        this.valuesMap = new LinkedHashMap<>();
        this.nodeGivenLabel = nodeGivenLabel;
    }

    /**
     * Create a new CPT associated belonging to a given node.
     * @param correspondingNode the node that the CPT table will
     *                          belong to.
     */
    public CPT(Node correspondingNode) {
        this.nodeLabels = new ArrayList<>();
        this.valuesMap = new LinkedHashMap<>();
        this.correspondingNode = correspondingNode;
        this.nodeGivenLabel = correspondingNode.getLabel();
    }

    /**
     * Set node labels to the CPT.
     * @param nodeLabels the node labels of the CPT
     */
    public void setNodeLabels(ArrayList<String> nodeLabels) {
        this.nodeLabels = nodeLabels;
    }

    /**
     * Get corresponding (owning) node of the CPT.
     * @return corresponding node of the CPT.
     */
    public Node getCorrespondingNode() {
        return correspondingNode;
    }

    /**
     * Get CPT's node labels.
     * @return the labels of the CPT.
     */
    public ArrayList<String> getNodeLabels() {
        return nodeLabels;
    }

    /**
     * Get CPT's values.
     * @return the values of the CPT.
     */
    public ArrayList<Double> getCptValues() {
        return cptValues;
    }

    /**
     * Add CPT values both in the arraylist and the truth - value map. Used mainly
     * when creating a new Bayesian Network - add multiple values at once without ArrayList.
     * @param values the values to be added to the CPT.
     */
    public void addCPTvalues(double... values) {
        ArrayList<Node> nodesUsedForLabels = new ArrayList<>();
        //
        nodesUsedForLabels.addAll(correspondingNode.getParents());
        nodesUsedForLabels.add(correspondingNode);
        // populate node labels ArrayList.
        for (Node n : nodesUsedForLabels) {
            this.nodeLabels.add(n.getLabel());
        }
        this.cptValues = DoubleStream.of(values).boxed().collect(Collectors.toCollection(ArrayList::new));
        populateMap();
    }

    /**
     * Add CPT values in the form of an ArrayList both in the arraylist and the truth - value map.
     * @param values the values to be added to the CPT.
     */
    public void addCPTvalues(ArrayList<Double> values) {
        this.cptValues = values;
        populateMap();
    }

    /**
     * Update the CPT values both in the arraylist and the map.
     * @param newValues the new values to be updated.
     */
    public void updateCPTvalues(ArrayList<Double> newValues) {
        for (int i=0; i<newValues.size(); i++) {
            cptValues.set(i,newValues.get(i));
        }
        populateMap();
    }

    /**
     * Get a seeked truth value for the corresponding's node marginalised
     * CPT table - this is only used for gibbs sampling.
     * @param value the value we want - can be either 1 or 0 (binary).
     * @return the CPT probability.
     */
    public double getCorrespondingNodeTruthValue(int value) {
        ve = new VariableElimination();
        int index = nodeLabels.indexOf(correspondingNode.getLabel()); // get index of node label.

        ArrayList<String> nodeLabelsWithoutCorresponding = new ArrayList<>(nodeLabels);
        nodeLabelsWithoutCorresponding.remove(correspondingNode.getLabel());
        CPT tempCPT = new CPT(this);

        // Marginalise the table so that only the own probability remains.
        for (String label : nodeLabelsWithoutCorresponding) {
            tempCPT = ve.marginalise(tempCPT, label);
        }
        tempCPT.normalize(); // normalize values.

        return tempCPT.getCPTSingleProb(value);
    }

    /**
     * Set CPT values to zeros according to evidence.
     * @param changeTrueVals if true sets all the false values to zero, and
     *                       vice versa.
     */
    public void setToZero(boolean changeTrueVals) {
        if (changeTrueVals) {
            // update to zero when Evidence is equal to true.
            for (int i = 0; i < cptValues.size() - 1; i += 2) {
                // update the value to 0.
                cptValues.set(i, 0.0);
            }
        } else {
            // update to zero when Evidence is equal to false.
            for (int i = 1; i < cptValues.size(); i += 2) {
                // update the value to 0.
                cptValues.set(i, 0.0);
            }
        }
        // update map.
        populateMap();
    }


    /**
     * Populate the truth combination - value map.
     */
    private void populateMap() {
        // Get the number of nodes in this CPT and the size of rows.
        int numberNodes = nodeLabels.size();
        int size = (int) Math.pow(2, numberNodes);

        // Create truth tables.
        for (int i = 0; i < size; i++) {
            // ArrayList stores all the values for the current column. Used as key for the HashMap
            ArrayList<Integer> valuesTableRow = new ArrayList<>();
            int repeat = numberNodes - Integer.toBinaryString(i).length();

            String truths = "0".repeat(repeat) + Integer.toBinaryString(i);

            for (char c : truths.toCharArray()) {
                // add the character (0 or 1) for the specific element in the ArrayList.
                valuesTableRow.add(Character.getNumericValue(c));
            }

            // add/update CPT values.
            double nodeValue = cptValues.get(i);
            valuesMap.put(valuesTableRow, nodeValue);
        }
    }

    /**
     * Print the CPT.
     */
    //TODO: redo the whole CPT printing. MAKE IT ONLY PRINTING
    public void constructAndPrintCPT(boolean print) {
        // Get the number of nodes in this CPT.
        int numberNodes = nodeLabels.size();
        int size = (int) Math.pow(2, numberNodes);

        if (print) {
            if (cptValues!= null) {
                printCPTHead();
            }
        }

        // Create truth tables.
        for (int i = 0; i < size; i++) {
            // ArrayList stores all the values for the current column. Used as key for the HashMap
            ArrayList<Integer> valuesTableRow = new ArrayList<>();

            int repeat = numberNodes - Integer.toBinaryString(i).length();

            String truths = "0".repeat(repeat) + Integer.toBinaryString(i);

            for (char c : truths.toCharArray()) {
                if (print) {
                    System.out.print(c + "\t");  // print the char value.
                }
                // add the character (0 or 1) for the specific element in the ArrayList.
                valuesTableRow.add(Character.getNumericValue(c));
            }

            //TODO: remove this - only useful for debug
            if (cptValues != null) {
                double nodeValue = cptValues.get(i);
                if (print) {

                    System.out.println("|" + nodeValue); // print node value.
                }
                valuesMap.put(valuesTableRow, nodeValue);
            } else {
                if (print) {
                   System.out.println("|" + 0);
                    valuesMap.put(valuesTableRow, 0.0);
                }
            }
        }
    }

    //TODO: redo the whole CPT printing.
    private void printCPTHead() {
        String head = "";
        String parentsStr = "";

        int toRemove = correspondingNode == null ? 0 : 1;
        // iterate until the previous to the last (last is always the element of the CPT).
        for (int i = 0; i < nodeLabels.size() - toRemove; i++) {
            head += nodeLabels.get(i) + "\t";
            parentsStr += nodeLabels.get(i);
            // add comma connector.
            if (i != nodeLabels.size() - 2) {
                parentsStr += ",";
            }
        }

        // handle the case when node has parents.
        if (nodeLabels.size() > 1) {
            head += nodeGivenLabel + "\t"; // add label in the head string.
            head += "| P(" + nodeGivenLabel + "|" + parentsStr.replaceAll("\\s+", ",") + ")";
            System.out.println(head);
        }
        // single element CPT.
        else {
            head += nodeGivenLabel + "\t";
            head += ("| P(" + nodeGivenLabel + ")");
            System.out.println(head);
        }
    }

    /**
     * Get a single probability in a CPT with only one variable.
     * @param truth the value we are looking (1 or 0 - binary).
     * @return
     */
    public double getCPTSingleProb(int truth) {
        ArrayList<Integer> temp = new ArrayList<>();
        temp.add(truth);
        return valuesMap.get(temp);
    }

    /**
     * Get CPT probability for a combination fo truth values - used when multiple
     * variable combinations in the CPT table.
     * @param truthValues
     * @return
     */
    public double getCPTProbability(ArrayList<Integer> truthValues) {
        populateMap();
        return valuesMap.get(truthValues);
    }


    @Override
    public String toString() {
        return "CPT{" +
                "nodeLabels=" + nodeLabels +
                '}';
    }

    //TODO: change/improve!!!
    public ArrayList<ArrayList<Integer>> getCombinations() {
        ArrayList<ArrayList<Integer>> trueValues = new ArrayList<>();
        //
        int size = (int) Math.pow(2, nodeLabels.size());

        // Add empty lists to the list of lists.
        for (int c = 0; c < size; c++) {
            trueValues.add(new ArrayList<>());
        }
        int space = size / 2;
        int prevSpace = size;
        for (String label : nodeLabels) {
            int counter = 0;
            for (int i = 0; i < size; i++) {
                if (counter < space) {
                    trueValues.get(i).add(1);
                } else {
                    trueValues.get(i).add(0);
                }
                counter++;
                if (counter == prevSpace) {
                    counter = 0;
                }
            }
            prevSpace = space;
            space = space / 2;
        }
        return trueValues;
    }

    /**
     * Normalise the variables. Used to make probabilities in a table sum up to one.
     */
    public void normalize() {
        // store the normalized values.
        ArrayList<Double> normalizedValues = new ArrayList<>();

        double trueValue = getCPTSingleProb(1);
        double falseValue = getCPTSingleProb(0);
        double sumValue = trueValue + falseValue;

        normalizedValues.add(falseValue / sumValue); // add normalised false value.
        normalizedValues.add(trueValue / sumValue); // add normalized true value.

        // update joined CPT values.
        updateCPTvalues(normalizedValues);
    }

}
