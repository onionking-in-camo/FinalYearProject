package data;

import environment.Field;
import environment.FieldType;
import environment.Grid;
import environment.MobileNetwork;
import obsolete.Network;

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

    private static int DEPTH = 20;
    public static int getDepth() {
        return DEPTH;
    }
    public static void setDepth(int depth) {
        if (depth < 1) { throw new IllegalArgumentException(); }
        DEPTH = depth;
    }

    private static int WIDTH = 20;
    public static int getWidth() {
        return WIDTH;
    }
    public static void setWidth(int width) {
        if (width < 1) { throw new IllegalArgumentException(); }
        WIDTH = width;
    }

    private static int SEED = 999;
    public static int getSeed() {
        return SEED;
    }
    public static void setSeed(int seed) {
        SEED = seed;
    }

    private static int RUN_DELAY = 100;
    public static int getRunDelay() {
        return RUN_DELAY;
    }
    public static void setRunDelay(int runDelay) {
        if (runDelay < 1 || runDelay > 1000) {
            throw new IllegalArgumentException("Run Delay must be between 0 and 1000"); }
        RUN_DELAY = runDelay;
    }

    private static int RUNTIME = 100;
    public static int getRuntime() {
        return RUNTIME;
    }
    public static void setRuntime(int runtime) {
        if (runtime < 1) { throw new IllegalArgumentException(); }
        RUNTIME = runtime;
    }

    private static double AGENT_PROB = 0.05;
    public static double getAgentProbability() {
        return AGENT_PROB;
    }
    public static void setAgentProbability(double prob) {
        if (prob < 0 || prob > 1.0)
            throw new IllegalArgumentException("Agent probability must be between 0 and 1.0");
        AGENT_PROB = prob;
    }

    // default probability that an Infected Agent will be generated
    private static double AGENT_ZERO_PROB = 0.01;
    public static double getAgentZeroProbability() {
        return AGENT_ZERO_PROB;
    }
    public static void setAgentZeroProbability(double prob) {
        if (prob < 0 || prob > 1.0)
            throw new IllegalArgumentException("Agent Zero probability must be between 0 and 1.0");
        AGENT_ZERO_PROB = prob;
    }

    // default flags for policies
    private static boolean SOCIAL_DISTANCING = false;
    public static boolean getSocialDistancing() {
        return SOCIAL_DISTANCING;
    }
    public static void setSocialDistancing(boolean flag) {
        SOCIAL_DISTANCING = flag;
    }

    private static boolean MASK_MANDATE = false;
    public static boolean getMasking() {
        return MASK_MANDATE;
    }
    public static void setMasking(boolean flag) {
        MASK_MANDATE = flag;
    }

    private static boolean QUARANTINING = false;
    public static boolean getQuarantining() {
        return QUARANTINING;
    }
    public static void setQuarantining(boolean flag) {
        QUARANTINING = flag;
    }

    private static double SOCIAL_DISTANCING_COMPLIANCE = 0.5;
    public static double getSocialDistancingCompliance() {
        return SOCIAL_DISTANCING_COMPLIANCE;
    }
    public static void setSocialDistancingCompliance(double compliance) {
        if (compliance < 0 || compliance > 1)
            throw new IllegalArgumentException("Social distancing compliance must be between 0 and 1 inclusive.");
        SOCIAL_DISTANCING_COMPLIANCE = compliance;
    }

    private static double MASK_COMPLIANCE = 0.9;
    public static double getMaskCompliance() {
        return MASK_COMPLIANCE;
    }
    public static void setMaskCompliance(double compliance) {
        if (compliance < 0 || compliance > 1)
            throw new IllegalArgumentException("Mask compliance must be between 0 and 1 inclusive.");
        MASK_COMPLIANCE = compliance;
    }

    private static double SELF_QUARANTINE_COMPLIANCE = 0.8;
    public static double getQuarantineCompliance() {
        return SELF_QUARANTINE_COMPLIANCE;
    }
    public static void setQuarantiningCompliance(double compliance) {
        if (compliance < 0 || compliance > 1)
            throw new IllegalArgumentException("Quarantining compliance must be between 0 and 1 inclusive.");
        SELF_QUARANTINE_COMPLIANCE = compliance;
    }

    // default reduction that mask-wearing provides
    private static double MASK_WEARING_REDUCTION = 0.8;
    public static double getMaskRiskReduction() {
        return MASK_WEARING_REDUCTION;
    }
    public static void setMaskRiskReduction(double reduction) {
        if (reduction < 0 || reduction > 1)
            throw new IllegalArgumentException("Mask reduction must be between 0 and 1 inclusive.");
        MASK_WEARING_REDUCTION = reduction;
    }

    private static double SYMPTOMATIC = 0.33;
    public static double getSymptomaticProbability() {
        return SYMPTOMATIC;
    }
    public static void setSymptomaticProbability(double prob) {
        if (prob < 0 || prob > 1)
            throw new IllegalArgumentException();
        SYMPTOMATIC = prob;
    }

    private static double INFECTIVITY = 0.33;
    public static double getInfectivity() {
        return INFECTIVITY;
    }
    public static void setInfectivity(double inf) {
        if (inf < 0 || inf > 1.0)
            throw new IllegalArgumentException();
        INFECTIVITY = inf;
    }

    private static int INFECTIOUS_PERIOD_MIN = 14;
    public static int getInfectiousPeriodMin() {
        return INFECTIOUS_PERIOD_MIN;
    }
    public static void setInfectiousPeriodMin(int period) {
        if (period < 1)
            throw new IllegalArgumentException();
        if (period > INFECTIOUS_PERIOD_MAX)
            throw new IllegalArgumentException("Infectious Min must be less than Infectious Max");
        INFECTIOUS_PERIOD_MIN = period;
    }

    private static int INFECTIOUS_PERIOD_MAX = 42;
    public static int getInfectiousPeriodMax() {
        return INFECTIOUS_PERIOD_MAX;
    }
    public static void setInfectiousPeriodMax(int period) {
        if (period < 1)
            throw new IllegalArgumentException();
        if (period < INFECTIOUS_PERIOD_MIN)
            throw new IllegalArgumentException("Infectious Max must be greater than Infectious Min");
        INFECTIOUS_PERIOD_MAX = period;
    }

    private static Class<?> FIELD_CLASS = Grid.class;
    public static Class<?> getFieldClass() {
        return FIELD_CLASS;
    }
    public static void setFieldClass(Class<?> fieldClass) {
        if (fieldClass != Grid.class && fieldClass != MobileNetwork.class) {
            throw new IllegalArgumentException(fieldClass.toString());
        }
        FIELD_CLASS =  fieldClass;
    }

    private static FieldType FIELD_TYPE = FieldType.GRID;
    public static FieldType getFieldType() {
        return FIELD_TYPE;
    }
    public static void setFieldType(FieldType fieldType) {
        FIELD_TYPE = fieldType;
    }

    public static String DATA_FILE_DIR = "./src/main/resources/";
    public static String DATA_FILE_PATH = "simulation_record_default";
}
