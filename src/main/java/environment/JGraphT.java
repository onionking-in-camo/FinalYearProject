package environment;

import actors.Agent;
import actors.AgentGenerator;
import actors.Entity;
import com.google.common.base.Supplier;
import com.mxgraph.canvas.mxGraphics2DCanvas;
import edu.uci.ics.jung.algorithms.layout.FRLayout2;
import org.jgrapht.Graph;
import org.jgrapht.alg.drawing.FRLayoutAlgorithm2D;
import org.jgrapht.alg.drawing.model.MapLayoutModel2D;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.generate.WattsStrogatzGraphGenerator;
import org.jgrapht.graph.DefaultDirectedGraph;
import com.mxgraph.swing.*;
import com.mxgraph.layout.*;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedGraph;

import javax.swing.*;

public class JGraphT {

    public JGraphT() {

    }

    static class EdgeFactory implements Supplier<Edge> {
        char aa = 'a';
        @Override
        public Edge get() {
            return new Edge(Character.toString(aa++));
        }
    }

    static class EdgeFac implements java.util.function.Supplier<Edge> {
        char aa = 'a';
        @Override
        public Edge get() {
            return new Edge(Character.toString(aa++));
        }
    }

    public static void main(String[] args) {
        DefaultUndirectedGraph<Entity, Edge> g = new DefaultUndirectedGraph<>(Edge.class);
        g.setVertexSupplier(new java.util.function.Supplier<Entity>() {
            int x = 0;
            int y = 0;
            AgentGenerator gen = new AgentGenerator();
            @Override
            public Entity get() {
                x++;
                y++;
                return gen.generate(new Location(x, y));
            }
        });
        g.setEdgeSupplier(new EdgeFac());


        WattsStrogatzGraphGenerator gen = new WattsStrogatzGraphGenerator(20, 2, 0.5);
        gen.generateGraph(g);
        JGraphXAdapter<Entity, Edge> adapter = new JGraphXAdapter<>(g);
        mxIGraphLayout layout = new mxCircleLayout(adapter);
        layout.execute(adapter.getDefaultParent());
        mxGraphComponent comp = new mxGraphComponent(adapter);
        JFrame frame = new JFrame("Demo");
        frame.getContentPane().add(comp);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
    }
}