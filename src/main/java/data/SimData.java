package data;

import java.awt.Color;
import java.util.Random;

public class SimData {

    private static Random RANDOM;

    public static Random getRandom() {
        if (RANDOM == null) {
            RANDOM = new Random(SEED);
        }
        return RANDOM;
    }

    public static int DEPTH = 100;
    public static int WIDTH = 100;
    public static int SEED = 9;

    public static Color SUSCEPTIBLE_COLOR = Color.BLUE;
    public static Color INFECTED_COLOR = Color.RED;
    public static Color RECOVERED_COLOR = Color.GREEN;

    public static int RUNTIME = 100;

    public static double AGENT_PROB = 0.5;
    public static double AGENT_ZERO_PROB = 0.025;

    public static boolean SOCIAL_DISTANCING = false;
    public static boolean MASK_MANDATE = false;
    public static boolean QUARANTINING = false;

    public static double SOCIAL_DISTANCING_COMPLIANCE = 0.5;
    public static double MASK_COMPLIANCE = 0.9;
    public static double MASK_WEARING_REDUCTION = 0.8;
    public static double SELF_QUARANTINE_COMPLIANCE = 0.8;
    public static double SYMPTOMATIC = 0.33;

    public static double INFECTIVITY = 0.25;
    public static int INFECTIOUS_PERIOD_MIN = 14;
    public static int INFECTIOUS_PERIOD_MAX = 42;

}
