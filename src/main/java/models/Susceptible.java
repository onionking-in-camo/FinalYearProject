package models;

public class Susceptible implements SIR {
    @Override
    public SIR nextState() {
        return new Infected();
    }

    @Override
    public SIR prevState() {
        throw new IllegalStateException("Susceptible does not have a previous state.");
    }

    @Override
    public String toString() {
        return "S";
    }
}