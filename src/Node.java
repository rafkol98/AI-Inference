import java.util.ArrayList;

public class Node {
    private ArrayList<Node> parents;
    private ArrayList<Node> children;
    private String label;
    private CPT cpt;

    public Node(String label, double ... vals) {
        this.label = label;
        this.cpt = new CPT(label, vals); // create CPT given the values.
    }





}
