package main;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import actors.Agent;
import data.GUIData;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.visualization.VisualizationImageServer;
import environment.*;

import java.util.HashMap;

/**
 * A graphical view of the simulation grid.
 * The view displays a colored rectangle for
 * each location representing its contents.
 * It uses a default background color. Colors
 * for each type of species can be defined
 * using the setColor method.
 *
 * (ITN) modified so that when window is closed
 * the application terminates.
 *
 * @author David J. Barnes and Michael Kolling
 * @version 2002-04-23
 * @author Ian T. Nabney
 * @version 02-02-2005
 * @author Maria Chli
 * @version 01-11-2009
 * @author James J. Kerr
 * @version 07-12-2021
 */
public class SimulatorView extends JFrame {

    private final String STEP_PREFIX = "Step: ";
    private final String TOTAL_AG_PREFIX = "Population(s): ";
    private JLabel stepLabel, population;
    private AbstractView view;

    // A map for storing colors for participants in the simulation
    private HashMap<Class<?>, Color> colors;
    // A statistics object computing and storing simulation information
    private FieldStats stats;

    public SimulatorView(int height, int width, Field f) {
        stats = new FieldStats();
        colors = new HashMap<Class<?>, Color>();

        setTitle("MAS Disease Spread");
        stepLabel = new JLabel(STEP_PREFIX, JLabel.CENTER);
        population = new JLabel(TOTAL_AG_PREFIX, JLabel.LEFT);

        setLocation(GUIData.SIM_X, GUIData.SIM_Y);

        if (f instanceof Grid) {
            view = new FieldView(height, width);
        }

        if (f instanceof MobileNetwork) {
            view = new NetworkView((MobileNetwork) f, height, width);
        }

        Container contents = getContentPane();
        contents.add(stepLabel, BorderLayout.NORTH);
        contents.add(view, BorderLayout.CENTER);
        contents.add(population, BorderLayout.SOUTH);

        // Make sure that closing the window ends the application
        this.setDefaultCloseOperation(
                WindowConstants.DISPOSE_ON_CLOSE);

        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                exitApp();
            }
        });

        pack();
        setVisible(true);
    }

    private void exitApp() {
        System.exit(0);
    }

    public void setColor(Class<?> actorClass, Color color) {
        colors.put(actorClass, color);
    }

    /**
     * Show the current status of the field.
     * @param step Which iteration step it is.
     */
     public void showStatus(int step, Field grid) {
        if (!isVisible())
            setVisible(true);

        stepLabel.setText(STEP_PREFIX + step);
        stats.reset();
        population.setText(TOTAL_AG_PREFIX + stats.getPopulationDetails2(grid));
        stats.countFinished();
        view.prepare();
        view.update(grid);

    }

    public boolean isViable() {
        return stats.isViable();
    }

    private abstract class AbstractView extends JPanel {

        protected int height;
        protected int width;
        protected Dimension size;
        protected final int SCALING_FACTOR = 6;

        public AbstractView(int width, int height) {
            this.height = height;
            this.width = width;
            size = new Dimension(0, 0);
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(
                    width * SCALING_FACTOR,
                    height * SCALING_FACTOR
            );
        }

        public abstract void prepare();
        public abstract void update(Field f);
    }

    private class NetworkView extends AbstractView {

        private VisualizationImageServer vs;
        private MobileNetwork f;

        public NetworkView(MobileNetwork f, int height, int width) {
            super(height, width);
            this.f = f;
        }

        @Override
        public void prepare() {
            vs = new VisualizationImageServer<>(
                new ISOMLayout<>(f.getGraph()),
                getPreferredSize());
            vs.getRenderContext().setVertexFillPaintTransformer(f.getPainter());
            this.add(vs);
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(1000, 600);
        }

        @Override
        public void update(Field f) {
            vs.updateUI();
        }
    }

    /**
     * Provide a graphical view of a rectangular field. This is
     * a nested class (a class defined inside a class) which
     * defines a custom component for the user interface. This
     * component displays the field.
     * This is rather advanced GUI stuff - you can ignore this
     * for your project if you like.
     */
    private class FieldView extends AbstractView {

        private int gridWidth, gridHeight;
        private int xScale, yScale;
        private Graphics g;
        private Image fieldImage;

        public FieldView(int height, int width) {
            super(height, width);
            gridHeight = height;
            gridWidth = width;
        }

        @Override
        public void prepare() {
            preparePaint();
        }

        @Override
        public void update(Field f) {
            for (int row = 0; row < f.getDimensions(); row++) {
                for (int col = 0; col < f.getDimensions(); col++) {
                    Object actor = f.getObjectAt(new Location(row, col));
                    if (actor != null) {
                        stats.incrementCount(actor);
                        if (actor instanceof Agent) {
                            Agent ag = (Agent) actor;
                            this.drawMark(col, row, colors.get(ag.getStatus().getClass()));
                        }
                    } else
                        this.drawMark(col, row, GUIData.EMP_COL);
                }
            }
            repaint();
        }

        /**
         * Prepare for a new round of painting. Since the component
         * may be resized, compute the scaling factor again.
         */
        private void preparePaint() {
            if (!size.equals(getSize())) {
                size = getSize();
                fieldImage = this.createImage(size.width, size.height);
                g = fieldImage.getGraphics();
                xScale = size.width / gridWidth;
                if (xScale < 1)
                    xScale = SCALING_FACTOR;
                yScale = size.height / gridHeight;
                if (yScale < 1)
                    yScale = SCALING_FACTOR;
            }
        }

        private void drawMark(int x, int y, Color color) {
            g.setColor(color);
            g.fillRect(x * xScale, y * yScale, xScale-1, yScale-1);
        }

        @Override
        public void paint(Graphics g) {
            if (fieldImage != null) {
                g.drawImage(fieldImage, 0, 0, null);
            }
        }
    }
}