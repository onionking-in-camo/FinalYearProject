package actors;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import data.SimData;
import environment.Grid;
import environment.Location;
import environment.Direction;
import models.SIR;
import models.Infected;
import models.Susceptible;

public class Agent extends Entity {

    private SIR status;
    private int durationOfIllness;
    private Direction direction;
    private List<Action> actions;
    private Set<Percept> percepts;
    private boolean distancing = false;
    private boolean masked = false;
    private boolean symptomatic = false;

    public Agent(Location location) {
        super(location);
        status = new Susceptible();
        percepts = new HashSet<>();

        // add actions to agent in order of priority
        actions = new ArrayList<>();
        actions.add(0, new Quarantine());
        actions.add(1, new MoveDistanced());
        actions.add(2, new Turn());
        actions.add(3, new MoveForward());
        actions.add(4, new MoveRandom());

        durationOfIllness = SimData.getRandom().nextInt(SimData.INFECTIOUS_PERIOD_MAX - SimData.INFECTIOUS_PERIOD_MIN) + SimData.INFECTIOUS_PERIOD_MIN;
        direction = Direction.getRandomDirection();
    }

    /**
     * Causes agent to take action;
     * it observes the environment and performs an
     * action depending on its goals
     *
     * @param f the field of play
     */
    public void act(Grid f) {
        see(f);
        processDisease(f);
        for (Action action : actions) {
            if (action.act(f))
                break;
        }
    }

    public SIR getStatus() {
        return status;
    }

    public void setStatus(SIR status) {
        this.status = status;
    }

    public void setDistancing(boolean b) {
        distancing = b;
    }

    public boolean getMasked() {
        return masked;
    }

    public void setMasked(boolean b) {
        masked = b;
    }

    public void setSymptomatic(boolean b)  {
        symptomatic = b;
    }

    /*
     * Defines the actions that an Agent is capable of
     * making.
     *
     */
    interface Action {
        boolean act(Grid f);
    }

    private class MoveRandom implements Action {
        @Override
        public boolean act(Grid f) {
            Location to = f.freeAdjacentLocation(getLocation());
            if (to != null) {
                move(f, to);
                return true;
            }
            return false;
        }
    }

    private class Quarantine implements Action {
        @Override
        public boolean act(Grid f) {
            if (symptomatic && getStatus().getClass() == Infected.class) {
                return true;
            }
            return false;
        }
    }

    private class Turn implements Action {
        @Override
        public boolean act(Grid f) {
            if (percepts.contains(Percept.OBSTACLE_IN_FRONT)) {
                if (SimData.getRandom().nextDouble() < 0.5)
                    direction = direction.turnRight(direction);
                else
                    direction = direction.turnLeft(direction);
                return true;
            }
            return false;
        }
    }

    private class MoveDistanced implements Action {
        @Override
        public boolean act(Grid f) {
            if (distancing) {
                List<Location> freeLocations = f.getAllFreeAdjacentLocations(getLocation());
                if (!freeLocations.isEmpty()) {
                    Location best = null;
                    int fewestNeighbours = 9;
                    for (Location l : freeLocations) {
                        int x = f.getAllNeighbours(l, Agent.class).size();
                        if (x < fewestNeighbours) {
                            best = l;
                            fewestNeighbours = x;
                        }
                    }
                    direction = Direction.getDirectionOfLocation(getLocation(), best);
                    move(f, best);
                }
                return true;
            }
            return false;
        }
    }

    private class MoveForward implements Action {
        @Override
        public boolean act(Grid f) {
            if (!percepts.contains(Percept.OBSTACLE_IN_FRONT)) {
                Location to = Location.getLocationInDirection(getLocation(), direction);
                move(f, to);
                return true;
            }
            return false;
        }
    }
    /*
     * End of actions.
     */

    private void move(Grid f, Location to) {
        f.clearLocation(this.getLocation());
        f.place(this, to);
        this.setLocation(to);
    }

    /**
     * Agent determines what percepts (deducible facts from its
     * environment) are present, and adds them to its beliefs.
     * @param f
     */
    private void see(Grid f) {
        // look for obstacles
        if (f.pathObstructed(getLocation(), direction)) percepts.add(Percept.OBSTACLE_IN_FRONT);
        else percepts.remove(Percept.OBSTACLE_IN_FRONT);
    }

    private void processDisease(Grid f) {
        if (status instanceof Infected) {
            if (durationOfIllness == 0)
                status = status.nextState();
            else
                durationOfIllness--;
        }
    }
}