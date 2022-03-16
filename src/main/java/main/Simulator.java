package main;
import java.util.*;

import actors.Agent;
import actors.Entity;
import data.GUIData;
import data.SimData;
import data.SimulationRecord;
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
    private boolean setup, finished;

    public Simulator() {
        this(SimData.WIDTH, SimData.DEPTH);
    }

    /**
     * Create a simulation field with the given size.
     * @param depth Depth of the field. Must be greater than zero.
     * @param width Width of the field. Must be greater than zero.
     */
    public Simulator(int depth, int width) {
        field = new Grid(depth, width);
//        field = new Network();
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

    public SimulationRecord getRecord() {
        return record;
    }

    public boolean hasFinished() {
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

        // register all susceptible agents who are in
        // close contact with an infected agent
        List<Agent> contacts = new ArrayList<>();
        for (Iterator<Agent> it = agents.iterator(); it.hasNext();) {
            Agent ag = it.next();
            if (ag.getStatus().getClass() == Susceptible.class && nearInfected(field, ag)) {
                contacts.add(ag);
            }
        }

        List<Agent> toInf = new ArrayList<>();

        // transmit contagion to contacts according to infection rate
        contacts.forEach(ag -> {
            List<Agent> infectedNeighbours = field
                    .getAllNeighbours(ag.getLocation(), Agent.class)
                    .stream()
                    .filter(this::isInfected)
                    .toList();
            // prob will represent the chance that an agent will not be infected
            double prob = 1.0;
            for (Agent e : infectedNeighbours) {
//                Agent x = (Agent) e;
                double infectionRate = SimData.INFECTIVITY;
                // if infected agent is masked, reduce the infection rate
                if (e.getMasked()) {
                    infectionRate *= SimData.MASK_WEARING_REDUCTION;
                }
                prob *= (1.0 - infectionRate);
            }
            double infectionProb = 1.0 - prob;
            if (SimData.getRandom().nextDouble() < infectionProb) { toInf.add(ag); }
        });

        // have all agents act
        for (Iterator<Agent> it = agents.iterator(); it.hasNext();) {
            Agent ag = it.next();
            ag.act(field);
        }

        for (Agent a : toInf) {
            a.setStatus(a.getStatus().nextState());
        }

        step++;
        view.showStatus(step, field);

        // slow down the simulation
        try { Thread.sleep(100);	} catch (Exception e) { /* TODO: handle exception */ }
    }

    private boolean isInfected(Object e) {
        if (e instanceof Agent) {
            Agent ag = (Agent) e;
            return ag.getStatus().getClass() == Infected.class;
        }
        return false;
    }

    private boolean nearInfected(Field f, Agent ag) {
        List<Entity> neighbours = f.getAllNeighbours(ag.getLocation(), Agent.class);
        for (Iterator<Entity> it = neighbours.iterator(); it.hasNext();) {
            if (isInfected(it.next())) {
                return true;
            }
        }
        return false;
    }

    private void populate() {
//        grid.clear();
//        grid.clearAll();
        field.initialise();
        agents = field.getAllEntities().stream().filter((e) -> e instanceof Agent).toList();
//        grid.populate();
//        EntityGenerator<Entity> generator = new AgentGenerator();
////        for (int row = 0; row < grid.getDepth(); row++) {
//        for (int row = 0; row < grid.getDimensions(); row++) {
////            for (int col = 0; col < grid.getWidth(); col++) {
//            for (int col = 0; col < grid.getDimensions(); col++) {
//                Location location = new Location(row, col);
//                if (grid.getObjectAt(location) == null) {
//                    Entity ag = generator.generate(location);
//                    if (ag instanceof Agent) {
//                        Agent a = (Agent) ag;
//                        if (ag != null) {
////                            grid.place(ag, location);
//                            grid.place(location, ag);
//                            this.agents.add(a);
//                        }
//                    }
//                }
//            }
//        }
        System.out.println(agents.size());
    }

    private void updateRecord() {
        record.addRecord(stats.getClassCount(field,
                Arrays.asList(Susceptible.class, Infected.class, Recovered.class)));
        supp.firePropertyChange("record", null, record.getData());
    }
}