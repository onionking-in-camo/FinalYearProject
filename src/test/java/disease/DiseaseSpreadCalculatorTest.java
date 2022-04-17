package disease;

import actors.Agent;
import actors.Entity;
import environment.Field;
import environment.Grid;
import environment.Location;
import models.Infected;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

public class DiseaseSpreadCalculatorTest {

    Grid f;
    int width = 3;
    int depth = 3;

    @BeforeEach
    void setup() {
        f = new Grid(width, depth);
    }

    void fillWithInfected(Field<Entity, Location> f) {
        for (int col = 0; col < width; col++) {
            for (int row = 0; row < depth; row++) {
                Location loc = new Location(row, col);
                Agent ag = new Agent(loc);
                ag.setStatus(new Infected());
                f.place(loc, ag);
            }
        }
    }

    @Test
    void getInfectedContacts_shouldHaveEntries_whenAllContactsAreInfected() {
        fillWithInfected(f);
//        System.out.println(f.toString());
//        Agent s = new Agent(new Location(1, 1));
//        List<Agent> agent = Arrays.asList(s);
//        f.place(new Location(1, 1), s);
//        System.out.println(f);
//        List<Agent> infectedContacts = DiseaseSpreadCalculator.getInfectedContacts(f, f);
//        assertTrue(!infectedContacts.isEmpty());
//        assertEquals(8, infectedContacts.size());
    }

}
