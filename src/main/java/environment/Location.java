package environment;

import actors.Entity;
import data.SimData;

/**
 * Adapted from:
 * @author David J. Barnes and Michael Kolling
 * @version 2002-04-09
 * Adapted by:
 * @author James J Kerr
 * @version 05-12-2021
 */
public class Location {
    private int row;
    private int col;
    private Entity occupant;

    public Location(int row, int col) {
        this.row = row;
        this.col = col;
        occupant = null;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Location) {
            Location other = (Location) obj;
            return row == other.getRow() && col == other.getCol();
        }
        return false;
    }

    @Override
    public String toString() {
        return "[" + row + ", " + col + "]";
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    @Override
    public int hashCode() {
        return (row << 16) + col;
    }

    public void addOccupant(Entity e) {
        occupant = e;
    }

    public Entity getOccupant() {
        return occupant;
    }

    public void setOccupant(Entity e) { occupant = e; }

    public boolean occupied() {
        return occupant != null;
    }

    public static Location getLocationInDirection(Location l, Direction d) {
        int x = l.col;
        int y = l.row;
        switch (d) {
            case NORTH:
                y -= 1;
                break;
            case NORTH_EAST:
                x += 1;
                y -= 1;
                break;
            case EAST:
                x += 1;
                break;
            case SOUTH_EAST:
                x += 1;
                y += 1;
                break;
            case SOUTH:
                y += 1;
                break;
            case SOUTH_WEST:
                x -= 1;
                y += 1;
                break;
            case WEST:
                x -= 1;
                break;
            case NORTH_WEST:
                x -= 1;
                y -= 1;
                break;
        }
        if (x < 0) { x = x + SimData.WIDTH; }
        if (y < 0) { y = y + SimData.DEPTH; }
        if (x >= SimData.WIDTH) { x = x - SimData.WIDTH; }
        if (y >= SimData.DEPTH) { y = y - SimData.DEPTH; }
        return new Location(y, x);
    }
}