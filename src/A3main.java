import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/********************Starter Code
 *
 * This class contains some examples on how to handle the required inputs and outputs
 *
 * @author at258
 *
 * run with
 * java A3main <Pn> <NID>
 *
 * Feel free to change and delete parts of the code as you prefer
 *
 */
public class A3main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        VariableElimination ve;
        String value;
        boolean evidenceFlag;
        boolean greedy = false;
        String detailsFlag = "";
        // assign details and greedy flag.
        if (args.length == 3) {
            if (args[2].equalsIgnoreCase("details")) {
                detailsFlag = args[2];
            } else if (args[2].equalsIgnoreCase("greedy")) {
                greedy = true;
            }
        }

        // if both are given.
        if (args.length >= 4) {
            if (args[2].equalsIgnoreCase("details")) {
                detailsFlag = args[2];
            } else if (args[2].equalsIgnoreCase("greedy")) {
                greedy = true;
            }

            if (args[3].equalsIgnoreCase("details")) {
                detailsFlag = args[3];
            } else if (args[3].equalsIgnoreCase("greedy")) {
                greedy = true;
            }
        }

        switch (args[0]) {
            case "P1": {
                // Construct the network in args[1].
                BayesianNetwork bn = getNetwork(args[1]);
                // Print the network.
                bn.printNetwork();
            }
            break;

            case "P2": {
                // Construct the network in args[1].
                BayesianNetwork bn = getNetwork(args[1]);
                String[] query=getQueriedNode(sc);
                String variable = query[0];
                value = query[1];
                evidenceFlag = false;
                String[] order = getOrder(sc);
                // execute query of p(variable=value) with given order of elimination
                ve = new VariableElimination(bn, variable, order);
                double result = ve.runVE(value, evidenceFlag);
                printResult(result);
                timeAndAverageRuns(detailsFlag,20,ve,value,evidenceFlag);
            }
            break;

            case "P3": {
                //construct the network in args[1]
                BayesianNetwork bn = getNetwork(args[1]);
                String[] query = getQueriedNode(sc);
                String variable = query[0];
                value = query[1];
                String[] order = getOrder(sc);
                ArrayList<String[]> evidence = getEvidence(sc);
                evidenceFlag = true;
                ve = new VariableElimination(bn, variable, order, evidence);
                // execute query of p(variable=value|evidence) with given order of elimination
                double result =  ve.runVE(value, evidenceFlag);
                printResult(result);
                timeAndAverageRuns(detailsFlag,20,ve,value,evidenceFlag);
            }
            break;

            case "P4": {
                //construct the network in args[1]
                BayesianNetwork bn = getNetwork(args[1]);
                String[] query = getQueriedNode(sc);
                String variable = query[0];
                value = query[1];
                String[] order;
                Ordering ordering = new Ordering(bn.getNodes(), bn.getEdges());
                // decide on ordering strategy.
                if (greedy) {
                    order = ordering.greedyMinEdgesSearch(variable);
                } else {
                    order = ordering.maximumCardinalitySearch(variable);
                }
                System.out.println(Arrays.toString(order));
                ArrayList<String[]> evidence = getEvidence(sc);
                evidenceFlag = true;
                ve = new VariableElimination(bn, variable, order, evidence);
                // execute query of p(variable=value|evidence) with given order of elimination
                //print the order
                System.out.println(Arrays.toString(order));
                double result =  ve.runVE(value, evidenceFlag);
                printResult(result);
                timeAndAverageRuns(detailsFlag,20,ve,value,evidenceFlag);
            }
            break;

            case "P5": {
                //construct the network in args[1]
                BayesianNetwork bn = getNetwork(args[1]);
                String[] query = getQueriedNode(sc);
                String variable = query[0];
                value = query[1];
                ArrayList<String[]> evidence = getEvidence(sc);
                GibbsSampling gs = new GibbsSampling(bn, variable, evidence);
                // execute query of p(variable=value|evidence) with given order of elimination
                double result =  gs.gibbsAsk(1000, value);
                printResult(result);
            }
            break;
        }
        sc.close();
    }

    //method to obtain the evidence from the user
    private static ArrayList<String[]> getEvidence(Scanner sc) {
        System.out.println("Evidence:");
        ArrayList<String[]> evidence = new ArrayList<String[]>();
        String[] line = sc.nextLine().split(" ");

        for (String st : line) {
            String[] ev = st.split(":");
            evidence.add(ev);
        }
        return evidence;
    }

    //method to obtain the order from the user
    private static String[] getOrder(Scanner sc) {

        System.out.println("Order:");
        String[] val = sc.nextLine().split(",");
        return val;
    }


    //method to obtain the queried node from the user
    private static String[] getQueriedNode(Scanner sc) {

        System.out.println("Query:");
        String[] val = sc.nextLine().split(":");

        return val;

    }

    //method to format and print the result
    private static void printResult(double result) {

        DecimalFormat dd = new DecimalFormat("#0.00000");
        System.out.println(dd.format(result));
    }

    private static BayesianNetwork getNetwork(String argumentIn) {
        BayesianNetwork bn = new BayesianNetwork();
        //construct the network in args[1]
        switch (argumentIn) {
            case "BNA":
                // Add nodes.
                Node a = bn.addNode("A");
                Node b = bn.addNode("B");
                Node c = bn.addNode("C");
                Node d = bn.addNode("D");

                // Add Edges
                bn.addEdge(a, b);
                bn.addEdge(b, c);
                bn.addEdge(c, d);

                // Add cpt values to nodes.
                a.getCpt().addCPTvalues(0.95, 0.05);
                b.getCpt().addCPTvalues(0.2, 0.8, 0.95, 0.05);
                c.getCpt().addCPTvalues(0.7, 0.3, 0.9, 0.1);
                d.getCpt().addCPTvalues(0.4, 0.6, 0.6, 0.4);
                break;
            case "BNB":
                // Add nodes.
                Node j = bn.addNode("J");
                Node k = bn.addNode("K");
                Node l = bn.addNode("L");
                Node m = bn.addNode("M");
                Node n = bn.addNode("N");
                Node o = bn.addNode("O");

                // Add Edges
                bn.addEdge(j, k);
                bn.addEdge(k, m);
                bn.addEdge(l, m);
                bn.addEdge(m, n);
                bn.addEdge(m, o);

                // Add cpt values to nodes.
                j.getCpt().addCPTvalues(0.95, 0.05);
                k.getCpt().addCPTvalues(0.3, 0.7, 0.1, 0.9);
                l.getCpt().addCPTvalues(0.3, 0.7);
                m.getCpt().addCPTvalues( 0.9, 0.1, 0.8, 0.2, 0.3, 0.7, 0.4, 0.6);
                n.getCpt().addCPTvalues(0.8, 0.2, 0.4, 0.6);
                o.getCpt().addCPTvalues(0.2, 0.8, 0.95, 0.05);
                break;
            case "BNC":
                // Add nodes.
                Node p = bn.addNode("P");
                Node q = bn.addNode("Q");
                Node r = bn.addNode("R");
                Node v = bn.addNode("V");
                Node s = bn.addNode("S");
                Node z = bn.addNode("Z");
                Node u = bn.addNode("U");

                // Add Edges
                bn.addEdge(p, q);
                bn.addEdge(q, v);
                bn.addEdge(q, s);
                bn.addEdge(r, v);
                bn.addEdge(r, s);
                bn.addEdge(v, z);
                bn.addEdge(s, z);
                bn.addEdge(s, u);

                // Add cpt values to nodes.
                p.getCpt().addCPTvalues(0.95, 0.05);
                q.getCpt().addCPTvalues(0.3, 0.7, 0.1, 0.9);
                r.getCpt().addCPTvalues(0.3, 0.7);
                v.getCpt().addCPTvalues(0.9, 0.1, 0.85, 0.15, 0.45, 0.55, 0.3, 0.7);
                s.getCpt().addCPTvalues(0.9, 0.1, 0.8, 0.2, 0.3, 0.7, 0.4, 0.6);
                z.getCpt().addCPTvalues(0.8, 0.2, 0.6, 0.4, 0.3, 0.7, 0.35, 0.65);
                u.getCpt().addCPTvalues(0.2, 0.8, 0.95, 0.05);
                break;
            case "CNX":
                // Add nodes.
                Node iod = bn.addNode("IOD");
                Node mp = bn.addNode("M");
                Node h = bn.addNode("H");
                Node fd = bn.addNode("FD");
                Node nb = bn.addNode("NB");
                Node ap = bn.addNode("A");
                Node at = bn.addNode("AT");
                Node cl = bn.addNode("CL");

                // Add Edges
                bn.addEdge(iod, mp);
                bn.addEdge(mp, fd);
                bn.addEdge(nb, ap);
                bn.addEdge(fd, ap);
                bn.addEdge(h, ap);
                bn.addEdge(ap, at);
                bn.addEdge(ap, cl);

                // Add cpt values to nodes.
                iod.getCpt().addCPTvalues(0.98, 0.02);
                mp.getCpt().addCPTvalues(0.71429, 0.28571, 0.6, 0.4);
                h.getCpt().addCPTvalues(0.875, 0.125);
                nb.getCpt().addCPTvalues(0.85, 0.15);
                fd.getCpt().addCPTvalues(0.999, 0.001, 0.97, 0.03);
                ap.getCpt().addCPTvalues(0.998, 0.002, 0.95, 0.05, 0.8, 0.2, 0.75, 0.25, 0.9, 0.1, 0.85, 0.15, 0.7, 0.3, 0.55, 0.45);
                at.getCpt().addCPTvalues(0.99, 0.01, 0.05, 0.95);
                cl.getCpt().addCPTvalues(0.05, 0.95, 0.3, 0.7);

                break;
        }
        return bn;
    }

    /**
     * Time and average given number of runs. Used to compare orderings.
     * @param numberOfRuns
     * @return
     */
    private static void timeAndAverageRuns(String detailsFlag, int numberOfRuns, VariableElimination ve, String value, boolean evidence) {
        if (detailsFlag.equalsIgnoreCase("details")) {
            int totalNumberOfOperations = 0;
            int totalTruthValuesCalculated = 0;
            long startTime = System.nanoTime(); // start timer.
            for (int i = 0; i < numberOfRuns; i++) {
                double result = ve.runVE(value, evidence);
                totalNumberOfOperations += ve.getNumberOfOperations();
                totalTruthValuesCalculated += ve.getTruthValuesCalculated();
            }
            long endTime = System.nanoTime();
            long totalDuration = (endTime - startTime) / 1000;  //divide by 1,000,000 to get microseconds.
            long averageDuration = totalDuration / numberOfRuns; // get average duration of a run.
            int averageNumberOfOperations = totalNumberOfOperations / numberOfRuns;
            int averageTruthValuesCalculated = totalTruthValuesCalculated / numberOfRuns;

            System.out.println("Duration run - Average of 20 runs: "+averageDuration +" µs");
            System.out.println("Number of joinMarginalise operation - Average of 20 runs: " + averageNumberOfOperations);
            System.out.println("Truth values calculated counter - Average of 20 runs: " + averageTruthValuesCalculated);
        }
    }

//    private static void

}
