package obsolete;

import actors.Entity;
import com.google.common.base.Function;
import edu.uci.ics.jung.algorithms.generators.random.KleinbergSmallWorldGenerator;
import edu.uci.ics.jung.graph.Graph;
import com.google.common.base.Supplier;
import edu.uci.ics.jung.graph.SparseGraph;

import java.awt.*;
import java.util.Collection;
import java.util.List;

public abstract class NetworkFinal<V, E, X> {

    private Graph<V, E> gg;
    private Supplier<V> nodeSupplier;
    private Supplier<E> edgeSupplier;
    private Function<X, Paint> nodePainter;

    class GraphFactory implements Supplier<Graph<V, E>> {
        @Override
        public Graph<V, E> get() {
            return new SparseGraph<>();
        }
    }

    public void setNodeSupplier(Supplier<V> nodeSupplier) {
        this.nodeSupplier = nodeSupplier;
    }

    public void setEdgeSupplier(Supplier<E> edgeSupplier) {
        this.edgeSupplier = edgeSupplier;
    }

    public void setPaint(Function<X, Paint> nodePainter) {
        this.nodePainter = nodePainter;
    }

    public abstract void clearAll();

    public abstract void place(X x, V l);

    public abstract void clearLocation(V l);

    public abstract X getObjectAt(V l);

    public abstract List<V> getAllFreeAdjacentLocations(V l);

    public abstract V freeAdjacentLocation(V l);

    public abstract <T> boolean isNeighbourTo(V l, Class<T> c);

    public List<V> getAllNeighbours(V l) {
        return gg.getNeighbors(l).stream().toList();
    }

//    public void createSmallWorld() {
//        KleinbergSmallWorldGenerator<V, E> gen = new KleinbergSmallWorldGenerator<>(
//                new GraphFactory(), nodeSupplier, edgeSupplier
//        )
//    }

}
