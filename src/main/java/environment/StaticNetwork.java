package environment;

import actors.Agent;
import actors.AgentGenerator;
import actors.Entity;
import actors.Obstacle;
import com.google.common.base.Function;
import com.google.common.base.Supplier;
import data.GUIData;
import data.SimData;
import edu.uci.ics.jung.algorithms.generators.random.BarabasiAlbertGenerator;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.VisualizationImageServer;
import graphs.ModifiableSparseGraph;
import models.Infected;
import models.Recovered;
import models.Susceptible;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class StaticNetwork {

    private Supplier<Entity> agentSupplier;
    private Supplier<StaticEdge> edgeSupplier;

    public static void main(String[] args) {
        StaticNetwork sn = new StaticNetwork();
        sn.setup();

    }

    private void setup() {
        BarabasiAlbertGenerator<Entity, StaticEdge> graphGen = new BarabasiAlbertGenerator<Entity, StaticEdge>(
                new GraphFactory(), new NodeFactory(), new EdgeFactory(), 500, 6, 10, new HashSet<>()
        );
        graphGen.evolveGraph(50);
        Graph<Entity, StaticEdge> g = graphGen.get();
        List<Entity> allV = new ArrayList<>(g.getVertices());
        for (Entity e : allV) {
            if (g.inDegree(e) < 1) {
                g.removeVertex(e);
            }
        }
        JFrame frame = new JFrame();
        frame.setTitle("Static Network");
        frame.setSize(700, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        VisualizationImageServer vs = new VisualizationImageServer<>(
                new ISOMLayout<>(g),
                new Dimension(600, 600));
        vs.getRenderContext().setVertexFillPaintTransformer(new NodePainter());
        frame.add(vs);
        frame.setVisible(true);

        List<Entity> agentsToInfect = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            agentsToInfect.clear();
            List<Entity> l = new ArrayList<>(g.getVertices());
            for (Entity e : l) {
                Agent ag = (Agent) e;
                if (ag.getStatus().getClass() == Infected.class) {
                    ag.processDisease();
                    continue;
                }
                if (ag.getStatus().getClass() != Susceptible.class) { continue; }
                List<StaticEdge> edges = new ArrayList<>(g.getIncidentEdges(e));
                double prob = 0;
                for (StaticEdge edge : edges) {
                    Agent opp = (Agent) g.getOpposite(ag, edge);
                    if (opp.getStatus().getClass() == Infected.class) {
                        prob += (SimData.INFECTIVITY * edge.getInfectionRate());
                    }
                }
                System.out.println(prob);
                if (prob > (SimData.INFECTIVITY * SimData.getRandom().nextDouble())) {
                    agentsToInfect.add(ag);
                }
            }
            for (Entity e : agentsToInfect) {
                Agent ag = (Agent) e;
                ag.setStatus(ag.getStatus().nextState());
            }

            vs.updateUI();

        }
    }

    static class GraphFactory implements Supplier<Graph<Entity, StaticEdge>> {
        @Override
        public Graph<Entity, StaticEdge> get() {
            return new ModifiableSparseGraph<>();
        }
    }

    static class EdgeFactory implements Supplier<StaticEdge> {
        @Override
        public StaticEdge get() {
            return StaticEdge.generateEdge();
        }
    }

    static class NodeFactory implements Supplier<Entity> {
        AgentGenerator genny = new AgentGenerator();
        int row, col = 0;
        @Override
        public Entity get() {
            ++row;
            ++col;
            Entity e = null;
            do {
                e = genny.generate(new Location(row, col));
            } while  (e == null);
            return e;
        }
    }

    static class NodePainter implements Function<Entity, Paint> {
        @Override
        public Paint apply(Entity e) {
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

}
