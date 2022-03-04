package models;

public class Infected implements SIR {
    @Override
    public SIR nextState() {
        return new Recovered();
    }

    @Override
    public SIR prevState() {
        return new Susceptible();
    }
}