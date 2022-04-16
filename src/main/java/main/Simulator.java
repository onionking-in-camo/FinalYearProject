package main;
import java.util.*;

import actors.Agent;
import data.GUIData;
import data.SimData;
import data.SimulationRecord;
import disease.DiseaseSpreadCalculator;
import environment.*;
import io.CSVWriter;
import models.Infected;
import models.Recovered;
import models.Susceptible;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * @author Maria Chli
 * @author James J Kerr
 * @version 08-12-21
 */
public class Simulator {

    private int step;
    private Field field;
    private GraphView graph;
    private SimulatorView view;
    private List<Agent> agents;
    private FieldStats stats;
    private SimulationRecord record;
    private PropertyChangeSupport supp;
    private boolean setup;
    private boolean finished;

    public Simulator() {
        this(SimData.WIDTH, SimData.DEPTH);
    }

    /**
     * Create a simulation field with the given size.
     * @param depth Depth of the field. Must be greater than zero.
     * @param width Width of the field. Must be greater than zero.
     */
    public Simulator(int depth, int width) {
        setField(depth, width);
        view = new SimulatorView(depth, width, field);
        view.setColor(Susceptible.class, GUIData.SUS_COL);
        view.setColor(Infected.class, GUIData.INF_COL);
        view.setColor(Recovered.class, GUIData.REC_COL);
        stats = new FieldStats();
        record = new SimulationRecord();
        record.addHeader(new String[] {
                "Seed",
                "Ag Prob.",
                "Ag0 Prob.",
                "Infectiousness",
                "Social Distancing",
                "Mask Mandate",
                "Quarantining",
                "Field type"
        });
        record.addHeader(new String[] {
                String.valueOf(SimData.SEED),
                String.valueOf(SimData.AGENT_PROB),
                String.valueOf(SimData.AGENT_ZERO_PROB),
                String.valueOf(SimData.INFECTIVITY),
                String.valueOf(SimData.SOCIAL_DISTANCING),
                String.valueOf(SimData.MASK_MANDATE),
                String.valueOf(SimData.QUARANTINING),
                field.getClass().getSimpleName()
        });
        record.addHeader(new String[] {"Susceptible", "Infected", "Recovered"});
        agents = new ArrayList<>();
        supp = new PropertyChangeSupport(this);
        graph = new GraphView(this);
        setup = false;
        finished = false;
        reset();
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        supp.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        supp.removePropertyChangeListener(listener);
    }

    public boolean finished() {
        return setup && finished;
    }

    public void reset() {
        step = 0;
        setup = true;
        finished = false;
        populate();
        updateRecord();
        view.showStatus(step, field);
    }

    public void simulate() {
        while (view.isViable()) {
            simulateStep();
            updateRecord();
        }
        finished = true;
    }

    /**
     * Run the simulation from its current state for the given number of steps.
     * Stop before the given number of steps if it ceases to be viable.
     * @param steps The number of steps to run for.
     */
    public void simulate(int steps) {
        for (int step = 1; step <= steps && view.isViable(); step++) {
            simulateStep();
            updateRecord();
        }
    }

    public void saveData(String filePath) {
        try {
            CSVWriter writer = new CSVWriter();
            writer.writeCSV(record.getFullRecord(), filePath);
        } catch (Exception e) {
            System.out.println("Simulator::saveData failed.");
            System.out.println(e.getMessage());
        }
    }

    public void saveImage(String imagePath) {
        graph.captureGraphImage(imagePath);
    }

    public void closeView() {
        this.view.setVisible(false);
    }

    private void simulateStep() {

        List<Agent> contacts = DiseaseSpreadCalculator.getInfectedContacts(this.agents, this.field);
        List<Agent> agentsToInfect = DiseaseSpreadCalculator.getNewlyInfected(contacts, this.field);

        // have all agents act
        for (Iterator<Agent> it = agents.iterator(); it.hasNext();) {
            Agent ag = it.next();
            ag.act(field);
        }

        // update infection status of newly infected agents
        for (Agent a : agentsToInfect) {
            a.setStatus(a.getStatus().nextState());
        }

        step++;
        view.showStatus(step, field);

        // slow down the simulation
        try { Thread.sleep(SimData.RUN_DELAY);	} catch (Exception e) { /* TODO: handle exception */ }
    }

    private void populate() {
        field.initialise();
        agents = field.getAllEntities().stream().filter((ag) -> ag instanceof Agent).toList();
    }

    private void setField(int d, int w) {
        if (SimData.FIELD_TYPE == Grid.class) {
            field = new Grid(d, w);
        }
        else if (SimData.FIELD_TYPE == MobileNetwork.class) {
            field = new MobileNetwork();
        }
    }

    private void updateRecord() {
        record.addRecord(stats.getClassCount(field,
                Arrays.asList(Susceptible.class, Infected.class, Recovered.class)));
        supp.firePropertyChange("record", null, record.getData());
    }
}