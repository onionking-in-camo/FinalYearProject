package environment;

//import edu.uci.ics.jung.*;

public class Edge {
    String name = "empty";

    public Edge() {

    }

    public Edge(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
