package environment;

import java.util.List;
import java.util.Arrays;
import data.SimData;

public enum Direction {
    NORTH,
    NORTH_EAST,
    EAST,
    SOUTH_EAST,
    SOUTH,
    SOUTH_WEST,
    WEST,
    NORTH_WEST;

    private static final List<Direction> DIRECTION_LIST = Arrays.asList(Direction.values());

    // this should be in Direction?
    public static Direction getDirectionOfLocation(Location from, Location to) {
        int fromX = from.getCol();
        int fromY = from.getRow();
        int toX = to.getCol();
        int toY = to.getRow();
        if (toX < fromX) {
            if (toY < fromY)
                return Direction.NORTH_WEST;
            if (toY > fromY)
                return Direction.SOUTH_WEST;
            return Direction.WEST;
        }
        if (toX > fromX) {
            if (toY < fromY)
                return Direction.NORTH_EAST;
            if (toY > fromY)
                return Direction.SOUTH_EAST;
            return Direction.EAST;
        }
        if (toY < fromY)
            return Direction.NORTH;
        return Direction.SOUTH;
    }

    private Direction turn(Direction d, int dir) {
        int i = DIRECTION_LIST.indexOf(d);
        int j = (i + dir + DIRECTION_LIST.size()) % DIRECTION_LIST.size();
        return DIRECTION_LIST.get(j);
    }

    public static Direction getRandomDirection() {
        return DIRECTION_LIST.get(SimData.getRandom().nextInt(DIRECTION_LIST.size()));
    }

    public Direction turnRight(Direction d) {
        return turn(d, 2);
    }

    public Direction turnLeft(Direction d) {
        return turn(d, -2);
    }
}
