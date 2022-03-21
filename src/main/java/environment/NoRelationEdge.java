package environment;

public class NoRelationEdge extends StaticEdge {
    @Override
    public Class<? extends StaticEdge> getEdgeType() {
        return this.getClass();
    }
    @Override
    public double getInfectionRate() {
        return 0;
    }
}
