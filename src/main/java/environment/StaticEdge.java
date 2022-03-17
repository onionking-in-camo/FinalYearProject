package environment;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class StaticEdge {

    private List<String> types = Arrays.asList(
            "NO_RELATION", "ACQUAINTANCE", "COLLEAGUE",
            "FRIEND", "RELATIVE"
    );
    private String edgeType;

    public StaticEdge() {
        Collections.shuffle(types);
        edgeType = types.get(0);
    }

    public String getEdgeType() {
        return edgeType;
    }
}
