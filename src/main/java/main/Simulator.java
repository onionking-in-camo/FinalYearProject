package main;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import actors.EntityGenerator;
import actors.Agent;
import actors.AgentGenerator;
import actors.Entity;
import data.SimData;
import data.SimulationRecord;
import environment.Grid;
import environment.FieldStats;
import environment.Location;
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
    private Grid grid;
    private GraphView graph;
    private SimulatorView view;
    private ArrayList<Agent> agents;
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
        grid = new Grid(depth, width);
        view = new SimulatorView(depth, width);
        view.setColor(Susceptible.class, SimData.SUSCEPTIBLE_COLOR);
        view.setColor(Infected.class, SimData.INFECTED_COLOR);
        view.setColor(Recovered.class, SimData.RECOVERED_COLOR);
        stats = new FieldStats();
        record = new SimulationRecord();
        record.addHeader(new String[] {
                "Seed", "Ag Prob.", "Ag0 Prob.", "Infectiousness", "Social Distancing?", "Mask Mandate?", "Quarantining?"
        });
        record.addHeader(new String[] {
                String.valueOf(SimData.SEED), String.valueOf(SimData.AGENT_PROB), String.valueOf(SimData.AGENT_ZERO_PROB),
                String.valueOf(SimData.INFECTIVITY), String.valueOf(SimData.SOCIAL_DISTANCING),
                String.valueOf(SimData.MASK_MANDATE), String.valueOf(SimData.QUARANTINING)
        });
        record.addHeader(new String[] {
                "Susceptible", "Infected", "Recovered"
        });
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
        view.showStatus(step, grid);
    }

    public void simulate() {
        while (view.isViable(grid)) {
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
        for (int step = 1; step <= steps && view.isViable(grid); step++) {
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
        Grid temp = grid.cloneField();

        // register all susceptible agents who are in
        // close contact with an infected agent
        List<Agent> contacts = new ArrayList<>();
        for (Iterator<Agent> it = agents.iterator(); it.hasNext();) {
            Agent ag = it.next();
            if (ag.getStatus().getClass() == Susceptible.class && nearInfected(temp, ag)) {
                contacts.add(ag);
            }
        }

        // transmit contagion to contacts according to infection rate
        contacts.forEach(ag -> {
            List<Entity> neighbours = temp.getAllNeighbours(ag.getLocation(), Agent.class);
            List<Entity> infectedNeighbours = neighbours.stream().filter(this::isInfected).toList();
            // prob will represent the chance that an agent will not be infected
            double prob = 1.0;
            for (Entity e : infectedNeighbours) {
                Agent x = (Agent) e;
                double infectionRate = SimData.INFECTIVITY;
                // if infected agent is masked, reduce the infection rate
                if (x.getMasked()) {
                    infectionRate *= SimData.MASK_WEARING_REDUCTION;
                }
                prob *= (1.0 - infectionRate);
            }
            double infectionProb = 1.0 - prob;
            if (SimData.getRandom().nextDouble() < infectionProb)
                ag.setStatus(ag.getStatus().nextState());
        });

        // have all agents act
        for (Iterator<Agent> it = agents.iterator(); it.hasNext();) {
            Agent ag = it.next();
            ag.act(temp);
        }

        // set the old field to the updated field
        grid = temp;
        step++;
        view.showStatus(step, grid);

        // slow down the simulation
        try { Thread.sleep(70);	} catch (Exception e) { /* TODO: handle exception */ }
    }

    private boolean isInfected(Entity e) {
        if (e instanceof Agent) {
            Agent ag = (Agent) e;
            return ag.getStatus().getClass() == Infected.class;
        }
        return false;
    }

    private boolean nearInfected(Grid f, Agent ag) {
        List<Entity> neighbours = f.getAllNeighbours(ag.getLocation(), Agent.class);
        for (Iterator<Entity> it = neighbours.iterator(); it.hasNext();) {
            if (isInfected(it.next())) {
                return true;
            }
        }
        return false;
    }

    private void populate() {
        grid.clear();
        EntityGenerator<Entity> generator = new AgentGenerator();
        for (int row = 0; row < grid.getDepth(); row++) {
            for (int col = 0; col < grid.getWidth(); col++) {
                Location location = new Location(row, col);
                if (grid.getObjectAt(location) == null) {
                    Entity ag = generator.generate(location);
                    if (ag instanceof Agent) {
                        Agent a = (Agent) ag;
                        if (ag != null) {
                            grid.place(ag, location);
                            this.agents.add(a);
                        }
                    }
                }
            }
        }
    }

    private void updateRecord() {
        record.addRecord(stats.getClassCount(grid,
                Arrays.asList(Susceptible.class, Infected.class, Recovered.class)));
        supp.firePropertyChange("record", null, record.getData());
    }
}