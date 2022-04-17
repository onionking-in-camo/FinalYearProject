package environment;
import java.util.*;
import java.util.stream.Collectors;

import actors.Agent;
import actors.AgentGenerator;
import actors.Entity;
import actors.EntityGenerator;

/**
 * @author David J. Barnes and Michael Kolling
 * @author Maria Chli
 * @author James J Kerr
 * @version 07-12-2021
 */
public class Grid implements Field<Entity, Location> {

    private int depth;
    private int width;
    private Entity[][] matrix;
    private Set<Location> quZone;

    /**
     * Create field grid where depth = number of rows,
     * and width = number of columns
     * @param depth
     * @param width
     */
    public Grid(int depth, int width) {
        this.depth = depth;
        this.width = width;
        matrix = new Entity[depth][width];
        quZone = new HashSet<>();
    }

    /**
     * Place a person at the given index.
     * If there is already a person at the location it will
     * be lost.
     * @param entity: the person to be placed.
     * @param row: row coordinate of the location.
     * @param col: column coordinate of the location.
     */
    private void placeByIndices(Entity entity, int row, int col) {
        place(new Location(row, col), entity);
    }

    /**
     * Place a person at the given location.
     * If there is already a person at the location it will
     * be lost.
     * @param entity: the person to be placed.
     * @param location: where to place the person.
     */
    private void placeByLocation(Entity entity, Location location) {
        matrix[location.getRow()][location.getCol()] = entity;
    }

    /**
     * Initialise the field by populating it with the
     * required entities at locations within the grid.
     */
    @Override
    public void initialise() {
        clearAll();
        EntityGenerator<Entity> gen = new AgentGenerator();
        for (int row = 0; row < getDimensions(); row++) {
            for (int col = 0; col < getDimensions(); col++) {
                if (matrix[row][col] == null) {
                    Location l = new Location(row, col);
                    Entity ag = gen.generate(l);
                    if (ag instanceof Agent) {
                        Agent a = (Agent) ag;
                        place(l, ag);
                    }
                }
            }
        }
    }

    @Override
    public void clearAll() {
        for (int row = 0; row < depth; row++) {
            for (int col = 0; col < width; col++) {
                matrix[row][col] = null;
            }
        }
    }

    @Override
    public void place(Location location, Entity entity) {
        placeByLocation(entity, location);
    }

    /**
     * Clear the given location.
     * If there is a person at the location it will
     * be lost.
     * @param location The location to be cleared.
     */
    @Override
    public void clearLocation(Location location) {
        matrix[location.getRow()][location.getCol()] = null;
    }

    /**
     * Return the person at the given location, if any.
     * @param location Where in the field.
     * @return The person at the given location, or null if there is none.
     */
    @Override
    public Entity getObjectAt(Location location) {
        return getObjectAt(location.getRow(), location.getCol());
    }

    @Override
    public List<Location> getAllAdjacentLocations(Location location) {
        List<Location> allLocs = new ArrayList<>();
        for (Iterator<Location> it = adjacentLocations(location); it.hasNext(); ) {
            Location loc = it.next();
            allLocs.add(loc);
        }
        return allLocs;
    }

    /**
     * Return the person at the given location, if any.
     * @param row The desired row.
     * @param col The desired column.
     * @return The person at the given location, or null if there is none.
     */
    public Entity getObjectAt(int row, int col) {
        return matrix[row][col];
    }

    /**
     * Return all free locations that are adjacent to the
     * given location. If there is none, then return an empty list.
     * @param location: the location from which to generate an adjacency.
     * @return A list of valid location within the grid area. This may be
     *         the empty list if all locations around are full.
     */
    @Override
    public ArrayList<Location> getAllFreeAdjacentLocations(Location location) {
        Iterator<Location> adjacent = adjacentLocations(location);
        ArrayList<Location> freeLocations = new ArrayList<Location>();
        while(adjacent.hasNext()) {
            Location next = adjacent.next();
            if(matrix[next.getRow()][next.getCol()] == null) {
                freeLocations.add(next);
            }
        }
        return freeLocations;
    }

    /**
     * Try to find a free location that is adjacent to the
     * given location. If there is none, then return the current
     * location if it is free. If not, return null.
     * The returned location will be within the valid bounds
     * of the field.
     * @param location The location from which to generate an adjacency.
     * @return A valid location within the grid area. This may be the
     *         same object as the location parameter, or null if all
     *         locations around are full.
     */
    @Override
    public Location freeAdjacentLocation(Location location) {
        Iterator<Location> adjacent = adjacentLocations(location);
        List<Location> adjs = new ArrayList<>();
        while (adjacent.hasNext()) {
            Location next = adjacent.next();
            if (matrix[next.getRow()][next.getCol()] == null) {
//                return next;
                adjs.add(next);
            }
        }
        if (matrix[location.getRow()][location.getCol()] == null) {
//            return location;
            adjs.add(location);
        }
        if (!adjs.isEmpty()) {
            Collections.shuffle(adjs);
            return adjs.get(0);
        }
        return null;
    }

