import java.util.*;
import java.util.stream.Collectors;

public class Agent {

    // Initialise inputs.
    BayesianNetwork bn;
    Node queried;
    ArrayList<String> order;
    ArrayList<String[]> evidences;


    public Agent(BayesianNetwork bn, String queried, String[] order) {
        this.bn = bn;
        this.queried = bn.getNode(queried);
        this.order = new ArrayList<>(Arrays.asList(order));
    }

    public Agent(BayesianNetwork bn, String queried, String[] order, ArrayList<String[]> evidences) {
        this.bn = bn;
        this.queried = bn.getNode(queried);
        this.order = new ArrayList<>(Arrays.asList(order));
        this.evidences = evidences;
    }

    public double variableElimination(String value, boolean evidence) {
        pruneIrrelevantVariables(evidence);
        System.out.println("NEW ORDER: "+ order);
        ArrayList<CPT> factors = createSetFactors();

        // if we are performing variable elimination with evidence, then project evidence for related factors.
        if (evidence) {
            projectEvidence(factors);
        }
        System.out.println("FACTORS: " + factors);
        CPT newFactor = null;
        // if order has at least one element, then perform variable elimination.
        if (order.size() >= 1) {
            for (String label : order) {
                System.out.println("\n\nDEBUG LABEL: " + label);
                ArrayList<CPT> toSumOut = getFactorsContainingLabel(label, factors); // get factors containing label.
                System.out.println("toSumOut: " + toSumOut);
                factors.removeAll(toSumOut); // remove all factors containing label.

                // create a new factor with all variables in factors of ToSumOut but without label.
                newFactor = joinMarginalise(toSumOut, label);
                factors.add(newFactor); // add new factor.

                System.out.println(factors);
            }

            if (evidence) {
                if (factors.size() > 1) {
                    // the node label should be the same for both - the queried node.
                    newFactor = join(factors, queried.getLabel());
                }
                newFactor = normalize(newFactor);
            }
        }
        // otherwise just assign the queried node's cpt to newFactor and read its value.
        else if (order.size() == 0 || newFactor == null) {
            newFactor = queried.getCpt();
        }

        System.out.println("Order of elimination: "+order);
        newFactor.constructAndPrintCPT(true);
        // Get the truth value that we are looking.
        int truthLooking = (value.equalsIgnoreCase("T")) ? 1 : 0;
        return newFactor.getCPTSingleProb(truthLooking);
    }


    private void projectEvidence(ArrayList<CPT> factors) {
        for (String[] ev : evidences) {
            System.out.println("INSIDE EVIDENCE - current evidence: "+ ev);
            // find the correspondent factor for current evidence label.
            CPT evFactor = getCorrespondentFactorForLabel(factors, ev[0]);
            boolean truthToChange = (ev[1].equalsIgnoreCase("T")) ? true : false;
            evFactor.setToZero(truthToChange);
            evFactor.constructAndPrintCPT(true);
        }
    }

    public CPT normalize(CPT joined) {
        ArrayList<Double> normalizedValues = new ArrayList<>();
        double trueValue = joined.getCPTSingleProb(1);
        double falseValue = joined.getCPTSingleProb(0);
        double sumValue = trueValue + falseValue;

        normalizedValues.add(falseValue / sumValue); // add normalised false value.
        normalizedValues.add(trueValue / sumValue); // add normalized true value.

        // update joined CPT values.
        joined.updateCPTvalues(normalizedValues);

        return joined;
    }

    /**
     * Prunes all the irrelevant variables according to our task. If the vidence flag is true, then it deletes
     * ancestors of the evidences.
     *
     * @param evidence
     */
    private void pruneIrrelevantVariables(boolean evidence) {
        ArrayList<String> ancLabels = new ArrayList<>();
        // prune not ancestors of queried node.
        prune(queried, ancLabels);
        // prune not ancestors of evidence nodes.
        if (evidence) {
            for (String[] ev : evidences) {
                Node evNode = bn.getNode(ev[0]); // get node of evidence.
                prune(evNode, ancLabels); // prune order lists based on this evidence ancestors.
            }
        }

        order.retainAll(ancLabels); // retain all elements identified as ancestors.
    }

    /**
     * Performs the pruning by removing every variable that is not an ancestor of a given initial node.
     *
     * @param initialNode the initial node that we search for its ancestors.
     */
    private void prune(Node initialNode, ArrayList<String> ancLabels) {

        ancLabels.add(initialNode.getLabel());

        Stack<Node> ancestors = new Stack<>();
        ancestors.push(initialNode);
        Node currentNode = initialNode;

        // iterate all the ancestors.
        while (!ancestors.isEmpty()) {
            // add all ancestors to the stack.
            if (currentNode.getParents().size() != 0) {
                for (Node node : currentNode.getParents()) {
                    ancestors.push(node);
                    ancLabels.add(node.getLabel());
                }
            }

            currentNode = ancestors.pop(); // take out the first element in the stack.
        }
    }

