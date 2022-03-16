package graphs;

import edu.uci.ics.jung.graph.SparseGraph;

import java.util.Map;

public class ModifiableSparseGraph<V, E> extends SparseGraph<V, E> {

    /**
     * Interchange a vertex within the graph to a new vertex, whilst
     * maintaining the original edges (connections).
     * If arguments oldV and newV are equal, as defined by {@link java.lang.Object#equals(Object)},
     * then the interchange will not occur. As such, correctly implementing the equality
     * check is crucial.
     * If newV already exists within the graph, it cannot be used as a replacement, and
     * will return false.
     * @param oldV: the old vertex
     * @param newV: the vertex to replace the old
     * @return true if the vertex was interchanged
     */
    public boolean interchangeVertex(V oldV, V newV) {
        // both vertices must be non-null
        if (oldV == null || newV == null) {
            throw new IllegalArgumentException("Vertices must not be null");
        }
        // newV must not exist within the graph already
        if (this.containsVertex(newV)) {
            throw new IllegalArgumentException("Replacement vertex already exists within the graph." +
                    "Cannot replace a vertex with an existing vertex.");
        }
        // if vertices are equal, perform no replacement
        if (oldV.equals(newV)) {
            return false;
        }
        // if oldV exists, interchange with newV
        if (this.containsVertex(oldV)) {
            Map<V, E>[] connections = this.vertex_maps.get(oldV);
            this.vertex_maps.remove(oldV);
            this.vertex_maps.put(newV, connections);
            return true;
        }
        // default
        return false;
    }
}
