import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

public class CPT {
    private Node node;
    private String nodeGivenLabel; // label of the given node.
    private ArrayList<String> nodeLabels;
    private ArrayList<Double> cptValues; // contains a value for each combination.
    private LinkedHashMap<ArrayList<Integer>, Double> valuesMap;

    public CPT() {
        this.nodeLabels = new ArrayList<>();
        this.valuesMap = new LinkedHashMap<>();
    }

    public CPT(String nodeGivenLabel) {
        this.nodeLabels = new ArrayList<>();
        this.valuesMap = new LinkedHashMap<>();
        this.nodeGivenLabel = nodeGivenLabel;
    }

    public CPT(Node node) {
        this.nodeLabels = new ArrayList<>();
        this.valuesMap = new LinkedHashMap<>();
        this.node = node;
        this.nodeGivenLabel = node.getLabel();
    }

    public void setNodeLabels(ArrayList<String> nodeLabels) {
        this.nodeLabels = nodeLabels;
    }

    public HashMap<ArrayList<Integer>, Double> getValuesMap() {
        return valuesMap;
    }

    public ArrayList<String> getNodeLabels() {
        return nodeLabels;
    }

    public ArrayList<Double> getCptValues() {
        return cptValues;
    }

    public void addCPTvalues(ArrayList<Double> values) {
        this.cptValues = values;
        populateMap();
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

    private void populateMap() {
        // Get the number of nodes in this CPT.
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

            double nodeValue = cptValues.get(i);
            valuesMap.put(valuesTableRow, nodeValue);
        }
    }

    /**
     * Print the CPT.
     */
    public void constructAndPrintCPT(boolean print) {
        System.out.println("NL:: "+nodeLabels);
        System.out.println("Node of: "+node);
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

    public double getCPTProbability(ArrayList<Integer> truthValues) {
        return valuesMap.get(truthValues);
    }


    @Override
    public String toString() {
        return "CPT{" +
                "nodeLabels=" + nodeLabels +
                '}';
    }

    //TODO: remove - only used for debug.
    public void printMap() {
        System.out.println(nodeLabels);
        System.out.println(valuesMap);
    }


    //TODO: change!!!
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

}
