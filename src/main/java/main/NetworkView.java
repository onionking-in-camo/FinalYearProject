package main;

import data.GUIData;

import javax.swing.*;

public class NetworkView extends JFrame {

    private final String STEP_PREFIX = "Step: ";
    private final String POP_PREFIX = "Population: ";

    public NetworkView() {
        setTitle("MAS Disease Spread MobileNetwork");
        setLocation(GUIData.SIM_X, GUIData.SIM_Y);
        getContentPane().add(new JLabel(STEP_PREFIX, JLabel.CENTER));
        getContentPane().add((new JLabel(POP_PREFIX, JLabel.LEFT)));
    }
}
