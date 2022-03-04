package main;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;

import data.GUIData;

public class GraphView extends JFrame implements PropertyChangeListener {

    private static final long serialVersionUID = -1236441407025451806L;
    private int[][] initData;
    JPanel g;

    private final XYChart graph = new XYChartBuilder()
            .width(500)
            .height(500)
            .title("Simulation Graph")
            .xAxisTitle("Step")
            .yAxisTitle("Number of person(s)")
            .build();

    public GraphView(Simulator sim) {
        sim.addPropertyChangeListener(this);
        initData = new int[][] { {0}, {0} };
        graph.addSeries("Susceptible", initData[0], initData[1]);
        graph.addSeries("Infected", initData[0], initData[1]);
        graph.addSeries("Recovered", initData[0], initData[1]);
        setTitle("Simulation Graph");
        setLocation(GUIData.GRAPH_X, GUIData.GRAPH_Y);
        g = new XChartPanel<XYChart>(graph);
        add(g);
        pack();
        setVisible(true);
    }

    public void captureGraphImage(String imagePath) {
        try {
            BitmapEncoder.saveBitmap(graph, imagePath, BitmapFormat.PNG);
        } catch (Exception e) {
            System.out.println("GraphView::captureGraphImage failed. Unable to save image.");
            System.out.println(e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        List<String[]> newData = (List<String[]>) evt.getNewValue();
        double[] stepsX = new double[newData.size()];
        double[] susValuesY = new double[newData.size()];
        double[] infValuesY = new double[newData.size()];
        double[] recValuesY = new double[newData.size()];
        for (int i = 0; i < newData.size(); i++) {
            String[] line = newData.get(i);
            stepsX[i] = i;
            susValuesY[i] = Integer.parseInt(line[0]);
            infValuesY[i] = Integer.parseInt(line[1]);
            recValuesY[i] = Integer.parseInt(line[2]);
        }
        graph.updateXYSeries("Susceptible", stepsX, susValuesY, null);
        graph.updateXYSeries("Infected", stepsX, infValuesY, null);
        graph.updateXYSeries("Recovered", stepsX, recValuesY, null);
        g.updateUI();
    }
}