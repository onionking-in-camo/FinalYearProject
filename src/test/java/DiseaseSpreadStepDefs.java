import actors.Agent;
import data.SimData;
import disease.DiseaseSpreadCalculator;
import environment.Grid;
import environment.Location;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import models.Infected;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DiseaseSpreadStepDefs {

    Grid g;
    List<Agent> contacts = new ArrayList<>();
    List<Agent> infected = new ArrayList<>();

    @Given("field is {int}, {int}")
    public void field_is(Integer width, Integer depth) {
        g = new Grid(width, depth);
    }

    @Given("field is filled with Infected")
    public void field_is_filled_with_infected() {
        for (int col = 0; col < g.getWidth(); col++) {
            for (int row = 0; row < g.getDepth(); row++) {
                Location loc = new Location(row, col);
                Agent ag = new Agent(loc);
                ag.setStatus(new Infected());
                g.place(loc, ag);
            }
        }
    }

    @When("check for contacts")
    public void check_for_contacts_with_infected() {
        contacts = DiseaseSpreadCalculator.getInfectedContacts(g.getAllOf(Agent.class), g);
    }

    @Then("contacts size should be {int}")
    public void contacts_size_should_be(Integer size) {
        assertEquals(size, contacts.size());
    }

    @Given("agent at {int}, {int} is Susceptible")
    public void agent_at_is_susceptible(Integer col, Integer row) {
        g.place(new Location(row, col), new Agent(new Location(row, col)));
    }

    @When("check for newly infected")
    public void check_for_newly_infected() {
        contacts = DiseaseSpreadCalculator.getInfectedContacts(g.getAllOf(Agent.class), g);
        infected = DiseaseSpreadCalculator.getNewlyInfected(contacts, g);
        System.out.println(g.toString());
    }

    @Given("chance of infection is {double}")
    public void chance_of_infection_is(Double infRate) {
        SimData.INFECTIVITY = infRate;
    }

    @Then("newly infected should equal {int}")
    public void newly_infected_should_equal(Integer size) {
        assertEquals(size, infected.size());
    }

    @Given("field is filled with Susceptible")
    public void field_is_filled_with_susceptible() {
        for (int col = 0; col < g.getWidth(); col++) {
            for (int row = 0; row < g.getDepth(); row++) {
                Location loc = new Location(row, col);
                Agent ag = new Agent(loc);
                g.place(loc, ag);
            }
        }
    }

    @Given("agent at {int}, {int} is Infected")
    public void agent_at_is_infected(Integer col, Integer row) {
        Agent ag = new Agent(new Location(row, col));
        ag.setStatus(new Infected());
        g.place(new Location(row, col), ag);
    }
}
