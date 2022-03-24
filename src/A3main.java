import java.text.DecimalFormat;
import java.util.ArrayList;
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
                Node n = bn.getNode("N");
                ArrayList<String> order = new ArrayList<>();
                order.add("J");
                order.add("K");
                order.add("L");
                order.add("M");
                order.add("N");
                order.add("O");
                Agent ve = new Agent(bn, n, order);

//                //construct the network in args[1]
//                String[] query=getQueriedNode(sc);
//                String variable=query[0];
//                String value=query[1];
//                String[] order=getOrder(sc);
//                // execute query of p(variable=value) with given order of elimination
//                double result=0.570501;
//                printResult(result);
            }
            break;

            case "P3": {
                //construct the network in args[1]
                String[] query = getQueriedNode(sc);
                String variable = query[0];
                String value = query[1];
                String[] order = getOrder(sc);
                ArrayList<String[]> evidence = getEvidence(sc);
                // execute query of p(variable=value|evidence) with given order of elimination
                double result = 0.570501;
                printResult(result);
            }
            break;

            case "P4": {
                //construct the network in args[1]
                String[] query = getQueriedNode(sc);
                String variable = query[0];
                String value = query[1];
                String order = "A,B";
                ArrayList<String[]> evidence = getEvidence(sc);
                // execute query of p(variable=value|evidence) with given order of elimination
                //print the order
                System.out.println(order);
                double result = 0.570501;
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

            case "BNB":
                // Add nodes with their cpt tables.
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

                // add cpt values.
                j.addCPTvalues(0.05, 0.95);
                k.addCPTvalues( 0.3, 0.7, 0.1, 0.9);
                l.addCPTvalues(0.7, 0.3);
                m.addCPTvalues( 0.9, 0.1, 0.8, 0.2, 0.3, 0.7, 0.4, 0.6);
                n.addCPTvalues(0.8, 0.2, 0.4, 0.6);
                o.addCPTvalues(0.2, 0.8, 0.95, 0.05);


        }

        return bn;
    }

}
