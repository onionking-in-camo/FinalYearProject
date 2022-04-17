package main;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import actors.Agent;
import actors.Entity;
import data.GUIData;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.visualization.VisualizationImageServer;
import environment.*;

import java.util.HashMap;
import java.util.Set;

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
public class SimulatorDisplay extends JFrame {

    private final String STEP_PREFIX = "Step: ";
    private final String TOTAL_AG_PREFIX = "Population(s): ";
    private JLabel step;
    private JLabel population;
    private FieldDisplay fieldDisplay;
    private FieldStats stats;
    private HashMap<Class<?>, Color> colors;

    public SimulatorDisplay(Field f) {
        stats = new FieldStats();
        colors = new HashMap<Class<?>, Color>();

        setTitle("MAS Disease Spread");
        step = new JLabel(STEP_PREFIX, JLabel.CENTER);
        population = new JLabel(TOTAL_AG_PREFIX, JLabel.LEFT);

        setLocation(GUIData.SIM_X, GUIData.SIM_Y);

        fieldDisplay = getFieldDisplay(f);

//        if (f instanceof Grid) {
//            fieldDisplay = new GridDisplay(height, width);
//        }
//
//        if (f instanceof MobileNetwork) {
//            fieldDisplay = new NetworkDisplay((MobileNetwork) f, height, width);
//        }

        Container contents = getContentPane();
        contents.add(step, BorderLayout.NORTH);
        contents.add(fieldDisplay, BorderLayout.CENTER);
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

    private FieldDisplay getFieldDisplay(Field field) {
        if (field instanceof Grid) {
            return new GridDisplay(field);
        }
        if (field instanceof MobileNetwork) {
            return new NetworkDisplay(field);
        }
        return null;
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
     public void showStatus(int step, Field<Entity, Location> field) {
        if (!isVisible()) { setVisible(true); }
        this.step.setText(STEP_PREFIX + step);
        stats.reset();
        population.setText(TOTAL_AG_PREFIX + stats.getPopulationDetails2(field));
        stats.countFinished();
        fieldDisplay.prepare();
        fieldDisplay.update();
    }

    public boolean isViable() {
        return stats.isViable();
    }

    private abstract class FieldDisplay extends JPanel {

        protected int height;
        protected int width;
        protected Dimension size;
        protected final int SCALING_FACTOR = 6;

        public FieldDisplay(int width, int height) {
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
        public abstract void update();
    }

    private class NetworkDisplay extends FieldDisplay {

        private VisualizationImageServer vs;
        private MobileNetwork f;

        public NetworkDisplay(Field field) {
            super(field.getDimensions(), field.getDimensions());
            this.f = (MobileNetwork) field;
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
        public void update() {
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
    private class GridDisplay extends FieldDisplay {

//        private int gridWidth, gridHeight;
        private int xScale, yScale;
        private Graphics g;
        private Image fieldImage;
        private Grid grid;

        public GridDisplay(Field f) {
            super(f.getDimensions(), f.getDimensions());
//            gridHeight = height;
//            gridWidth = width;
//            this.height =
            grid = (Grid) f;
        }

        @Override
        public void prepare() {
            preparePaint();
        }

        @Override
        public void update() {
            Set<Location> qu = grid.getZone();
            for (int row = 0; row < grid.getDimensions(); row++) {
                for (int col = 0; col < grid.getDimensions(); col++) {
                    Object actor = grid.getObjectAt(new Location(row, col));
                    if (actor != null) {
                        stats.incrementCount(actor);
                        if (actor instanceof Agent) {
                            Agent ag = (Agent) actor;
                            this.drawMark(col, row, colors.get(ag.getStatus().getClass()));
                        }
                    }
                    else {
                        if (qu.contains(new Location(row, col))) {
                            this.drawMark(col, row, GUIData.QU_COL);
                        }
                        else this.drawMark(col, row, GUIData.EMP_COL);
                    }
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
                xScale = size.width / width;
                if (xScale < 1)
                    xScale = SCALING_FACTOR;
                yScale = size.height / height;
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