    /**
     * Create a set of factors.
     *
     * @return
     */
    private ArrayList<CPT> createSetFactors() {
        ArrayList<CPT> factors = new ArrayList<>();

        // iterate through the nodes of the BN.
        for (Node node : bn.getNodes()) {
            if (order.contains(node.getLabel()) || node.getLabel().equalsIgnoreCase(queried.getLabel())) {
                factors.add(node.getCpt());
            }
        }

        return factors;
    }

    /**
     * Get the factors that contain the passed in label.
     *
     * @param label
     * @param factors
     * @return
     */
    private ArrayList<CPT> getFactorsContainingLabel(String label, ArrayList<CPT> factors) {
        ArrayList<CPT> toSumOut = new ArrayList<>();

        for (CPT factor : factors) {
            if (factor.getNodeLabels().contains(label)) {
                toSumOut.add(factor);
            }
        }
        return toSumOut;
    }

    /**
     * Find the correspondent CPT/factor from the factors for a passed in label.
     *
     * @param label
     * @return
     */
    private CPT getCorrespondentFactorForLabel(ArrayList<CPT> factors, String label) {
        // Iterate through the factors.
        for (CPT factor : factors) {
            Node node = bn.getNode(label);
            // if the corresponding node for that
            if (factor.getCorrespondentNode().equals(node)) {
                return factor;
            }
        }
        return null;
    }

    // remove variable.
    private CPT marginalise(CPT newFactor, String label) {
        CPT marginalised = new CPT();
        newFactor.constructAndPrintCPT(true);

        // set node labels everything except current label.
        ArrayList<String> nodeLabels = new ArrayList<>(newFactor.getNodeLabels());
        int index = nodeLabels.indexOf(label);
        int sizeOfNodeLabels = nodeLabels.size();

        System.out.println("DEBUG INDEX: " + index + " SIZE OF NODE LABELS-1: "+ (sizeOfNodeLabels-1));
        System.out.println("label to marginalise: "+ label);
        if (nodeLabels.size() > 1) {
            nodeLabels.remove(label);
            marginalised.setNodeLabels(nodeLabels);

            int iterationSize = newFactor.getCptValues().size() / 2;

            // add the marginalised values.
            ArrayList<Double> marginalisedFactorValues = new ArrayList<>();

            if (index == sizeOfNodeLabels - 1) {
                System.out.println("DEBUG: WE ARE ALRIGHT!");
                for (int i = 0; i < newFactor.getCptValues().size() - 1; i += 2) {
                    double value = newFactor.getCptValues().get(i) + newFactor.getCptValues().get(i + 1);
                    marginalisedFactorValues.add(value);
                }
            } else {
                System.out.println("MESA STO MATI TOU KIKLONA");
                System.out.println("Combination"+newFactor.getCombinations());
                System.out.println(newFactor.getCPTProbability(newFactor.getCombinations().get(0)));
                ArrayList<ArrayList<Integer>> truthAlreadyTried = new ArrayList<>();
                for (int i = 0; i < newFactor.getCptValues().size(); i++) {
                    ArrayList<Integer> truthValuesForTrue = newFactor.getCombinations().get(i);
                    ArrayList<Integer> truthValuesForFalse = newFactor.getCombinations().get(i);
                    truthValuesForTrue.set(index, 1); // set value for true.
                    truthValuesForFalse.set(index, 0); // set value for false.

                    if (!truthAlreadyTried.contains(truthValuesForTrue) && !truthAlreadyTried.contains(truthValuesForFalse)) {
                        System.out.println("TV T: "+truthValuesForTrue + " prob: "+ newFactor.getCPTProbability(truthValuesForTrue));
                        System.out.println("TV F: "+truthValuesForFalse + " prob: "+ newFactor.getCPTProbability(truthValuesForFalse));
//                    System.out.println("DEBUG inside - i is:"+ i + ", newFactor i:" + newFactor.getCptValues().get(i) + "newFactor i+z:" +  newFactor.getCptValues().get(i + z));
                        double value = newFactor.getCPTProbability(truthValuesForTrue) + newFactor.getCPTProbability(truthValuesForFalse);
                        marginalisedFactorValues.add(value);
                        truthAlreadyTried.add(truthValuesForTrue);
                        truthAlreadyTried.add(truthValuesForFalse);
                        System.out.println("\n");
                    }

                }
                Collections.reverse(marginalisedFactorValues); // reverse the values (Added different order).
            }

            marginalised.addCPTvalues(marginalisedFactorValues);
        } else if (nodeLabels.size() == 1) {
            marginalised.setNodeLabels(nodeLabels);
        }

        System.out.println("MARGINALISED TABLE");
        marginalised.constructAndPrintCPT(true);
        return marginalised;
    }


