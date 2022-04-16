package data;

import environment.Grid;

import java.awt.Color;
import java.util.Random;

public class SimData {

    private static Random RANDOM;

    /**
     * Singleton instance of Random.
     * @return the singleton instance of the Random class
     */
    public static Random getRandom() {
        if (RANDOM == null) {
            RANDOM = new Random(SEED);
        }
        return RANDOM;
    }

    // default dimensions for the simulation model
    public static int DEPTH = 40;
    public static int WIDTH = 40;
    // default seed for the simulation model
    public static int SEED = 999;
    // default run speed
    public static int RUN_DELAY = 100;
    // default runtime
    public static int RUNTIME = 100;
    // default probability that an Agent will be generated
    public static double AGENT_PROB = 0.05;
    // default probability that an Infected Agent will be generated
    public static double AGENT_ZERO_PROB = 0.01;
    // default flags for policies
    public static boolean SOCIAL_DISTANCING = false;
    public static boolean MASK_MANDATE = false;
    public static boolean QUARANTINING = false;
    // the likelihood that Entities will comply to the policies
    public static double SOCIAL_DISTANCING_COMPLIANCE = 0.5;
    public static double MASK_COMPLIANCE = 0.9;
    public static double SELF_QUARANTINE_COMPLIANCE = 0.8;
    // default reduction that mask-wearing provides
    public static double MASK_WEARING_REDUCTION = 0.8;
    // default likelihood that an Infected entity will show symptoms
    public static double SYMPTOMATIC = 0.33;
    // default probability that infection spreads given contact between relevant entities
    public static double INFECTIVITY = 0.33;
    // min and max for sickness duration
    public static int INFECTIOUS_PERIOD_MIN = 14;
    public static int INFECTIOUS_PERIOD_MAX = 42;
    // default field type
    public static Class<?> FIELD_TYPE = Grid.class;
    //
    public static String DATA_FILE_DIR = "./src/main/resources/";
    public static String DATA_FILE_PATH = "simulation_record_default";
}