    /**
     * Detect if specified class is adjacent
     * @param location: the location from which to generate adjacencies.
     * @return boolean.
     */
    @Override
    public <T extends Entity> boolean isNeighbourTo(Location location, Class<T> c) {
        Iterator<Location> it = adjacentLocations(location, 1);
        while(it.hasNext()) {
            Entity e = getObjectAt(it.next());
            if(e != null && e.getClass() == c)
                return true;
        }
        return false;
    }

    @Override
    public int getDimensions() {
        return getDepth();
    }

    /**
     * Return a list of all neighbours of type c from the given location, where
     * a neighbour is an object occupying a location that is adjacent to the current
     * location.
     * @param location: the location from which to find adjacent neighbours
     * @param c: the class of the neighbours to find
     * @param <T> the sought-after subclass of the neighbour superclass
     * @return a list of adjacent neighbours
     */
    @Override
    public <T extends Entity> List<T> getAllNeighbours(Location location, Class<T> c) {
        ArrayList<T> neighbours = new ArrayList<>();
        Iterator<Location> it = adjacentLocations(location, 1);
        while (it.hasNext()) {
            Location adjLoc = it.next();
            Entity e = getObjectAt(adjLoc);
            if (e != null && e.getClass() == c) {
                neighbours.add((T) e);
            }
        }
        return neighbours;
    }

    @Override
    public List<Entity> getAllEntities() {
        List<Entity> es = new ArrayList<>();
        for (int row = 0; row < getDepth(); row++) {
            for (int col = 0; col < getWidth(); col++) {
                if (matrix[row][col] != null) {
                    es.add(matrix[row][col]);
                }
            }
        }
        return es;
    }

    public int getDepth() {
        return depth;
    }

    public int getWidth() {
        return width;
    }

    /**
     * Generate an iterator over a shuffled list of locations within
     * "manhattan distance" w to the given one. The list will not include
     * the location itself.
     * All locations will lie within the grid.
     * The topology of the grid is torus shaped.
     * This means that it is like a chessboard but when a piece goes beyond the
     * bottom row it reappears from the first row and vice versa.
     * Similarly, when it goes beyond the rightmost column it reappears from the
     * leftmost column and vice versa.
     * @param location The location from which to generate adjacencies.
     * @param w The "manhattan" radius of the neighbourhood.
     * @return An iterator over locations adjacent to that given.
     */
    private Iterator<Location> adjacentLocations(Location location, int w) {
        int row = location.getRow();
        int col = location.getCol();
        LinkedList<Location> locations = new LinkedList<Location>();
        for (int roffset = -w; roffset <= w; roffset++) {
            int nextRow = row + roffset;
            for (int coffset = -w; coffset <= w; coffset++) {
                int nextCol = col + coffset;
                if (nextCol < 0)
                    nextCol = nextCol + width;
                if (nextCol >= width)
                    nextCol = nextCol - width;
                if (nextRow < 0)
                    nextRow = nextRow + depth;
                if (nextRow >= depth)
                    nextRow = nextRow - depth;
                locations.add(new Location(nextRow, nextCol));
            }
        }
        return locations.iterator();
    }

    /**
     * Generate an iterator over a shuffled list of locations adjacent
     * to the given one. The list will not include the location itself.
     * All locations will lie within the grid.
     * The topology of the grid is torus shaped.
     * This means that it is like a chessboard but when a piece goes beyond the
     * bottom row it reappears from the first row and vice versa.
     * Similarly when it goes beyond the rightmost column it reappears from the
     * leftmost column and vice versa.
     * @param location The location from which to generate adjacencies.
     * @return An iterator over locations adjacent to that given.
     */
    private Iterator<Location> adjacentLocations(Location location) {
        return adjacentLocations(location, 1);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (int col = 0; col < matrix.length; col++) {
            for (int row = 0; row < matrix[0].length; row++) {
                if (matrix[row][col] != null) {
                    if (matrix[row][col] instanceof Agent) {
                        Agent ag = (Agent) matrix[row][col];
                        sb.append(" " + ag.getStatus().toString() + " ");
                    }
                    else sb.append(" X ");
                }
            }
            sb.append("]");
            sb.append("\n");
            if (col != matrix.length - 1) sb.append("[");
        }
        return sb.toString();
    }

    @Override
    public void registerZone(Set<Location> zoneLocations) {
        quZone.addAll(zoneLocations);
    }

    @Override
    public void deregisterZone(Set<Location> zoneLocations) {
        quZone.removeAll(zoneLocations);
    }

    @Override
    public Set<Location> getZone() {
        return quZone;
    }
}