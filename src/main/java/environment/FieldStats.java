package environment;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import actors.Agent;
import data.Counter;
import models.Infected;

/**
 * This class collects and provides some statistical data on the state
 * of a field. It is flexible: it will create and maintain a counter
 * for any class of object that is found within the field.
 *
 * @author David J. Barnes and Michael Kolling
 * @version 2002-04-23
 * @version Ian T. Nabney
 * @version 01-02-2005
 */
public class FieldStats {
    // Counters for each type of entity (host, artist, etc.) in the simulation.
    private HashMap<Class<?>, Counter> counters;
    // Whether the counters are currently up to date.
    private boolean valid;

    /**
     * Construct a field-statistics object.
     */
    public FieldStats()
    {
        // Set up a collection for counters for each type of person that
        // we might find
        counters = new HashMap<Class<?>, Counter>();
        valid = true;
    }

    /**
     * @return A string describing what persons are in the field.
     */
    public String getPopulationDetails(Grid grid)
    {
        StringBuffer buffer = new StringBuffer();
        if(!valid) {
            generateCounts(grid);
        }
        Iterator<Class<?>> keys = counters.keySet().iterator();
        while(keys.hasNext()) {
            Counter info = counters.get(keys.next());
            buffer.append("[");
            buffer.append(info.getName());
            buffer.append(" -> ");
            buffer.append(info.getCount());
            buffer.append("]");
            buffer.append(' ');
        }
        return buffer.toString();
    }

    public String[] getClassCount(Grid grid, List<Class<?>> classes) {
        String[] results = new String[classes.size()];
        generateCounts(grid);
        for (int i = 0; i < classes.size(); i++) {
            if (counters.containsKey(classes.get(i))) {
                results[i] = String.valueOf(counters.get(classes.get(i)).getCount());
            }
            else {
                results[i] = String.valueOf(0);
            }
        }
        return results;
    }

    /**
     * Invalidate the current set of statistics; reset all
     * counts to zero.
     */
    public void reset()
    {
        valid = false;
        Iterator<Class<?>> keys = counters.keySet().iterator();
        while(keys.hasNext()) {
            Counter cnt = counters.get(keys.next());
            cnt.reset();
        }
    }

    /**
     * Increment the count for one class of animal.
     */
    public void incrementCount(Object actor)
    {
        Class<?> actorClass = actor.getClass();
        if (actorClass == Agent.class) {
            Agent ag = (Agent) actor;
            actorClass = ag.getStatus().getClass();
            Counter c = (Counter) counters.get(Agent.class);
            if (c == null) {
                c = new Counter(Agent.class.getSimpleName());
                counters.put(Agent.class, c);
            }
            c.increment();
        }

        Counter counter = (Counter) counters.get(actorClass);
        if (counter == null) {
            // we do not have a counter for this species yet - create one
            counter = new Counter(actorClass.getSimpleName());
            counters.put(actorClass, counter);
        }
        counter.increment();
    }

    /**
     * Indicate that an person count has been completed.
     */
    public void countFinished() {
        valid = true;
    }

    /**
     * Determine whether the simulation is still viable.
     * I.e., should it continue to run.
     * @return true If there is more than one types of Actor present.
     */
    public boolean isViable(Grid grid) {
        Counter c = (Counter) counters.get(Infected.class);
        return c.getCount() > 0;
    }

    /**
     * Generate counts of the number of persons.
     * These are not kept up to date as persons
     * are placed in the field, but only when a request
     * is made for the information.
     */
    private void generateCounts(Grid grid) {
        reset();
        for(int row = 0; row < grid.getDepth(); row++) {
            for(int col = 0; col < grid.getWidth(); col++) {
                Object animal = grid.getObjectAt(row, col);
                if (animal != null) {
                    incrementCount(animal);
                }
            }
        }
        valid = true;
    }
}
