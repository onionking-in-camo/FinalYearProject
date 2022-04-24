package actors;

import data.SimData;
import environment.Location;

import java.util.Random;

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
        double value = SimData.getRandom().nextDouble();
        if (value <= SimData.getAgentZeroProbability()) {
            Agent ag = new Agent(l);
            ag.setStatus(ag.getStatus().nextState());
            return ag;
        }
        //
        double offset = SimData.getAgentZeroProbability();
        if (value <= SimData.getAgentProbability() + offset && value > offset) {
            Agent ag = new Agent(l);
            if (willDistance(SimData.getRandom()))
                ag.willDistance(true);
            if (isMasked(SimData.getRandom()))
                ag.setMasked(true);
            if (willQuarantine(SimData.getRandom()))
                ag.willQuarantine(true);
            return ag;
        }
        return null;
    }

    private boolean willDistance(Random rand) {
        return SimData.getSocialDistancing() && rand.nextDouble() < SimData.getSocialDistancingCompliance();
    }

    private boolean isMasked(Random rand) {
        return SimData.getMasking() && rand.nextDouble() < SimData.getMaskCompliance();
    }

    private boolean willQuarantine(Random rand) {
        return SimData.getQuarantining() && rand.nextDouble() < SimData.getSymptomaticProbability();
    }
}