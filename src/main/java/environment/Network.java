package environment;

import actors.Agent;
import actors.AgentGenerator;
import actors.Entity;
import actors.Obstacle;
import com.google.common.base.Function;
import data.GUIData;
import data.SimData;
import edu.uci.ics.jung.algorithms.generators.random.BarabasiAlbertGenerator;
import edu.uci.ics.jung.algorithms.generators.random.KleinbergSmallWorldGenerator;
import edu.uci.ics.jung.graph.Graph;
import com.google.common.base.Supplier;
import graphs.ModifiableSparseGraph;
import models.Infected;
import models.Susceptible;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Network implements Field<Entity, Location> {

    private ModifiableSparseGraph<Location, Edge> gg;
    private Supplier<Location> nodeSupplier;
    private Supplier<Edge> edgeSupplier;
    private Function<Location, Paint> nodePainter;

    public Network() {}

    class GraphFactory implements Supplier<Graph<Location, Edge>> {
        @Override
        public Graph<Location, Edge> get() {
            return new ModifiableSparseGraph<>();
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

    public Function<Location, Paint> getPainter() { return this.nodePainter; }

    public Graph<Location, Edge> getGraph() {
        return gg;
    }

    @Override
    public void initialise() {
        setNodeSupplier(new NodeFactory());
        setEdgeSupplier(new EdgeFactory());
        setPaint(new NodePainter());
        createSmallWorld(12, 0.5);
    }

    @Override
    public void clearAll() {
        List<Location> vertices = gg.getVertices().stream().toList();
        for (Location l : vertices) {
            l.addOccupant(null);
        }
    }

    @Override
    public void place(Location l, Entity e) {
        if (gg.containsVertex(l)) {
            l.setOccupant(e);
        }
    }

    // Remove the occupant of the location
    @Override
    public void clearLocation(Location l) {
        if (gg.containsVertex(l)) {
            l.setOccupant(null);
        }
    };

    @Override
    public Entity getObjectAt(Location l) {
        if (gg.containsVertex(l)) {
            return l.getOccupant();
        }
        return null;
    };

    /**
     * Returns a list of all nodes, connected to location l, which
     * are not occupied by another entity.
     * @param l: the location from which to find free adjacent
     *         locations
     * @return a list of adjacent locations that are free, or
     * an empty list if:
     * 1) There are no free adjacencies
     * 2) The supplied location is not within the network
     */
    @Override
    public List<Location> getAllFreeAdjacentLocations(Location l) {
        if (gg.containsVertex(l)) {
            List<Location> free = new ArrayList<>();
            List<Location> adjs = new ArrayList<>(gg.getNeighbors(l));
            for (Location loc : adjs) {
                if (!loc.occupied()) {
                    free.add(loc);
                }
            }
            return free;
        }
        return Collections.EMPTY_LIST;
    }

    /**
     * Returns a free adjacent location to the given location l. If
     * more than one free location exists, a single location is chosen
     * randomly.
     * @param l: location from which to find free adjacencies
     * @return if true, a free adjacent location, else null
     */
    @Override
    public Location freeAdjacentLocation(Location l) {
        List<Location> frees = getAllFreeAdjacentLocations(l);
        if (frees.isEmpty()) { return null; }
        return frees.get(SimData.getRandom().nextInt(frees.size()));
    }

    @Override
    public <T extends Entity> boolean isNeighbourTo(Location l, Class<T> c) {
        List<Entity> neighbours = getAllNeighbours(l, c);
        for (Entity e : neighbours) {
            if (e.getClass() == c) {
                return true;
            }
        }
        return false;
    }

    @Override
    public <T extends Entity> List<Entity> getAllNeighbours(Location location, Class<T> c) {
        List<Entity> allNeighbours = new ArrayList<>();
        Collection<Location> interim = gg.getNeighbors(location);
        if (interim != null) {
            List<Location> n = new ArrayList<>(interim);
            for (Location l : n) {
                if (l.occupied() && l.getOccupant().getClass() == c) {
                    allNeighbours.add(l.getOccupant());
                }
            }
        }
        return allNeighbours;
    }

    @Override
    public int getDimensions() {
        return 0;
    }

    @Override
    public boolean pathObstructed(Entity entity) {
        Location l = entity.getLocation();
        return getAllFreeAdjacentLocations(l).isEmpty();
    }

    @Override
    public List<Entity> getAllEntities() {
        List<Entity> entities = new ArrayList<>();
        List<Location> nodes = gg.getVertices().stream().toList();
        for (Location l : nodes) {
            if (l.occupied()) {
                entities.add(l.getOccupant());
            }
        }
        return entities;
    }

    public void createSmallWorld(int lattice, double clustering) {
        KleinbergSmallWorldGenerator<Location, Edge> gen = new KleinbergSmallWorldGenerator<>(
                new GraphFactory(), nodeSupplier, edgeSupplier, lattice, clustering
        );
        gg = (ModifiableSparseGraph<Location, Edge>) gen.get();
    }

    public void createFreeScaleWorld(int vertices, int edges) {
        BarabasiAlbertGenerator<Location, Edge> gen = new BarabasiAlbertGenerator<>(
                new GraphFactory(), nodeSupplier, edgeSupplier, vertices, edges, SimData.SEED, new HashSet<>()
        );
        gen.evolveGraph(50);
        gg = (ModifiableSparseGraph<Location, Edge>) gen.get();
    }

//    private boolean replace(Location o, Location n) {
//        return gg.interchangeVertex(o, n);
//    }

    static class NodePainter implements Function<Location, Paint> {
        @Override
        public Paint apply(Location loc) {
            Entity e = loc.getOccupant();
            if (e == null) {
                return GUIData.EMP_COL;
            }
            if (e instanceof Agent) {
                Agent ag = (Agent) e;
                if (ag.getStatus().getClass() == Susceptible.class) {
                    return GUIData.SUS_COL;
                }
                if (ag.getStatus().getClass() == Infected.class) {
                    return GUIData.INF_COL;
                }
                // else, Recovered
                return GUIData.REC_COL;
            }
            if (e instanceof Obstacle) {
                return GUIData.OBS_COL;
            }
            return GUIData.DEF_COL;
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
//            row++;
//            col++;
            Location loc = new Location(row++, col++);
            Entity e = gen.generate(loc);
            loc.addOccupant(e);
            return loc;
        }
    }
}