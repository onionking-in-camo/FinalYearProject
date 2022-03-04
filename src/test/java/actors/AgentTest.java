package actors;

import models.Susceptible;
import org.junit.jupiter.api.Test;
import environment.Location;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AgentTest {
    @Test
    void whenAgentIsInstantiated_shouldBeSusceptible() {
        Agent ag = new Agent(new Location(0, 0));
        assertEquals(Susceptible.class, ag.getStatus().getClass());
    }
}
