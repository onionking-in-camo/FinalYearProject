package policies;

import actors.Agent;
import data.SimData;
import environment.Grid;
import environment.Location;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import models.Infected;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PoliciesStepDefs {

    Grid g;

    @Given("field of {int}, {int}")
    public void field_is(Integer width, Integer height) {
        g = new Grid(height, width);
    }

    @Given("all agents are compliant")
    public void all_agents_are_compliant() {
        SimData.setSocialDistancingCompliance(1.0);
        SimData.setQuarantiningCompliance(1.0);
        for (Agent ag : g.getAllOf(Agent.class)) {
            ag.willDistance(true);
            ag.willQuarantine(true);
        }
    }

    @Given("there is an agent at {int}, {int}")
    public void there_is_an_agent_at(Integer x, Integer y) {
        g.place(new Location(y, x), new Agent(new Location(y, x)));
    }

    @When("agents move")
    public void agents_move() {
        System.out.println(g.toString());
        List<Agent> agents = g.getAllOf(Agent.class);
        for (Agent ag : agents) {
            ag.act(g);
        }
        System.out.println(g.toString());
    }

    @Then("each agent has no neighbours")
    public void each_agent_has_no_neighbours() {
        for (Agent ag : g.getAllOf(Agent.class)) {
            assertFalse(g.isNeighbourTo(ag.getLocation(), Agent.class));
        }
    }

    @And("{string} policy is in effect")
    public void policyIsInEffect(String policy) {
        if (policy.equals("quarantining")) {
            SimData.setQuarantining(true);
        }
        else if (policy.equals("social distancing")) {
            SimData.setSocialDistancing(true);
        }
    }

    @Then("agent is at {int}, {int}")
    public void agentIsAt(int col, int row) {
        assertTrue(g.getObjectAt(col, row) != null);
    }

    @And("all agents are infected")
    public void allAgentsAreInfected() {
        for (Agent ag : g.getAllOf(Agent.class)) {
            ag.setStatus(new Infected());
        }
    }
}
