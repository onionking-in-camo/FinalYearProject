package disease;

import actors.Agent;
import data.SimData;
import environment.Field;
import models.Infected;
import models.Susceptible;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DiseaseSpreadCalculator {

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
                    .filter(DiseaseSpreadCalculator::isInfected)
                    .toList();

            double uninfectedProb = 1.0; // the chance that the agent will remain uninfected

            // for every infected neighbour, we calculate the decrease in probability
            // that the susceptible agent will remain uninfected
            for (Agent infectedAg : infectedNeighbours) {
                double infRate = SimData.INFECTIVITY;
                if (infectedAg.getMasked()) { infRate *= SimData.MASK_WEARING_REDUCTION; }
                uninfectedProb *= (1.0 - infRate);
            }

            // the chance the susceptible agent becomes infected is the
            // inverse of the chance that the agent remains uninfected
            double infectedProb = 1.0 - uninfectedProb;
            if (SimData.getRandom().nextDouble() < infectedProb)
                newlyInfected.add(ag);
        }
        return newlyInfected;

    }

    private static boolean isInfected(Agent ag) {
        return ag.getStatus().getClass() == Infected.class;
    }

    private static boolean nearInfected(Field f, Agent ag) {
        List<Agent> neighbours = f.getAllNeighbours(ag.getLocation(), Agent.class);
        for (Iterator<Agent> it = neighbours.iterator(); it.hasNext();) {
            if (isInfected(it.next())) {
                return true;
            }
        }
        return false;
    }
}
