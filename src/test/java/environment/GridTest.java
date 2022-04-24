package environment;

import actors.Agent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class GridTest {

    Grid g;
    Location l;
    int width = 5;
    int depth = 5;
    int x = 3;
    int y = 3;

    @BeforeEach
    void setup() {
        g = new Grid(width, depth);
        l = new Location(x, y);
    }

    private void fillGrid() {
        for (int col = 0; col < g.getWidth(); col++) {
            for (int row = 0; row < g.getDepth(); row++) {
                g.place(new Location(row, col), new Agent(new Location(row, col)));
            }
        }
    }

    @Test
    void place_whenAgentIsPlacedAtLocation_shouldContainAgentAtLocation() {
        Agent ag = new Agent(l);
        g.place(l, ag);
        assertTrue(g.getObjectAt(l) != null);
        assertEquals(g.getObjectAt(l), ag);
    }

    @Test
    void getObjectAt_whenAgentAtLocation_shouldContainAgent() {
        Agent ag = new Agent(l);
        g.place(l, ag);
        assertTrue(g.getObjectAt(l) != null);
        assertEquals(ag, g.getObjectAt(l));
    }

    @Test
    void getObjectAt_whenLocationEmpty_shouldContainNothing() {
        assertTrue(g.getObjectAt(l) == null);
    }

    @Test
    void clearLocation_whenLocationIsNotEmpty_shouldMakeLocationEmpty() {
        g.place(l, new Agent(l));
        assertTrue(g.getObjectAt(l) != null);
        g.clearLocation(l);
        assertTrue(g.getObjectAt(l) == null);
    }

    @Test
    void getAllAdjacentLocations_shouldNotBeEmpty() {
        assertTrue(!g.getAllAdjacentLocations(l).isEmpty());
        assertEquals(9, g.getAllAdjacentLocations(l).size());
    }

    @Test
    void getAllFreeAdjacentLocations_shouldBe9_whenFieldEmpty() {
        assertEquals(9, g.getAllFreeAdjacentLocations(l).size());
    }

    @Test
    void getAllFreeAdjacentLocations_shouldBe0_whenFieldIsFull() {
        fillGrid();
        assertEquals(0, g.getAllFreeAdjacentLocations(l).size());
    }

    @Test
    void getAllEntities_shouldBeEmpty_whenFieldIsEmpty() {
        assertEquals(Collections.EMPTY_LIST, g.getAllEntities());
    }

    @Test
    void getAllEntities_shouldBe1_when1AgentInField() {
        g.place(l, new Agent(l));
        assertEquals(1, g.getAllEntities().size());
    }

    @Test
    void getAllEntities_shouldBe25_whenFieldIsFull() {
        fillGrid();
        assertEquals(width * depth, g.getAllEntities().size());
    }

    @Test
    void getAllNeighbours_shouldBe8_whenGridIsFull() {
        fillGrid();
        assertEquals(8, g.getAllNeighbours(l, Agent.class).size());
    }

    @Test
    void getAllNeighbours_shouldBe0_whenGridIsEmpty() {
        assertEquals(0, g.getAllNeighbours(l, Agent.class).size());
    }

    @Test
    void getAllOf_shouldBe0_whenFieldIsEmpty() {
        assertEquals(0, g.getAllOf(Agent.class).size());
    }

    @Test
    void getAllOf_shouldBe25_whenFieldIsFull() {
        fillGrid();
        assertEquals(25, g.getAllOf(Agent.class).size());
    }

    @Test
    void registerZone_whenLocationsAdded() {
        Set<Location> zone = new HashSet<>();
        zone.add(new Location(1, 1));
        zone.add(new Location(2, 2));
        g.registerZone(zone);
        assertEquals(2, g.getZone().size());
    }

    @Test
    void registerZone_whenLocationsAdded_withMultipleEqualLocations() {
        Set<Location> zone = new HashSet<>();
        zone.add(new Location(1, 1));
        zone.add(new Location(1, 1));
        g.registerZone(zone);
        assertEquals(1, g.getZone().size());
    }

    @Test
    void deregisterZone_whenLocationsPresent_removePresentLocations() {
        Set<Location> zone = new HashSet<>();
        zone.add(new Location(1, 1));
        zone.add(new Location(2, 2));
        g.registerZone(zone);
        assertEquals(2, g.getZone().size());
        g.deregisterZone(zone);
        assertEquals(0, g.getZone().size());
    }
}