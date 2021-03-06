package main;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

import data.GUIData;
import data.SimData;
import environment.*;
import exceptions.IllegalParameterException;
import models.Infected;
import models.Recovered;
import models.Susceptible;

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
    private LabelledTextArea saveFilePath;

    private JButton setUpButton;
    private JButton stepOnceButton;
    private JButton runLongButton;
    private JButton resetButton;
    private JButton quitButton;
    // simulation params
    private String defaultSimLength = String.valueOf(SimData.getRuntime());
    private String defaultSimSeed = String.valueOf(SimData.getSeed());
    private String defaultSimSize = String.valueOf(SimData.getWidth());
    // disease params
    private String defaultInfectiousness = String.valueOf(SimData.getInfectivity());
    private String defaultSymptomaticProb = String.valueOf(SimData.getSymptomaticProbability());
    private String defaultMaskReduction = String.valueOf(SimData.getMaskRiskReduction());
    // compliance params
    private String defQuarantineCompliance = String.valueOf(SimData.getQuarantineCompliance());
    private String defSocialDistancingCompliance = String.valueOf(SimData.getSocialDistancingCompliance());
    private String defMaskWearingCompliance = String.valueOf(SimData.getMaskCompliance());
    // agent params
    private String agentProb = String.valueOf(SimData.getAgentProbability());
    private String agentZeroProb = String.valueOf(SimData.getAgentZeroProbability());
    //
    private String defaultOutputPath = String.valueOf(SimData.DATA_FILE_PATH);

    private ButtonGroup fieldGroup;

    private Simulator simulator;

    public static void main(String[] args) {
        new Main();
    }

    public Main() {

        setUpButton = new JButton();
        stepOnceButton = new JButton();
        runLongButton = new JButton();
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
        quarantineCompliance = new LabelledTextArea("Quarantine Compliance: ", defQuarantineCompliance);
        socialDistancingCompliance = new LabelledTextArea("Distancing Compliance: ", defSocialDistancingCompliance);
        maskWearingCompliance = new LabelledTextArea("Mask Compliance: ", defMaskWearingCompliance);

        // policy options
        socialDistancing = new LabelledCheckBox("Social Distancing: ", false);
        maskMandate = new LabelledCheckBox("Mask Mandate: ", false);
        quarantining = new LabelledCheckBox("Quarantining: ", false);

        // data output path
        saveFilePath = new LabelledTextArea("File name: ", defaultOutputPath);

        // field options
        JRadioButton gridBox = new JRadioButton();
        gridBox.setEnabled(true);
        gridBox.setSelected(true);
        gridBox.setActionCommand("Grid");
        gridBox.setText("Grid");

        JRadioButton staticBox = new JRadioButton();
        staticBox.setEnabled(true);
        staticBox.setActionCommand("Smallworld");
        staticBox.setText("Smallworld Network");

        JRadioButton mobileBox = new JRadioButton();
        mobileBox.setEnabled(true);
        mobileBox.setActionCommand("Freescale");
        mobileBox.setText("Freescale Network");

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
        JPanel filePathBox = new JPanel();

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
        filePathBox.setBorder(new TitledBorder("Output Path"));

        // set layout
        mainFrame.getContentPane().setLayout(new BorderLayout());

        topBox.setLayout(new GridLayout(2,1));
        middleBox.setLayout((new GridLayout(2, 1)));
        topMiddleBox.setLayout(new BorderLayout());
        lowMiddleBox.setLayout(new BorderLayout());
        lowerBox.setLayout(new GridLayout(2, 1));

        paramBox.setLayout(new GridLayout(2, 2));
        fieldBox.setLayout(new GridLayout(1,1));

        agentBox.setLayout(new GridLayout(2,1));
        policiesBox.setLayout(new GridLayout(3,1));

        diseaseBox.setLayout(new GridLayout(3,1));
        complianceBox.setLayout(new GridLayout(3,1));

        filePathBox.setLayout(new GridLayout(1, 1));
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

        filePathBox.add(saveFilePath);

        commandBox.add(setUpButton);
        commandBox.add(resetButton);
        commandBox.add(stepOnceButton);
        commandBox.add(runLongButton);
        commandBox.add(quitButton);

        lowerBox.add(filePathBox, BorderLayout.NORTH);
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
                if (simulator.finished()) {
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
            setParams();
            simulator = new Simulator.SimulationBuilder()
                    .setDepth(SimData.getDepth())
                    .setWidth(SimData.getWidth())
                    .setField(SimData.getFieldType())
                    .setClassColour(Susceptible.class, GUIData.SUS_COL)
                    .setClassColour(Infected.class, GUIData.INF_COL)
                    .setClassColour(Recovered.class, GUIData.REC_COL)
                    .build();

        } catch (IllegalParameterException e) {
            JOptionPane.showMessageDialog(mainFrame,
                    "Parameter setup failed." +
                            e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(mainFrame,
                    "Problem creating simulation." +
                            e.getMessage());
        }

        setUpButton.setEnabled(false);
        stepOnceButton.setEnabled(true);
        runLongButton.setEnabled(true);
        resetButton.setEnabled(true);
    }

    private void runOnce() {
        runIterations(1);
    }

    private void runIterations(int iterations) {
        simulator.simulate(iterations);
    }

    private void run() {
        simulator.simulate();
    }

    private void save() {
        String filePath = SimData.DATA_FILE_DIR + SimData.DATA_FILE_PATH;
        simulator.saveData(filePath);
        simulator.saveImage(filePath);
    }

    private void reset() {
        if (this.simulator != null)
            simulator.closeView();
        setUpButton.setEnabled(true);
        stepOnceButton.setEnabled(false);
        runLongButton.setEnabled(false);
        runLongButton.setText("Run");
        runLongButton.setToolTipText("Run simulation for the duration specified.");
    }

    private void setParams() throws IllegalParameterException {
        SimData.setRuntime(Integer.parseInt(simLength.getValue()));
        SimData.setSeed(Integer.parseInt(simSeed.getValue()));
        SimData.setWidth(Integer.parseInt(mapWidth.getValue()));
        SimData.setDepth(Integer.parseInt(mapDepth.getValue()));
        SimData.setAgentProbability(Double.parseDouble(agentCreationProb.getValue()));
        SimData.setAgentZeroProbability(Double.parseDouble(agentZeroCreationProb.getValue()));
        SimData.setInfectivity(Double.parseDouble(infectiousness.getValue()));
        SimData.setSocialDistancing(socialDistancing.getValue());
        SimData.setSocialDistancingCompliance(Double.parseDouble(this.socialDistancingCompliance.getValue()));
        SimData.setMasking(maskMandate.getValue());
        SimData.setMaskCompliance(Double.parseDouble(this.maskWearingCompliance.getValue()));
        SimData.setMaskRiskReduction(Double.parseDouble(this.maskWearingReduction.getValue()));
        SimData.setQuarantining(quarantining.getValue());
        SimData.setQuarantiningCompliance(Double.parseDouble(this.quarantineCompliance.getValue()));
        SimData.setSymptomaticProbability(Double.parseDouble(this.symptomaticProb.getValue()));
        SimData.DATA_FILE_PATH = String.valueOf(saveFilePath.getValue());
        String fieldType = fieldGroup.getSelection().getActionCommand();
//        String networkType =
        Class fieldClass;
        if (fieldType.equals("Grid")) {
            fieldClass = Grid.class;
            SimData.setFieldType(FieldType.GRID);
        }
        else {
            fieldClass = MobileNetwork.class;
            SimData.setFieldType(FieldType.NETWORK);
            if (fieldType.equals("Smallworld"))
                SimData.setNetworkType(NetworkType.SMALLWORLD);
            else if (fieldType.equals("Freescale"))
                SimData.setNetworkType(NetworkType.FREESCALE);
        }
        SimData.setFieldClass(fieldClass);
    }
}