package main;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import actors.Agent;
import data.GUIData;
import environment.Grid;
import environment.FieldStats;

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

    private static final long serialVersionUID = 1L;
    private static final Color EMPTY_COLOR = Color.white;
    private static final Color UNKNOWN_COLOR = Color.gray;

    private final String STEP_PREFIX = "Step: ";
    private final String TOTAL_AG_PREFIX = "Population(s): ";
    private JLabel stepLabel, population;
    private FieldView fieldView;

    // A map for storing colors for participants in the simulation
    private HashMap<Class<?>, Color> colors;
    // A statistics object computing and storing simulation information
    private FieldStats stats;

    public SimulatorView(int height, int width) {
        stats = new FieldStats();
        colors = new HashMap<Class<?>, Color>();

        setTitle("MAS Disease Spread");
        stepLabel = new JLabel(STEP_PREFIX, JLabel.CENTER);
        population = new JLabel(TOTAL_AG_PREFIX, JLabel.LEFT);

        setLocation(GUIData.SIM_X, GUIData.SIM_Y);


        fieldView = new FieldView(height, width);

        Container contents = getContentPane();
        contents.add(stepLabel, BorderLayout.NORTH);
        contents.add(fieldView, BorderLayout.CENTER);
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

    @SuppressWarnings("unused")
    private Color getColor(Class<?> actorClass) {
        Color col = colors.get(actorClass);
        if (col != null) {
            return col;
        }
        return UNKNOWN_COLOR;
    }

    /**
     * Show the current status of the field.
     * @param step Which iteration step it is.
     * @param stats Status of the field to be represented.
     */
    public void showStatus(int step, Grid grid) {
        if (!isVisible())
            setVisible(true);

        stepLabel.setText(STEP_PREFIX + step);
        stats.reset();
        fieldView.preparePaint();

        for (int row = 0; row < grid.getDepth(); row++) {
            for (int col = 0; col < grid.getWidth(); col++) {
                Object actor = grid.getObjectAt(row, col);
                if (actor != null) {
                    stats.incrementCount(actor);
                    if (actor instanceof Agent) {
                        Agent ag = (Agent) actor;
                        fieldView.drawMark(col, row, colors.get(ag.getStatus().getClass()));
                    }
                }
                else
                    fieldView.drawMark(col, row, EMPTY_COLOR);
            }
        }
        stats.countFinished();
        population.setText(TOTAL_AG_PREFIX + stats.getPopulationDetails(grid));
        fieldView.repaint();
    }

    public boolean isViable(Grid grid) {
        return stats.isViable(grid);
    }

    /**
     * Provide a graphical view of a rectangular field. This is
     * a nested class (a class defined inside a class) which
     * defines a custom component for the user interface. This
     * component displays the field.
     * This is rather advanced GUI stuff - you can ignore this
     * for your project if you like.
     */
    private class FieldView extends JPanel {
        static final long serialVersionUID = -3018063635072997091L;
        private final int GRID_VIEW_SCALING_FACTOR = 6;

        private int gridWidth, gridHeight;
        private int xScale, yScale;
        Dimension size;
        private Graphics g;
        private Image fieldImage;

        /**
         * Create a new FieldView component.
         */
        public FieldView(int height, int width) {
            gridHeight = height;
            gridWidth = width;
            size = new Dimension(0, 0);
        }

        /**
         * Tell the GUI manager how big we would like to be.
         */
        public Dimension getPreferredSize() {
            return new Dimension(gridWidth * GRID_VIEW_SCALING_FACTOR,
                    gridHeight * GRID_VIEW_SCALING_FACTOR);
        }

        /**
         * Prepare for a new round of painting. Since the component
         * may be resized, compute the scaling factor again.
         */
        public void preparePaint() {
            if (!size.equals(getSize())) {  // if the size has changed...
                size = getSize();
                fieldImage = fieldView.createImage(size.width, size.height);
                g = fieldImage.getGraphics();
                xScale = size.width / gridWidth;
                if (xScale < 1)
                    xScale = GRID_VIEW_SCALING_FACTOR;
                yScale = size.height / gridHeight;
                if (yScale < 1)
                    yScale = GRID_VIEW_SCALING_FACTOR;
            }
        }

        /**
         * Paint on grid location on this field in a given color.
         */
        public void drawMark(int x, int y, Color color) {
            g.setColor(color);
            g.fillRect(x * xScale, y * yScale, xScale-1, yScale-1);
        }

        public void paint(Graphics g) {
            if (fieldImage != null) {
                g.drawImage(fieldImage, 0, 0, null);
            }
        }
    }
}