package main;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

import data.GUIData;
import data.SimData;
import environment.Grid;
import environment.MobileNetwork;
import environment.StaticNetwork;

public class Main {

    private JFrame mainFrame;
    private LabelledTextArea simLength;
    private LabelledTextArea simSeed;
    private LabelledTextArea mapWidth;
    private LabelledTextArea mapDepth;
    private LabelledTextArea agentCreationProb;
    private LabelledTextArea agentZeroCreationProb;
    private LabelledTextArea infectiousness;
    private LabelledTextArea symptomaticProb;
    private LabelledTextArea maskWearingReduction;
    private LabelledTextArea quarantineCompliance;
    private LabelledTextArea socialDistancingCompliance;
    private LabelledTextArea maskWearingCompliance;
    private LabelledCheckBox socialDistancing;
    private LabelledCheckBox maskMandate;
    private LabelledCheckBox quarantining;

    private JButton setUpButton;
    private JButton stepOnceButton;
    private JButton runLongButton;
    private JButton runIterationsButton;
    private JButton resetButton;
    private JButton quitButton;
    // simulation params
    private String defaultSimLength = String.valueOf(SimData.RUNTIME);
    private String defaultSimSeed = String.valueOf(SimData.SEED);
    private String defaultSimSize = String.valueOf(SimData.WIDTH);
    // disease params
    private String defaultInfectiousness = String.valueOf(SimData.INFECTIVITY);
    private String defaultSymptomaticProb = String.valueOf(SimData.SYMPTOMATIC);
    private String defaultMaskReduction = String.valueOf(SimData.MASK_WEARING_REDUCTION);
    // compliance params
    private String defQuarantineCompliance = String.valueOf(SimData.SELF_QUARANTINE_COMPLIANCE);
    private String defSocialDistancingCompliance = String.valueOf(SimData.SOCIAL_DISTANCING_COMPLIANCE);
    private String defMaskWearingCompliance = String.valueOf(SimData.MASK_COMPLIANCE);
    // agent params
    private String agentProb = String.valueOf(SimData.AGENT_PROB);
    private String agentZeroProb = String.valueOf(SimData.AGENT_ZERO_PROB);

    private ButtonGroup fieldGroup;

    private Simulator s;

    public static void main(String[] args) {
        new Main();
    }

