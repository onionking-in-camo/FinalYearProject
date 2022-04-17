package models;

public interface SIR {
    public SIR nextState(SIR this);
    public SIR prevState(SIR this);
    public String toString();
}
