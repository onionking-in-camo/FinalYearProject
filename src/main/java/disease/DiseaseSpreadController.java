package disease;

import actors.Agent;
import data.SimData;
import environment.Field;
import models.Infected;
import models.SIR;
import models.Susceptible;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DiseaseSpreadController {

    public static List<Agent> getInfectedContacts(List<Agent> allAgents, Field field) {
        List<Agent> contacts = new ArrayList<>();
        for (Iterator<Agent> it = allAgents.iterator(); it.hasNext();) {
            Agent ag = it.next();
            if (ag.getStatus().getClass() == Susceptible.class && nearInfected(field, ag)) {
                contacts.add(ag);
            }
        }
        return contacts;
    }

    public static List<Agent> getNewlyInfected(List<Agent> contacts, Field field) {
        List<Agent> newlyInfected = new ArrayList<>();
        for (Agent ag : contacts) {
            List<Agent> neighbours = field.getAllNeighbours(ag.getLocation(), Agent.class);
            List<Agent> infectedNeighbours = neighbours
                    .stream()
                    .filter(DiseaseSpreadController::isInfected)
                    .toList();
            double pInfected = calculateProbabilityOfInfection(infectedNeighbours);
            if (SimData.getRandom().nextDouble() <= pInfected)
                newlyInfected.add(ag);
        }
        return newlyInfected;

    }

    public static double calculateProbabilityOfInfection(List<Agent> neighbours) {
        double pUninfected = 1.0;
        for (Agent infected : neighbours) {
            double rateOfInfection = SimData.getInfectivity();
            if (infected.getMasked()) {
                rateOfInfection *= SimData.getMaskRiskReduction();
            }
            pUninfected *= (1.0 - rateOfInfection);
        }
        return 1.0 - pUninfected;
    }

    public static void infectAll(List<Agent> toInfect) {
//        List<Agent> infected = new ArrayList<>();
        for (Agent ag : toInfect) {
            if (ag.getStatus().getClass() == Susceptible.class) {
                ag.setStatus(ag.getStatus().nextState());
            }
//            infected.add(ag);
        }
//        return infected;
    }

    public static boolean isInfected(Agent ag) {
        return ag.getStatus().getClass() == Infected.class;
    }

    public static boolean nearInfected(Field f, Agent ag) {
        List<Agent> neighbours = f.getAllNeighbours(ag.getLocation(), Agent.class);
        for (Iterator<Agent> it = neighbours.iterator(); it.hasNext();) {
            if (isInfected(it.next())) {
                return true;
            }
        }
        return false;
    }
}