    public Main() {

        setUpButton = new JButton();
        stepOnceButton = new JButton();
        runLongButton = new JButton();
        runIterationsButton = new JButton();
        resetButton = new JButton();
        quitButton = new JButton();

        // sim params
        simLength = new LabelledTextArea("Simulation Duration: ", defaultSimLength);
        simSeed = new LabelledTextArea("Simulation Seed: ", defaultSimSeed);
        mapWidth = new LabelledTextArea("Map Width: ", defaultSimSize);
        mapDepth = new LabelledTextArea("Map Depth: ", defaultSimSize);

        // agent params
        agentCreationProb = new LabelledTextArea("Agent Prob: ", agentProb);
        agentZeroCreationProb = new LabelledTextArea("Agent Zero Prob: ", agentZeroProb);

        // disease params
        infectiousness = new LabelledTextArea("Infectiousness: ", defaultInfectiousness);
        symptomaticProb = new LabelledTextArea("Symptomatic Prob: ", defaultSymptomaticProb);
        maskWearingReduction = new LabelledTextArea("Mask Risk Reduction: ", defaultMaskReduction);

        // compliance params
        quarantineCompliance = new LabelledTextArea("Quarantine Compliance: ", this.defQuarantineCompliance);
        socialDistancingCompliance = new LabelledTextArea("Distancing Compliance: ", this.defSocialDistancingCompliance);
        maskWearingCompliance = new LabelledTextArea("Mask Compliance: ", this.defMaskWearingCompliance);

        // policy options
        socialDistancing = new LabelledCheckBox("Social Distancing: ", false);
        maskMandate = new LabelledCheckBox("Mask Mandate: ", false);
        quarantining = new LabelledCheckBox("Quarantining: ", false);

        // field options
        JRadioButton gridBox = new JRadioButton();
        gridBox.setEnabled(true);
        gridBox.setSelected(true);
        gridBox.setActionCommand("Grid");
        gridBox.setText("Grid");

        JRadioButton staticBox = new JRadioButton();
        staticBox.setEnabled(true);
        staticBox.setActionCommand("Static");
        staticBox.setText("Static Network");

        JRadioButton mobileBox = new JRadioButton();
        mobileBox.setEnabled(true);
        mobileBox.setActionCommand("Mobile");
        mobileBox.setText("Mobile Network");

        fieldGroup = new ButtonGroup();
        fieldGroup.add(gridBox);
        fieldGroup.add(staticBox);
        fieldGroup.add(mobileBox);

        setUpButton.setText("Set up simulation");
        setUpButton.setToolTipText("Feed simulation parameters and set up simulation.");
        setUpButton.setEnabled(true);
        stepOnceButton.setText("Step Once");
        stepOnceButton.setToolTipText("Run simulation for only one step.");
        stepOnceButton.setEnabled(false);
        runLongButton.setText("Run");
        runLongButton.setToolTipText("Run simulation for the duration specified.");
        runLongButton.setEnabled(false);
        runIterationsButton.setText("Run for Duration");
        runIterationsButton.setToolTipText("Run a number of iterations of simulation.");
        runIterationsButton.setEnabled(false);
        resetButton.setText("Reset");
        resetButton.setToolTipText("Allow changing of the parameters.");
        resetButton.setEnabled(false);
        quitButton.setText("Quit");
        quitButton.setToolTipText("Quit simulation.");

        mainFrame = new JFrame("Disease Simulation Setup");
        mainFrame.setLocation(GUIData.SETUP_X, GUIData.SETUP_Y);

        JPanel topBox = new JPanel();
        JPanel middleBox = new JPanel();
        JPanel lowerBox = new JPanel();

        JPanel topMiddleBox = new JPanel();
        JPanel lowMiddleBox = new JPanel();

        JPanel paramBox = new JPanel();
        JPanel commandBox = new JPanel();
        JPanel agentBox = new JPanel();
        JPanel policiesBox = new JPanel();
        JPanel diseaseBox = new JPanel();
        JPanel complianceBox = new JPanel();
        JPanel fieldBox = new JPanel();

        // add borders
        topBox.setBorder(BorderFactory.createEtchedBorder());
        middleBox.setBorder(BorderFactory.createEtchedBorder());
        lowerBox.setBorder(BorderFactory.createEtchedBorder());
        topMiddleBox.setBorder(BorderFactory.createEtchedBorder());
        lowMiddleBox.setBorder(BorderFactory.createEtchedBorder());

        paramBox.setBorder(new TitledBorder("Simulation Parameters"));
        fieldBox.setBorder(new TitledBorder("Field Topology"));
        agentBox.setBorder(new TitledBorder("Agents"));
        policiesBox.setBorder(new TitledBorder("Policies"));
        diseaseBox.setBorder(new TitledBorder("Disease"));
        complianceBox.setBorder(new TitledBorder("Compliance"));
        commandBox.setBorder(new TitledBorder("Commands"));

        // set layout
        mainFrame.getContentPane().setLayout(new BorderLayout());

        topBox.setLayout(new GridLayout(2,1));
        middleBox.setLayout((new GridLayout(2, 1)));
        topMiddleBox.setLayout(new BorderLayout());
        lowMiddleBox.setLayout(new BorderLayout());
        lowerBox.setLayout(new GridLayout(1, 1));

        paramBox.setLayout(new GridLayout(2, 2));
        fieldBox.setLayout(new GridLayout(1,1));

        agentBox.setLayout(new GridLayout(2,1));
        policiesBox.setLayout(new GridLayout(3,1));

        diseaseBox.setLayout(new GridLayout(3,1));
        complianceBox.setLayout(new GridLayout(3,1));

        commandBox.setLayout(new GridLayout(3,1));

        /*
         * Start of topBox
         */
        paramBox.add(simLength);
        paramBox.add(mapWidth);
        paramBox.add(simSeed);
        paramBox.add(mapDepth);

        fieldBox.add(gridBox);
        fieldBox.add(staticBox);
        fieldBox.add(mobileBox);

        topBox.add(paramBox);
        topBox.add(fieldBox);
        /*
         * End of topBox
         */

        /*
         * Start of middleBox
         */
            /*
             * Start of topMiddleBox
             */
            agentBox.add(agentCreationProb);
            agentBox.add(agentZeroCreationProb);

            policiesBox.add(socialDistancing);
            policiesBox.add(maskMandate);
            policiesBox.add(quarantining);

            topMiddleBox.add(agentBox, BorderLayout.CENTER);
            topMiddleBox.add(policiesBox, BorderLayout.EAST);
            /*
             * End of topMiddleBox
             */

            /*
             * Start of lowMiddleBox
             */
            diseaseBox.add(infectiousness);
            diseaseBox.add(symptomaticProb);
            diseaseBox.add(maskWearingReduction);

            complianceBox.add(maskWearingCompliance);
            complianceBox.add(socialDistancingCompliance);
            complianceBox.add(quarantineCompliance);

            lowMiddleBox.add(diseaseBox, BorderLayout.CENTER);
            lowMiddleBox.add(complianceBox, BorderLayout.EAST);
            /*
             * End of lowMiddleBox
             */
        middleBox.add(topMiddleBox, BorderLayout.NORTH);
        middleBox.add(lowMiddleBox, BorderLayout.SOUTH);
        /*
         * End of middleBox
         */

        /*
         * Start of lowerBox
         */
        commandBox.add(setUpButton);
        commandBox.add(resetButton);
        commandBox.add(stepOnceButton);
        commandBox.add(runLongButton);
        commandBox.add(runIterationsButton);
        commandBox.add(quitButton);

        lowerBox.add(commandBox, BorderLayout.CENTER);
        /*
         * End of lowerBox
         */

        mainFrame.getContentPane().add(topBox, BorderLayout.NORTH);
        mainFrame.getContentPane().add(middleBox, BorderLayout.CENTER);
        mainFrame.getContentPane().add(lowerBox, BorderLayout.SOUTH);

        // set event handlers
        mainFrame.setDefaultCloseOperation(
                WindowConstants.DO_NOTHING_ON_CLOSE);

        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                exitApp();
            }
        });

        quitButton.addActionListener((e) -> exitApp());

        resetButton.addActionListener((e) -> reset());

        stepOnceButton.addActionListener((e) -> {
            new Thread(() -> {
                if (s.hasFinished()) {
                    save();
                    return;
                }
                runOnce();
                mainFrame.setVisible(true);
            }).start();
//            mainFrame.setVisible(false);
        });

        runLongButton.addActionListener((e) -> {
            new Thread(() -> {
                run();
                save();
                mainFrame.setVisible(true);
            }).start();
            mainFrame.setVisible(false);
        });

        runIterationsButton.addActionListener((e) -> {
            new Thread(() -> {
                int[] seeds = new int[] {3, 9, 81, 90, 171};
                for (int i = 0; i < 5; i++) {
                    simSeed.setValue(String.valueOf(seeds[i]));
                    setUp();
                    run();
                    save();
                }
                mainFrame.setVisible(true);
            }).start();
            mainFrame.setVisible(false);
        });

        setUpButton.addActionListener((e) -> {
            new Thread(() -> {
                setUp();
                mainFrame.setVisible(true);
            }).start();
            mainFrame.setVisible(false);
        });

        mainFrame.pack();
        mainFrame.setVisible(true);
    }

    private void exitApp() {
        int response = JOptionPane.showConfirmDialog(mainFrame,
                "Do you really want to quit?",
                "Quit?",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        if (response == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    private void setUp() {
        try {
            int runtime = (int) simLength.getValue();
            int seed = (int) simSeed.getValue();
            int width = (int) mapWidth.getValue();
            int depth = (int) mapDepth.getValue();
            double agProb = agentCreationProb.getValue();
            double agZeroProb = agentZeroCreationProb.getValue();
            boolean distancing = socialDistancing.getValue();
            boolean masks = maskMandate.getValue();
            boolean quarantine = quarantining.getValue();
            String fieldType = fieldGroup.getSelection().getActionCommand();
            Class fieldClass = null;
            if (fieldType.equals("Grid")) {
                fieldClass = Grid.class;
            }
            else if (fieldType.equals("Static")) {
                fieldClass = StaticNetwork.class;
            }
            else {
                fieldClass = MobileNetwork.class;
            }

            SimData.RUNTIME = runtime;
            SimData.SEED = seed;
            SimData.WIDTH = width;
            SimData.DEPTH = depth;
            SimData.AGENT_PROB = agProb;
            SimData.AGENT_ZERO_PROB = agZeroProb;
            SimData.SOCIAL_DISTANCING = distancing;
            SimData.MASK_MANDATE = masks;
            SimData.QUARANTINING = quarantine;
            SimData.FIELD_TYPE = fieldClass;

            try {
                s = new Simulator();
            } catch (Exception e) {
                System.out.println("Simulator instantiation failed.");
                e.printStackTrace();
            }

            setUpButton.setEnabled(false);
            stepOnceButton.setEnabled(true);
            runLongButton.setEnabled(true);
            runIterationsButton.setEnabled(true);
            resetButton.setEnabled(true);
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(mainFrame,
                    "Problem creating simulation." +
                            e.getMessage());
        }
    }

    private void runOnce() {
        runIterations(1);
    }

    private void runIterations(int iterations) {
        s.simulate(iterations);
    }

    private void run() {
        s.simulate();
    }

    private void save() {
        String filePath = "./src/main/resources/simulation_record";
        s.saveData(filePath);
        s.saveImage(filePath);
    }

    public void reset() {
        if (this.s != null)
            s.closeView();
        setUpButton.setEnabled(true);
        stepOnceButton.setEnabled(false);
        runLongButton.setEnabled(false);
        runLongButton.setText("Run");
        runLongButton.setToolTipText("Run simulation for the duration specified.");
    }
}