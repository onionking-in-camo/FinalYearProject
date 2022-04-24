package actors;

import environment.Field;
import environment.Location;
import environment.Grid;
import models.Susceptible;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class AgentTest {

    Field f;

    @BeforeEach
    void setup() {
        f = new Grid(2, 2);
    }

    @Test
    void whenAgentIsInstantiated_shouldBeSusceptible() {
        Agent ag = new Agent(new Location(0, 0));
        assertEquals(Susceptible.class, ag.getStatus().getClass());
    }

    @Test
    void whenAgentMoves_agentLocationShouldChange() {
        Location origin = new Location(1, 1);
        Agent ag = new Agent(origin);
        f.place(origin, ag);
        ag.act(f);
        assertEquals(f.getObjectAt(origin), null);
        assertNotEquals(ag.getLocation(), origin);
    }

    @Test
    void whenAgentMoves_thereIsNoFreeLocations_agentLocationShouldNotChange() {
        Location origin = new Location(1,1);
        Agent ag = new Agent(origin);
    }
}
