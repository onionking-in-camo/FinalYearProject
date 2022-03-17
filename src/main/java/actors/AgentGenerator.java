package actors;

import data.SimData;
import environment.Location;

public class AgentGenerator extends EntityGenerator<Entity> {

    /**
     * Generate an Agent at the specified location l. Characteristics of the individual
     * are assigned randomly, using parameters from the SimData data class.
     * An Agent can either be Susceptible or Infected, or null, i.e. there is a chance
     * that no Agent is generated.
     * @param l - the location to place the new Agent
     * @return Agent (Susceptible or Infected) or null
     */
    @Override
    protected Entity generateEntity(Location l) {
        if (SimData.getRandom().nextDouble() < SimData.AGENT_ZERO_PROB) {
            Agent ag = new Agent(l);
            ag.setStatus(ag.getStatus().nextState());
            return ag;
        }
        //
        if (SimData.getRandom().nextDouble() < SimData.AGENT_PROB) {
            Agent ag = new Agent(l);
            if (isDistancing())
                ag.setDistancing(true);
            if (isMasked())
                ag.setMasked(true);
            if (isSymptomatic())
                ag.setSymptomatic(true);
            return ag;
        }
        else return new Agent(l);
//        return null;
    }


    private boolean isDistancing() {
        return SimData.SOCIAL_DISTANCING && SimData.getRandom().nextDouble() < SimData.SOCIAL_DISTANCING_COMPLIANCE;
    }

    private boolean isMasked() {
        return SimData.MASK_MANDATE && SimData.getRandom().nextDouble() < SimData.MASK_COMPLIANCE;
    }

    private boolean isSymptomatic() {
        return SimData.QUARANTINING && SimData.getRandom().nextDouble() < SimData.SYMPTOMATIC;
    }
}