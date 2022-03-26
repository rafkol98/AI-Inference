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
    private ArrayList<String> nodeLabels;
    private ArrayList<Double> cptValues; // contains a value for each combination.
    private LinkedHashMap<ArrayList<Integer>, Double> valuesMap;

    public CPT(ArrayList<String> nodeLabels) {
        this.nodeLabels = nodeLabels;
        this.valuesMap = new LinkedHashMap<>();
    }

    public CPT(Node node) {
        this.nodeLabels = new ArrayList<>();
        this.valuesMap = new LinkedHashMap<>();
        this.node = node;
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

    public void addCPTvalues(double... values) {
        ArrayList<Node> nodesUsedForLabels = new ArrayList<>();
        nodesUsedForLabels.addAll(node.getParents());
        nodesUsedForLabels.add(node);
        // populate node labels ArrayList.
        for (Node n : nodesUsedForLabels) {
            this.nodeLabels.add(n.getLabel());
        }
        this.cptValues = DoubleStream.of(values).boxed().collect(Collectors.toCollection(ArrayList::new));
        constructAndPrintCPT(false);
    }

    /**
     * Print the CPT.
     */
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
                double nodeValue = node.getCpt().getCptValues().get(i);
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

    /**
     * Get values in the truth table for the combination (or single) of labels and expected truth values
     * for each passed in.
     *
     * @param labels
     * @param truthValues
     * @return
     */
    public ArrayList<Double> getValues(ArrayList<String> labels, ArrayList<Integer> truthValues) {
        ArrayList<Double> values = new ArrayList<>();
//        System.out.println("\n\nLabels: "+labels);
        // populate a map with label (key) and corresponding required truth table value (value).
        HashMap<String, Integer> tempMap = populateMap(labels, truthValues);
        truthValues = trimTruthValues(tempMap, labels, truthValues);


        // if the population of map was successful, find appropriate elements in the CPT table.
        if (tempMap.size() > 0) {
            // Iterate through all the valuesMap keys - ArrayList entries.
            // Each entry/key corresponds to a row in the table.
            for (ArrayList<Integer> key : valuesMap.keySet()) {
                ArrayList<Integer> matching = new ArrayList<>(); // store matching values (in correct places).
                // iterate through all values in the current key.
                for (int i = 0; i < key.size(); i++) {
                    // if the labels passed in has the value for the current column,
                    // check if the value is the same.
                    if (labels.contains(nodeLabels.get(i))) {
//                        System.out.println("key.get(i)"+ key.get(i));
//                        System.out.println("tempMap.get(nodeLabels.get(i))"+ tempMap.get(nodeLabels.get(i)));
//                        if (key.get(i) == tempMap.get(nodeLabels.get(i))) {
                        matching.add(key.get(i)); // add matching value to the matching arraylist.
//                        }
                    }

                }
//                System.out.println("truth before:"+ truthValues);
//                System.out.println("matching before:"+matching);
                matching = matchingAll(labels, truthValues, matching);
//                System.out.println("matching after"+matching+"\n");
                // if matching arraylist for this specific key equals the truth values expected,
                // add the hashmap value to the values returned variable.
                if (matching.equals(truthValues)) {
                    values.add(valuesMap.get(key));
                }
            }
        }

        return values;
    }

    /**
     * Keep only the truth values relating to the CPT.
     * @param tempMap
     * @param labels
     * @param truthValues
     * @return
     */
    public ArrayList<Integer> trimTruthValues(HashMap<String, Integer> tempMap, ArrayList<String> labels, ArrayList<Integer> truthValues) {
        ArrayList<Integer> newTruthValues = new ArrayList<>();
//        System.out.println("\n\n");
//        System.out.println(tempMap.keySet());
//        System.out.println(labels);
        for (String key : tempMap.keySet()) {
            for (String label : labels) {
                if (key.equalsIgnoreCase(label)) {
                    int index = labels.indexOf(label);
                    newTruthValues.add(truthValues.get(index));
                }
            }
        }

//        System.out.println("new truth values: "+newTruthValues);
//        System.out.println("\n\n");
        return newTruthValues;
    }

    public ArrayList<Integer> matchingAll(ArrayList<String> labels, ArrayList<Integer> truthValues, ArrayList<Integer> matching) {
//        System.out.println("\n\nCALLED ");

//        System.out.println("truth values" + truthValues);
//        System.out.println("matching"+ matching);
        ArrayList<Integer> one = new ArrayList<>();
        int counter = 0;
        for (String label: labels) {
//            System.out.println("nodeLabels: "+nodeLabels);
            int indexTruth = nodeLabels.indexOf(label);
            int indexMatching = labels.indexOf(label);
//            System.out.println("INDEX TRUTH: "+indexTruth+" INDEX MATCHING"+indexMatching);

            if (indexTruth != -1) {
//                System.out.println("MESA TRUTH: "+truthValues);
//                System.out.println("MESA MATCHING: "+matching);
                if (truthValues.get(indexTruth) == matching.get(indexMatching)) {
                    one.add(matching.get(indexTruth));
//                    System.out.println("MATCH\n\n");
                    counter++;
                }
            }
        }

//        System.out.println("one"+one);
        return one;
    }


    /**
     * Populate a map with the label and truth value required for each.
     *
     * @param labels
     * @param truthValues
     * @return
     */
    public HashMap<String, Integer> populateMap(ArrayList<String> labels, ArrayList<Integer> truthValues) {
        HashMap<String, Integer> tempMap = new HashMap<>();

        for (int i = 0; i < labels.size(); i++) {
            int index = nodeLabels.indexOf(labels.get(i));
            if (index != -1) {
//                System.out.println("label "+labels.get(i) + "index: "+nodeLabels.indexOf(labels.get(i)));
                tempMap.put(labels.get(i), truthValues.get(index));
            }
        }

        return tempMap;
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
}
