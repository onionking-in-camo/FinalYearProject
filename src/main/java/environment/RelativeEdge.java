package environment;

public class RelativeEdge extends StaticEdge {
    @Override
    public Class<? extends StaticEdge> getEdgeType() {
        return this.getClass();
    }

    @Override
    public double getInfectionRate() {
        return (1.0 / 16) * 6;
    }
}
