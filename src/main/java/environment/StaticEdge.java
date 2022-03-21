package environment;

import data.SimData;

import java.util.Random;

public abstract class StaticEdge {
    public abstract Class<? extends StaticEdge> getEdgeType();
    public abstract double getInfectionRate();

    public static StaticEdge generateEdge() {
        Random r = SimData.getRandom();
        double d = r.nextDouble();

        if (d < 0.33) { return new RelativeEdge(); }

        if (d < 0.67) { return new FriendEdge(); }

        return new NoRelationEdge();

    }
}
