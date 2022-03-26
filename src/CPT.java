import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

public class CPT {
    private Node node;
    private ArrayList<String> nodeLabels;
    private ArrayList<Double> cptValues; // contains a value for each combination.
    private HashMap<ArrayList<Integer>, Double> valuesMap;

    public CPT(ArrayList<String> nodeLabels) {
        this.nodeLabels = nodeLabels;
    }

    public CPT(Node node) {
        this.nodeLabels = new ArrayList<>();
        this.valuesMap = new HashMap<>();
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
            printCPTHead();
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

            double nodeValue = node.getCpt().getCptValues().get(i);
            if (print) {
                System.out.println("|" + nodeValue); // print node value.
            }

            valuesMap.put(valuesTableRow, nodeValue);

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

    public ArrayList<Double> getValues(ArrayList<String> labels, ArrayList<Integer> truthValues) {
        ArrayList<Double> values = new ArrayList<>();

        // labels size must equal truh values size.
        if (labels.size() == truthValues.size()) {
            HashMap<String, Integer> tempMap = populateMap(labels, truthValues);
            ArrayList<Integer> indexes = new ArrayList<>(); // store indexes in nodeLabels of the passed in labels.

//            for (String label: labels) {
//                indexes.add(nodeLabels.indexOf(label)); // add node labels
//            }


            for (ArrayList<Integer> key : valuesMap.keySet()) {
                System.out.println(key);
                ArrayList<Integer> valuesX = new ArrayList<>();
                for (int i=0; i<key.size(); i++) {

                    if (labels.contains(nodeLabels.get(i))) {

                        System.out.println("key i value"+key.get(i));
                        System.out.println("node label value"+ tempMap.get(nodeLabels.get(i)));
                        if (key.get(i) == tempMap.get(nodeLabels.get(i))) {
                            System.out.println("mesa");
                            valuesX.add(key.get(i));
                        }
                    }

                }

                if (valuesX.equals(truthValues)) {
                    values.add(valuesMap.get(key));
                }
                System.out.println("NEW ONE");
            }
        }

       return values;
    }


    public HashMap<String, Integer> populateMap(ArrayList<String> labels, ArrayList<Integer> truthValues) {
        HashMap<String, Integer> tempMap = new HashMap<>();

        if (labels.size() == truthValues.size()) {
            // they are the same size so assign each label a truth value.
            // E.g. K : 0, L : 1.
            for (int i=0; i<labels.size(); i++) {
                tempMap.put(labels.get(i), truthValues.get(i));
            }
        }
       return tempMap;
    }
//
//    //TODO: get specified value. THIS IS JUST SINGLE CASE.
//    // value determine on where to start skipping.
//    public ArrayList<Double> get(String label, double value) {
//        // FIND INDEX OF LABEL IN THE NODELABELS ARRAYLIST.
//        int columnIndexForLabel = nodeLabels.indexOf(label) + 1;
//        if (columnIndexForLabel != -1) {
//            // find the number of zeros for the first column.
//            // To do that you get the number of all combinations divided by 2.
//            int zerosElement = (int) Math.pow(2, nodeLabels.size()) / 2;
//
//            for (int i = 1; i < columnIndexForLabel; i++) {
//                zerosElement /= 2;
//            }
//
//            int finalZerosElement = zerosElement;
//            return getEveryNthElement(finalZerosElement);
//
//        }
//
//        return null;
//    }

//    /**
//     * Get every nth element
//     * @param nthElement
//     * @return
//     */
//    public ArrayList<Double> getEveryNthElement(int nthElement) {
//        ArrayList<Double> elements = new ArrayList<>();
//
//        for (int i = 0; i < cptValues.size(); i += nthElement*2) {
//            for (int x = 0; x<nthElement; x++) {
//                elements.add(cptValues.get(i+x));
//            }
//        }
//
//        return elements;
//    }

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
