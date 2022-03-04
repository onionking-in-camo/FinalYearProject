package models;

public interface SIR {
    public abstract SIR nextState(SIR this);
    public abstract SIR prevState(SIR this);
}
