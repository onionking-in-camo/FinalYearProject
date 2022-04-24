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

    public Entity getOccupant() {
        return occupant;
    }

    public void setOccupant(Entity e) { occupant = e; }

    public boolean occupied() {
        return occupant != null;
    }
}