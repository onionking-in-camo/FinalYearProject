package disease;

import actors.Agent;
import data.SimData;
import environment.Grid;
import io.cucumber.java.bs.A;
import models.Infected;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DiseaseSpreadControllerTest {

    Grid f;
    int width = 3;
    int depth = 3;
    Agent infAg;

    @BeforeEach
    void setup() {
        f = new Grid(width, depth);
        infAg = new Agent(null);
        infAg.setStatus(new Infected());
    }

    @Test
    void calculateProbabilityOfInfection() {
        double[] risk = new double[] {0.0, 0.30, 0.51, 0.66, 0.76, 0.83, 0.88, 0.92, 0.94};
        List<Agent> infected = new ArrayList<>();
        SimData.setInfectivity(0.3);

        for (int i = 0; i < risk.length; i++) {
            String strRisk = String.format("%3.2f", risk[i]);
            String strProb = String.format("%3.2f", DiseaseSpreadController.calculateProbabilityOfInfection(infected));
            assertEquals(strRisk, strProb);
            infected.add(infAg);
        }
    }

    @Test
    void calculateCertainInfectionRisk() {
        SimData.setInfectivity(1.0);
        List<Agent> infected = new ArrayList<>();
        infected.add(infAg);
        assertEquals(1.0, DiseaseSpreadController.calculateProbabilityOfInfection(infected));
    }

    @Test
    void calculate0InfectionRisk() {
        SimData.setInfectivity(0);
        assertEquals(0.0, DiseaseSpreadController.calculateProbabilityOfInfection(Arrays.asList(infAg)));
    }

    @Test
    void calculateInfectionWithMask() {
        SimData.setInfectivity(0.3);
        SimData.setMasking(true);
        SimData.setMaskCompliance(1.0);
        SimData.setMaskRiskReduction(0.5);

        double[] risk = new double[] {0.0, 0.15, 0.28, 0.39, 0.48, 0.56, 0.62, 0.68, 0.73};
        List<Agent> infected = new ArrayList<>();

        for (int i = 0; i < risk.length; i++) {
            String strRisk = String.format("%3.2f", risk[i]);
            String strProb = String.format("%3.2f", DiseaseSpreadController.calculateProbabilityOfInfection(infected));
            assertEquals(strRisk, strProb);
            infAg.setMasked(true);
            infected.add(infAg);
        }


    }
}
