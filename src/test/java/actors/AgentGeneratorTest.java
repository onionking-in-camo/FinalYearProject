package actors;

import data.SimData;
import environment.Location;
import models.Infected;
import models.Recovered;
import models.Susceptible;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AgentGeneratorTest {
    AgentGenerator generator;
    Location loc;

    @BeforeEach
    void setup() {
        generator = new AgentGenerator();
        loc = new Location(1, 1);
    }

    @Test
    void generate_whenSusceptibleProbabilityCertain_shouldGenerateSusceptibleAgents() {
        SimData.setAgentProbability(1.0);
        SimData.setAgentZeroProbability(0.0);
        List<Agent> agents = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Entity e = generator.generate(loc);
            assertTrue(e instanceof Agent);
            Agent ag = (Agent) e;
            agents.add(ag);
        }
        for (Agent ag : agents) {
            assertEquals(Susceptible.class, ag.getStatus().getClass());
        }
    }

    @Test
    void generate_whenAllShouldBeInfected() {
        SimData.setAgentProbability(0.0);
        SimData.setAgentZeroProbability(1.0);
        List<Agent> agents = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Entity e = generator.generate(loc);
            assertTrue(e instanceof Agent);
            Agent ag = (Agent) e;
            agents.add(ag);
        }
        for (Agent ag : agents) {
            assertEquals(Infected.class, ag.getStatus().getClass());
        }
    }

    @Test
    void generate_shouldHaveNoRecovered() {
        SimData.setAgentProbability(0.5);
        SimData.setAgentZeroProbability(0.5);
        List<Agent> agents = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Entity e = generator.generate(loc);
            assertTrue(e instanceof Agent);
            Agent ag = (Agent) e;
            agents.add(ag);
        }
        for (Agent ag : agents) {
            assertNotEquals(Recovered.class, ag.getStatus().getClass());
        }
    }
}