    private ArrayList<String> variablesBoth(CPT first, CPT second) {
        // return common elements in first and second CPTs.
        return (ArrayList<String>) first.getNodeLabels().stream().filter(second.getNodeLabels()::contains).collect(Collectors.toList());
    }

    private ArrayList<String> variablesNotInSecond(CPT first, CPT second) {
        return (ArrayList<String>) first.getNodeLabels().stream()
                .filter(element -> !second.getNodeLabels().contains(element))
                .collect(Collectors.toList());
    }


    //TODO: improve
    private ArrayList<Integer> getFactorTruthCombination(ArrayList<Integer> truthCombination, CPT newFactor, CPT factor) {
        ArrayList<Integer> factorTruth = new ArrayList<>();
        for (int i = 0; i < factor.getNodeLabels().size(); i++) {
            for (int j = 0; j < newFactor.getNodeLabels().size(); j++) {
                if (newFactor.getNodeLabels().get(j).equalsIgnoreCase(factor.getNodeLabels().get(i))) {
                    factorTruth.add(truthCombination.get(j));
                }

            }
        }
        return factorTruth;
    }

    private CPT joinMarginalise(ArrayList<CPT> toSumOut, String label) {
        CPT newFactor;
        if (toSumOut.size() > 1) {
            newFactor = join(toSumOut, label);
        } else {
            newFactor = toSumOut.get(0);
        }
        CPT marginalisedNewFactor = marginalise(newFactor, label);

        return marginalisedNewFactor;
    }


    //TODO: improve!
    private CPT join(ArrayList<CPT> toSumOut, String label) {
        CPT newFactor = new CPT(label);
        CPT first = toSumOut.get(0);
        // Join iteratively (two factors at a time).
        for (int i = 1; i < toSumOut.size(); i++) {
            first.constructAndPrintCPT(true);
            CPT second = toSumOut.get(i);

            first.constructAndPrintCPT(true);
            second.constructAndPrintCPT(true);

            ArrayList<String> combined = getCombined(first, second);
            // Truth combinations to calculate.
            newFactor.setNodeLabels(combined);

            // Get all the truth values combinations.
            ArrayList<ArrayList<Integer>> newFactorTruths = newFactor.getCombinations();
            ArrayList<Double> newFactorValues = new ArrayList<>();

            // Iterate through the new factors' truth combinations and calculate their values.
            for (int x = 0; x < newFactorTruths.size(); x++) {
                ArrayList<Integer> truthCombination = newFactorTruths.get(x);
                ArrayList<Integer> f1Truth = getFactorTruthCombination(truthCombination, newFactor, first);
                ArrayList<Integer> f2Truth = getFactorTruthCombination(truthCombination, newFactor, second);

                double value = first.getCPTProbability(f1Truth) * second.getCPTProbability(f2Truth);
                newFactorValues.add(value);
            }

            System.out.println("DEBUG: COMPLETED TURN!!!");
            // reverse values orders.
            Collections.reverse(newFactorValues);
            newFactor.addCPTvalues(newFactorValues);

            System.out.println("NEW FACTOR");
            System.out.println("LABELS AFTER: " + newFactor.getNodeLabels());
            newFactor.constructAndPrintCPT(true);
            System.out.println("END");

            //TODO: THiS IS THE PROBLEM
            first = new CPT(newFactor);
        }

        System.out.println("OUT OF JOIN SOON...");
        return newFactor;
    }

    /**
     * // Collect all the variables without repetition from the two CPT/factors tables.
     *
     * @param first
     * @param second
     * @return
     */
    private ArrayList<String> getCombined(CPT first, CPT second) {
        ArrayList<String> v1 = variablesBoth(first, second); // variables in both.
        ArrayList<String> v2 = variablesNotInSecond(first, second); // variables in the first but not second.
        ArrayList<String> v3 = variablesNotInSecond(second, first); // variables in the second but not first.
        // Combine three arraylists. TODO: maybe function it.
        ArrayList<String> combined = new ArrayList<String>();
        combined.addAll(v1);
        combined.addAll(v2);
        combined.addAll(v3);

        Collections.reverse(combined);
        return combined;
    }

}
