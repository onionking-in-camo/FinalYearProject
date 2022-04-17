package actors;

import data.SimData;
import environment.Field;
import environment.Location;
import environment.Grid;
import models.Infected;
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

    @Test
    void whenAgentHasContactWithInfected_thenAgentShouldBecomeInfected() {
        SimData.INFECTIVITY = 1.0;
        SimData.DEPTH = 2;
        SimData.WIDTH = 2;
        Location locS = new Location(0, 0);
        Location locI = new Location(0, 1);
        Agent s = new Agent(locS);
        Agent i = new Agent(locI);
        i.setStatus(new Infected());
        f.place(locS, s);
        f.place(locI, i);
        s.act(f);
        i.act(f);
        assertEquals(Infected.class, s.getStatus().getClass());
    }
}
