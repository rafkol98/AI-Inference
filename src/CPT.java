import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

public class CPT {
    private ArrayList<Double> cptValues; // contains a value for each combination.

    public ArrayList<Double> getCptValues() {
        return cptValues;
    }

    public CPT(double ... values) {
        this.cptValues = DoubleStream.of(values).boxed().collect(Collectors.toCollection(ArrayList::new));
    }

}
