package actors;

import java.util.*;

import data.SimData;
import environment.Field;
import environment.Grid;
import environment.Location;
import environment.Direction;
import models.SIR;
import models.Infected;
import models.Susceptible;

public class Agent extends Entity {

    private SIR status;
    private int health;
    private List<Action> actions;
    private Set<Percept> percepts;
    private boolean distancing = false;
    private boolean masked = false;
    private boolean symptomatic = false;
    private boolean quarantining = false;

    public Agent(Location location) {
        super(location);
        status = new Susceptible();
        percepts = new HashSet<>();

        // add actions to agent in order of priority
        actions = new ArrayList<>();
        actions.add(0, new Quarantine());
        actions.add(1, new MoveDQ());
        actions.add(2, new MoveQuarantined());
        actions.add(3, new MoveDistanced());
        actions.add(4, new MoveRandom());

        // randomly generate a number that reflects the duration that the agent will be sick, if infected
        health = SimData.getRandom().nextInt(
                SimData.INFECTIOUS_PERIOD_MAX - SimData.INFECTIOUS_PERIOD_MIN) + SimData.INFECTIOUS_PERIOD_MIN;
        // set the direction in which the agent will initially move
//        direction = Direction.getRandomDirection();
    }

    /**
     * Causes agent to take action;
     * it observes the environment and performs an
     * action depending on its goals
     *
     * @param f the field of play
     */
    public void act(Field<Entity, Location> f) {
        see(f);
        processDisease();
        for (Action action : actions) {
            if (action.act(f))
                break;
        }
    }

    public int getHealth() { return health; }

    public void setHealth(int health) { this.health = health; }

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

    /** Beginning of actions
     * Defines the actions that an Agent is capable of
     * making.
     *
     */
    interface Action {
        boolean act(Field<Entity, Location> f);
        default List<Location> orderBestLocations(Field<Entity, Location> f, Location origin) {
            List<Location> freeLocs = f.getAllFreeAdjacentLocations(origin);
            Collections.sort(freeLocs, Comparator.comparingInt(elem -> f.getAllNeighbours(elem, Agent.class).size()));
            return freeLocs;
        };
        default Location findNonQuarantineLocation(Field<Entity, Location> f, List<Location> moves) {
            Set<Location> quLocations = f.getZone();
            for (Location loc : moves) {
                if (!quLocations.contains(loc)) {
                    return loc;
                }
            }
            return null;
        }
    }

    private class MoveRandom implements Action {
        @Override
        public boolean act(Field<Entity, Location> f) {
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
        public boolean act(Field<Entity, Location> f) {
            if (symptomatic && getStatus().getClass() == Infected.class) {
                if (!quarantining) {
                    quarantining = true;
                    f.registerZone(new HashSet<>(f.getAllAdjacentLocations(getLocation())));
                }
                return true;
            }
            else {
                if (quarantining) {
                    quarantining = false;
                    f.deregisterZone(new HashSet<>(f.getAllAdjacentLocations(getLocation())));
                }
                return false;
            }
        }
    }

    private class MoveDQ implements Action {
        @Override
        public boolean act(Field<Entity, Location> f) {
            if (SimData.QUARANTINING && distancing) {
                List<Location> freeLocs = orderBestLocations(f, getLocation());
                if (!freeLocs.isEmpty()) {
                    Location choice = findNonQuarantineLocation(f, freeLocs);
                    if (choice != null) {
                        move(f, choice);
                        return true;
                    }
                    move(f, freeLocs.get(0));
                    return true;
                }
                return true;
            }
            return false;
        }
    }

    private class MoveQuarantined implements Action {
        @Override
        public boolean act(Field<Entity, Location> f) {
            if (SimData.QUARANTINING) {
                List<Location> freeLocs = f.getAllFreeAdjacentLocations(getLocation());
                if (!freeLocs.isEmpty()) {
                    Collections.shuffle(freeLocs);
                    Location move = findNonQuarantineLocation(f, freeLocs);
                    if (move != null) {
                        move(f, move);
                        return true;
                    }
                    move(f, freeLocs.get(0));
                    return true;
                }
                return true;
            }
            return false;
        }
    }

    private class MoveDistanced implements Action {
        @Override
        public boolean act(Field<Entity, Location> f) {
            if (distancing) {
                List<Location> freeLocs = orderBestLocations(f, getLocation());
                if (!freeLocs.isEmpty()) {
                    move(f, freeLocs.get(0));
                }
                return true;
            }
            return false;
        }
    }
    /** End of actions
     */

    /**
     * Move from current location, to new location.
     * @param f - the field representation
     * @param to - the location to move to
     */
    private void move(Field<Entity, Location> f, Location to) {
        f.clearLocation(this.getLocation());
        f.place(to, this);
        this.setLocation(to);
    }

    /**
     * Agent determines what percepts (deducible facts from its
     * environment) are present, and adds them to its beliefs.
     * @param f
     */
    private void see(Field<Entity, Location> f) {
        // look for obstacles
//        if (f.pathObstructed(this)) percepts.add(Percept.OBSTACLE_IN_FRONT);
//        else percepts.remove(Percept.OBSTACLE_IN_FRONT);

    }

    private void processDisease() {
        if (status instanceof Infected) {
            if (health == 0) status = status.nextState();
            else health--;
        }
    }
}