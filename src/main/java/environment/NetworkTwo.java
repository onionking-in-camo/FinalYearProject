package environment;

import actors.Agent;
import actors.AgentGenerator;
import actors.Entity;
import com.google.common.base.Function;
import data.SimData;
import edu.uci.ics.jung.algorithms.generators.random.BarabasiAlbertGenerator;
import edu.uci.ics.jung.algorithms.generators.random.KleinbergSmallWorldGenerator;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.graph.Graph;
import com.google.common.base.Supplier;
import edu.uci.ics.jung.graph.SparseGraph;
import edu.uci.ics.jung.visualization.VisualizationImageServer;
import models.Infected;
import models.Susceptible;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class NetworkTwo {

    private Graph<Location, Edge> gg;
    private Supplier<Location> nodeSupplier;
    private Supplier<Edge> edgeSupplier;
    private Function<Location, Paint> nodePainter;

    public NetworkTwo() {}

    class GraphFactory implements Supplier<Graph<Location, Edge>> {
        @Override
        public Graph<Location, Edge> get() {
            return new SparseGraph<>();
        }
    }

    public void setNodeSupplier(Supplier<Location> nodeSupplier) {
        this.nodeSupplier = nodeSupplier;
    }

    public void setEdgeSupplier(Supplier<Edge> edgeSupplier) {
        this.edgeSupplier = edgeSupplier;
    }

    public void setPaint(Function<Location, Paint> nodePainter) {
        this.nodePainter = nodePainter;
    }

    public Graph<Location, Edge> getGraph() {
        return gg;
    }

    public void clearAll() {
        List<Location> vertices = gg.getVertices().stream().toList();
        for (Location l : vertices) {
            l.addOccupant(null);
        }
    }

    public void place(Entity e, Location l) {
        int col = l.getCol();
        int row = l.getRow();
        Location loc = new Location(row, col);
        loc.addOccupant(e);
        replace(l, loc);
    };

    public void clearLocation(Location l) {};

    public Entity getObjectAt(Location l) {
        if (gg.containsVertex(l)) {
            return l.getOccupant();
        }
        return null;
    };

    public List<Location> getAllFreeAdjacentLocations(Location l) {
        if (gg.containsVertex(l)) {
            List<Location> free = new ArrayList<>();
            List<Location> adjacents = gg.getNeighbors(l).stream().toList();
            for (Location loc : adjacents) {
                if (!loc.occupied()) {
                    free.add(loc);
                }
            }
            return free;
        }
        return Collections.EMPTY_LIST;
    }

    public Location freeAdjacentLocation(Location l) {
        List<Location> frees = getAllFreeAdjacentLocations(l);
        if (frees.isEmpty()) { return null; }
        return frees.get(SimData.getRandom().nextInt(frees.size()));
    }

    public <T> boolean isNeighbourTo(Location l, Class<T> c) {
        return l.getOccupant().getClass() == c;
    }

    public void createSmallWorld(int lattice, double clustering) {
        KleinbergSmallWorldGenerator<Location, Edge> gen = new KleinbergSmallWorldGenerator<>(
                new GraphFactory(), nodeSupplier, edgeSupplier, lattice, clustering
        );
        gg = gen.get();
    }

    public void createFreeScaleWorld(int vertices, int edges, int seed) {
        BarabasiAlbertGenerator<Location, Edge> gen = new BarabasiAlbertGenerator<>(
                new GraphFactory(), nodeSupplier, edgeSupplier, vertices, edges, seed, new HashSet<>()
        );
        gen.evolveGraph(50);
        gg = gen.get();
    }

    private boolean replace(Location o, Location n) {
        if (gg.containsVertex(o)) {
            List<Location> neighbours = gg.getNeighbors(o).stream().toList();
            gg.removeVertex(o);
            gg.addVertex(n);
            for (Location l : neighbours) {
                gg.addEdge(edgeSupplier.get(), n, l);
            }
            return true;
        }
        return false;
    }

    static class NodePainter implements Function<Location, Paint> {
        @Override
        public Paint apply(Location loc) {
            Entity e = loc.getOccupant();
            if (e == null) {
                return Color.GRAY;
            }
            if (e instanceof Agent) {
                Agent ag = (Agent) e;
                if (ag.getStatus().getClass() == Susceptible.class) {
                    return Color.BLUE;
                }
                if (ag.getStatus().getClass() == Infected.class) {
                    return Color.RED;
                }
                // else, Recovered
                return Color.GREEN;
            }
            return Color.WHITE;
        }
    }

    static class EdgeFactory implements Supplier<Edge> {
        static final String relation = " CONNECTS ";
        @Override
        public Edge get() {
            return new Edge(relation);
        }
    }

    static class NodeFactory implements Supplier<Location> {
        AgentGenerator gen = new AgentGenerator();
        int row = 0;
        int col = 0;
        @Override
        public Location get() {
            row++;
            col++;
            Location loc = new Location(row, col);
            Entity e = gen.generate(loc);
            loc.addOccupant(e);
            return loc;
        }
    }

    public static void main(String[] args) {
        NetworkTwo n2 = new NetworkTwo();

        n2.setNodeSupplier(new NodeFactory());
        n2.setEdgeSupplier(new EdgeFactory());
        n2.setPaint(new NodePainter());

//        n2.createFreeScaleWorld(8, 3, 123);
        n2.createSmallWorld(8, 0.5);

        VisualizationImageServer<Location, Edge> vs =
                new VisualizationImageServer<Location, Edge>(
                        new ISOMLayout<Location, Edge>(n2.getGraph()),
                        new Dimension(800, 600));
        vs.getRenderContext().setVertexFillPaintTransformer(n2.nodePainter);
        JFrame frame = new JFrame();
        frame.getContentPane().add(vs);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
