package main;
import java.awt.*;
import java.util.*;

import actors.Agent;
import actors.Entity;
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
import java.util.List;

/**
 * @author Maria Chli
 * @author James J Kerr
 * @version 08-12-21
 */
public class Simulator {

    public static class SimulationBuilder {
        private int width;
        private int depth;
        private Field<Entity, Location> field;
        private SimulatorDisplay display;
        private Map<Class<?>, Color> classColours = new HashMap<>();
        private List<PropertyChangeListener> listeners = new ArrayList<>();

        public SimulationBuilder setWidth(int w) {
            width = w;
            return this;
        }

        public SimulationBuilder setDepth(int d) {
            depth = d;
            return this;
        }

        public SimulationBuilder setField(FieldType type) {
            if (type == FieldType.GRID)    { field = new Grid(depth, width); }
            if (type == FieldType.NETWORK) { field = new MobileNetwork(); }
            display = new SimulatorDisplay(field);
            return this;
        }

        public SimulationBuilder setClassColour(Class<?> cl, Color co) {
            classColours.put(cl, co);
            return this;
        }

        public SimulationBuilder addListener(PropertyChangeListener l) {
            listeners.add(l);
            return this;
        }

        public Simulator build() {
            Simulator sim = new Simulator();
            sim.width = this.width;
            sim.depth = this.depth;
            sim.field = this.field;
            sim.display = this.display;
            classColours.forEach((k, v) -> {
                sim.display.setColor(k, v);
            });
            listeners.forEach(listener -> {
                sim.addPropertyChangeListener(listener);
            });
            sim.step = 0;
            sim.setup = false;
            sim.finished = false;
            sim.agents = new ArrayList<>();
            sim.stats = new FieldStats();
            sim.record = new SimulationRecord();
            sim.record.addHeader(new String[] {"Seed", "Agent Probability", "Agent 0 Probability", "Infectiousness", "Social Distancing", "Mask Mandate", "Quarantining", "Field type"});
            sim.record.addHeader(new String[] {String.valueOf(SimData.SEED), String.valueOf(SimData.AGENT_PROB), String.valueOf(SimData.AGENT_ZERO_PROB), String.valueOf(SimData.INFECTIVITY), String.valueOf(SimData.SOCIAL_DISTANCING), String.valueOf(SimData.MASK_MANDATE), String.valueOf(SimData.QUARANTINING), field.getClass().getSimpleName()});
            sim.record.addHeader(new String[] {"Susceptible", "Infected", "Recovered"});
            sim.supp = new PropertyChangeSupport(sim);
            sim.graph = new GraphDisplay(sim);
            sim.reset();
            return sim;
        }
    }

    private int width;
    private int depth;
    private Field<Entity, Location> field;
    private SimulatorDisplay display;
    private int step;
    private boolean setup;
    private boolean finished;
    private List<Agent> agents;
    private FieldStats stats;
    private SimulationRecord record;
    private PropertyChangeSupport supp;
    private GraphDisplay graph;

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
        display.showStatus(step, field);
    }

    public void simulate() {
        while (display.isViable()) {
            simulateStep();
            updateRecord();
        }
        finished = true;
    }

    public void simulate(int steps) {
        for (int step = 1; step <= steps && display.isViable(); step++) {
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
        this.display.setVisible(false);
        this.graph.setVisible(false);
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
        display.showStatus(step, field);

        // slow down the simulation
        try { Thread.sleep(SimData.RUN_DELAY);	} catch (Exception e) { /* TODO: handle exception */ }
    }

    private void populate() {
        field.initialise();
        agents = field.getAllOf(Agent.class);
    }

    private void updateRecord() {
        record.addRecord(stats.getClassCount(field,
                Arrays.asList(Susceptible.class, Infected.class, Recovered.class)));
        supp.firePropertyChange("record", null, record.getData());
    }
}