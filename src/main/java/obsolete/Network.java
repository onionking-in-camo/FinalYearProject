package obsolete;

import actors.AgentGenerator;
import actors.Entity;
import com.google.common.base.Function;
import edu.uci.ics.jung.algorithms.generators.random.KleinbergSmallWorldGenerator;
import edu.uci.ics.jung.algorithms.layout.*;
import edu.uci.ics.jung.graph.Graph;
import actors.Agent;
import edu.uci.ics.jung.graph.SparseGraph;
import edu.uci.ics.jung.visualization.VisualizationImageServer;
import com.google.common.base.Supplier;
import environment.Edge;
import environment.Location;
import models.Susceptible;
import models.Infected;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class Network {

    private static Graph<Entity, Edge> gg = null;

    static class GraphFactory implements Supplier<Graph<Entity, Edge>> {
        @Override
        public Graph<Entity, Edge> get() {
            return new SparseGraph<Entity, Edge>();
        }
    }

    static class NodeFactory implements Supplier<Entity> {
        AgentGenerator gen = new AgentGenerator();
        @Override
        public Entity get() {
            Entity e;
            do {
                e = gen.generate(null);
            } while (e == null);
//            return gen.generate(null);
            return e;
        }
    }

    static class EdgeFactory implements Supplier<Edge> {
        char aa = 'a';
        @Override
        public Edge get() {
            return new Edge(Character.toString(aa++));
        }
    }

    static class NodePainter implements Function<Entity, Paint> {
        @Override
        public Paint apply(Entity agent) {
            if (agent instanceof Agent) {
                Agent ag = (Agent) agent;
                if (ag.getStatus().getClass() == Susceptible.class) {
                    return Color.BLUE;
                }
                if (ag.getStatus().getClass() == Infected.class) {
                    return Color.RED;
                }
                return Color.GREEN;
            }
            return Color.GRAY;
        }
    }

    public static void main(String[] args) {
        KleinbergSmallWorldGenerator<Entity, Edge> gen = new KleinbergSmallWorldGenerator<Entity, Edge>(
                new GraphFactory(), new NodeFactory(), new EdgeFactory(), 5, 0.5
        );
//        Graph<Entity, Edge> gg = gen.get();
        gg = gen.get();
        System.out.println("Nodes: " + gg.getVertexCount());
        System.out.println("Edges: " + gg.getEdgeCount());
        VisualizationImageServer<Entity, Edge> vs =
                new VisualizationImageServer<Entity, Edge>(
                        new ISOMLayout<>(gg),
                        new Dimension(800, 600));
        vs.getRenderContext().setVertexFillPaintTransformer(ag -> new NodePainter().apply(ag));
        JFrame frame = new JFrame();
        frame.getContentPane().add(vs);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        try {

            Agent agnt = new Agent(null);
            for (int i = 0; i < 3; i++) {
                List<Entity> l = gg.getVertices().stream().toList();
                if (i == 0) {
                    addVertex(agnt, l);
                }
                else {
                    addVertex(new Agent(new Location(i, i)), l);
                }

                vs.updateUI();
                Thread.sleep(1000);
            }
            removeVertex(agnt);
            vs.updateUI();
        } catch (InterruptedException ie) { ;; }

    }

    private HashMap<Agent, Agent> updates = new HashMap<>();

    private Collection<Entity> getNeighbours(Entity vertex) {
        return gg.getNeighbors(vertex);
    }

    private static void addVertex(Entity toAdd, List<Entity> connections) {
        gg.addVertex(toAdd);
        for (Entity e : connections) {
            gg.addEdge(new EdgeFactory().get(), toAdd, e);
        }
    }

    private static void removeVertex(Entity v) {
        gg.removeVertex(v);
    }

    private static void tranmission() {
        Collection<Entity> vertices = gg.getVertices();
        for (Entity e : vertices) {
            if (e instanceof Agent) {
                Agent ag = (Agent) e;
                if (ag.getStatus().getClass() == Susceptible.class) {
                    // get neighbours
                    Collection<Entity> neighbours = gg.getNeighbors(ag);
                    // for each neighbour, if infected, increase chance of contracting disease

                    // if chance > infectivity, agent becomes infected
                    // add agent to hashmap

                }
            }
        }
    }
}
