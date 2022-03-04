package environment;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import actors.Entity;

/**
 * @author David J. Barnes and Michael Kolling
 * @author Maria Chli
 * @author James J Kerr
 * @version 07-12-2021
 */
public class Grid {

    private int depth;
    private int width;
    private Entity[][] field;

    /**
     * Create field grid where depth = number of rows,
     * and width = number of columns
     * @param depth
     * @param width
     */
    public Grid(int depth, int width) {
        this.depth = depth;
        this.width = width;
        field = new Entity[depth][width];
    }

    public void clear() {
        for (int row = 0; row < depth; row++) {
            for (int col = 0; col < width; col++) {
                field[row][col] = null;
            }
        }
    }

    /**
     * Place a person at the given location.
     * If there is already a person at the location it will
     * be lost.
     * @param entity: the person to be placed.
     * @param row: row coordinate of the location.
     * @param col: column coordinate of the location.
     */
    public void place(Entity entity, int row, int col) {
        place(entity, new Location(row, col));
    }

    /**
     * Place a person at the given location.
     * If there is already a person at the location it will
     * be lost.
     * @param entity: the person to be placed.
     * @param location: where to place the person.
     */
    public void place(Entity entity, Location location) {
        field[location.getRow()][location.getCol()] = entity;
    }

    /**
     * Clear the given location.
     * If there is a person at the location it will
     * be lost.
     * @param location The location to be cleared.
     */
    public void clearLocation(Location location) {
        field[location.getRow()][location.getCol()] = null;
    }

    /**
     * Return the person at the given location, if any.
     * @param location Where in the field.
     * @return The person at the given location, or null if there is none.
     */
    public Entity getObjectAt(Location location) {
        return getObjectAt(location.getRow(), location.getCol());
    }

    /**
     * Return the person at the given location, if any.
     * @param row The desired row.
     * @param col The desired column.
     * @return The person at the given location, or null if there is none.
     */
    public Entity getObjectAt(int row, int col) {
        return field[row][col];
    }

    /**
     * Return all free locations that are adjacent to the
     * given location. If there is none, then return an empty list.
     * @param location: the location from which to generate an adjacency.
     * @return A list of valid location within the grid area. This may be
     *         the empty list if all locations around are full.
     */
    public ArrayList<Location> getAllFreeAdjacentLocations(Location location) {
        Iterator<Location> adjacent = adjacentLocations(location);
        ArrayList<Location> freeLocations = new ArrayList<Location>();
        while(adjacent.hasNext()) {
            Location next = (Location) adjacent.next();
            if(field[next.getRow()][next.getCol()] == null) {
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
    public Location freeAdjacentLocation(Location location) {
        Iterator<Location> adjacent = adjacentLocations(location);
        while (adjacent.hasNext()) {
            Location next = adjacent.next();
            if (field[next.getRow()][next.getCol()] == null) {
                return next;
            }
        }
        if (field[location.getRow()][location.getCol()] == null) {
            return location;
        }
        return null;
    }

    public Grid cloneField() {
        Grid clone = new Grid(getDepth(), getWidth());
        for (int row = 0; row < getDepth(); row++) {
            for (int col = 0; col < getWidth(); col++) {
                clone.field[row][col] = field[row][col];
            }
        }
        return clone;
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

    /**
     * Detect if specified class is adjacent
     * @param location: the location from which to generate adjacencies.
     * @return boolean.
     */
    public <T> boolean isNeighbourTo(Location location, Class<T> c) {
        Iterator<Location> it = adjacentLocations(location, 1);
        while(it.hasNext()) {
            Entity e = getObjectAt(it.next());
            if(e != null && e.getClass() == c)
                return true;
        }
        return false;
    }

    public <T> Location getNeighbour(Location loc, Class<T> c) {
        Iterator<Location> it = adjacentLocations(loc, 1);
        while(it.hasNext()) {
            Location adjLoc = it.next();
            Entity e = getObjectAt(adjLoc);
            if(e != null && e.getClass() == c)
                return adjLoc;
        }
        return null;
    }

    public <T extends Entity> List<Entity> getAllNeighbours(Location location, Class<T> c) {
        ArrayList<Entity> neighbours = new ArrayList<>();
        Iterator<Location> it = adjacentLocations(location, 1);
        while (it.hasNext()) {
            Location adjLoc = it.next();
            Entity e = getObjectAt(adjLoc);
            if (e != null && e.getClass() == c) {
                neighbours.add(e);
            }
        }
        return neighbours;
    }

    /**
     * Generate an iterator over a shuffled list of locations within
     * "manhattan distance" w to the given one. The list will not include the location itself.
     * All locations will lie within the grid.
     * The topology of the grid is torus shaped.
     * This means that it is like a chessboard but when a piece goes beyond the
     * bottom row it reappears from the first row and vice versa.
     * Similarly when it goes beyond the rightmost column it reappears from the
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

    public boolean pathObstructed(Location l, Direction d) {
        Location path = Location.getLocationInDirection(l, d);
        return getObjectAt(path) != null;
    }

    public int getDepth() {
        return depth;
    }

    public int getWidth() {
        return width;
    }
}