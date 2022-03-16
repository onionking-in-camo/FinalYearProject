package environment;

public class Edge {

    String name = "empty";

    public Edge(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
