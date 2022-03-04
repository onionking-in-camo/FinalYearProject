package actors;

import data.SimData;
import environment.Location;

public class AgentGenerator extends EntityGenerator<Entity> {

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
        return null;
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