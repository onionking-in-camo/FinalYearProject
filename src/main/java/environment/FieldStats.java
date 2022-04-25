package environment;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import actors.Agent;
import actors.Entity;
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
 * @author James J Kerr
 * @version 01-01-2022
 */
public class FieldStats {
    
    private HashMap<Class<?>, Counter> counters; // counter for each Class type
    private boolean valid; // flag to signify if the stats are up-to-date

    /**
     * Construct and initialise field-statistics object.
     */
    public FieldStats() {
        counters = new HashMap<Class<?>, Counter>();
        valid = true;
    }

    public String[] getClassCount(Field field, List<Class<?>> classes) {
        String[] results = new String[classes.size()];
        generateCounts(field);
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
    public void reset() {
        valid = false;
        Iterator<Class<?>> keys = counters.keySet().iterator();
        while(keys.hasNext()) {
            Counter count = counters.get(keys.next());
            count.reset();
        }
    }

    /**
     * Increment the count for the specified actor.
     */
    public void incrementCount(Object actor) {
        
        /** if actor is an Agent, create counts for each of the
         * SIR epidemiological compartments and increment each
         * count accordingly. Count of each SIR model are needed
         * for two purposes;
         * 1) Recording the simulation requires counting the 
         * populations of Agents by SIR status as each time step.
         * 2) The simulation should only run until the number of
         * Infected agents reaches 0.
         */
        Class<?> actorClass = actor.getClass();
        if (actorClass == Agent.class) {
            Agent ag = (Agent) actor;
            actorClass = ag.getStatus().getClass();
            Counter c = counters.get(Agent.class);
            if (c == null) {
                c = new Counter(Agent.class.getSimpleName());
                counters.put(Agent.class, c);
            }
            c.increment();
        }
        
        Counter counter = counters.get(actorClass);
        if (counter == null) {
            // we do not have a counter for this species yet - create one
            counter = new Counter(actorClass.getSimpleName());
            counters.put(actorClass, counter);
        }
        counter.increment();
    }

    /**
     * Indicate that a person count has been completed.
     */
    public void countFinished() {
        valid = true;
    }

    /**
     * Determine whether the simulation is still viable.
     * i.e., should it continue to run.
     * @return true if there are still Infected agents
     */
    public boolean isViable() {
        Counter c = counters.get(Infected.class);
        return c.getCount() > 0;
    }

    private void generateCounts(Field field) {
        reset();
        List<Entity> entities = field.getAllEntities();
        for (Entity e : entities) {
            incrementCount(e);
        }
        valid = true;
    }

    public String getPopulationDetails(Field field) {
        StringBuffer buffer = new StringBuffer();
        if (!valid) {
            generateCounts(field);
        }
        Iterator<Class<?>> keys = counters.keySet().iterator();
        while (keys.hasNext()) {
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
}
