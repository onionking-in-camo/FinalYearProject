package main;

import data.GUIData;
import edu.uci.ics.jung.visualization.VisualizationImageServer;

import javax.swing.*;

public class NetworkView extends JFrame {

    private final String STEP_PREFIX = "Step: ";
    private final String POP_PREFIX = "Population: ";

    public NetworkView(int height, int width) {
        setTitle("MAS Disease Spread Network");
        setLocation(GUIData.SIM_X, GUIData.SIM_Y);
        getContentPane().add(new JLabel(STEP_PREFIX, JLabel.CENTER));
        getContentPane().add((new JLabel(POP_PREFIX, JLabel.LEFT)));
    }

    public void addNetwork(VisualizationImageServer vis) {
        getContentPane().add(vis);
    }
}
