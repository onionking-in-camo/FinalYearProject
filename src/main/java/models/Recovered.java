package models;

public class Recovered implements SIR {
    @Override
    public SIR nextState() {
        throw new IllegalStateException("Recovered does not have a next state.");
    }

    @Override
    public SIR prevState() {
        return new Infected();
    }
}