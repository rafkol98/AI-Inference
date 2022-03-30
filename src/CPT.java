import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

public class CPT {
    private Node correspondentNode;
    private String nodeGivenLabel; // label of the given node.
    private ArrayList<String> nodeLabels;
    private ArrayList<Double> cptValues; // contains a value for each combination.
    private LinkedHashMap<ArrayList<Integer>, Double> valuesMap;

    public CPT() {
        this.nodeLabels = new ArrayList<>();
        this.valuesMap = new LinkedHashMap<>();
    }

    // Deep Copy CPT.
    public CPT(CPT copy) {
        this.correspondentNode = copy.correspondentNode;
        this.nodeGivenLabel = copy.nodeGivenLabel;
        this.nodeLabels = copy.nodeLabels;
        this.cptValues = copy.cptValues;
        this.valuesMap = copy.valuesMap;
        populateMap();
    }

    public CPT(String nodeGivenLabel) {
        this.nodeLabels = new ArrayList<>();
        this.valuesMap = new LinkedHashMap<>();
        this.nodeGivenLabel = nodeGivenLabel;
    }

    public CPT(Node correspondentNode) {
        this.nodeLabels = new ArrayList<>();
        this.valuesMap = new LinkedHashMap<>();
        this.correspondentNode = correspondentNode;
        this.nodeGivenLabel = correspondentNode.getLabel();
    }

    public void updateCPTvalues(ArrayList<Double> newValues) {
        for (int i=0; i<newValues.size(); i++) {
            cptValues.set(i,newValues.get(i));
        }
        populateMap();
    }

    public void setNodeLabels(ArrayList<String> nodeLabels) {
        this.nodeLabels = nodeLabels;
    }

    public Node getCorrespondentNode() {
        return correspondentNode;
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

    /**
     * Set CPT values to zeros accordingly to evidence.
     * @param changeTrueVals
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

    public void addCPTvalues(double... values) {
        ArrayList<Node> nodesUsedForLabels = new ArrayList<>();
        nodesUsedForLabels.addAll(correspondentNode.getParents());
        nodesUsedForLabels.add(correspondentNode);
        // populate node labels ArrayList.
        for (Node n : nodesUsedForLabels) {
            this.nodeLabels.add(n.getLabel());
        }
        this.cptValues = DoubleStream.of(values).boxed().collect(Collectors.toCollection(ArrayList::new));
    }

    private void populateMap() {
        // Get the number of nodes in this CPT.
//        System.out.println("NODE LABELS: "+ nodeLabels);
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

//            System.out.println("INDEX "+ i + " OF SIZE: "+ size);
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


        int toRemove = correspondentNode == null ? 0 : 1;
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

    //TODO: improve!
    public double getCPTSingleProb(int truth) {
        ArrayList<Integer> temp = new ArrayList<>();
        temp.add(truth);
        return valuesMap.get(temp);
    }

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

}
