package disease;

import actors.Agent;
import environment.Location;
import models.Infected;
import models.Recovered;
import models.Susceptible;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SIRTest {

    Agent agS;
    Agent agI;

    @BeforeEach
    void setup() {
        agS = new Agent(new Location(1, 1));
        agI = new Agent(new Location(2, 2));
        agI.setStatus(new Infected());
        assertTrue(agS.getStatus().getClass() == Susceptible.class);
        assertTrue(agI.getStatus().getClass() == Infected.class);
    }

    @Test
    void s_whenNextState_thenI() {
        agS.setStatus(agS.getStatus().nextState());
        assertEquals(Infected.class, agS.getStatus().getClass());
    }

    @Test
    void i_whenNextState_thenR() {
        agI.setStatus(agI.getStatus().nextState());
        assertEquals(Recovered.class, agI.getStatus().getClass());
    }
}