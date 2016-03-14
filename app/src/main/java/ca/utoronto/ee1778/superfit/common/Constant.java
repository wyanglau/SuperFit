package ca.utoronto.ee1778.superfit.common;

/**
 * Created by liuwyang on 2016-03-07.
 */
public class Constant {
    public final static String EXTRAS_TAG_USER = "user";
    public final static String EXTRAS_TAG_EXERCISES = "exercises";
    public final static String EXTRAS_TAG_TRACKER = "tracker";


    /**
     * test_mode: 0 for exercies test
     * 1 for daily checkin
     */
    public final static String EXTRAS_TAG_TEST_MODE = "test_mode";
    public final static int MODE_TEST = 0;
    public final static int MODE_CHECK_IN = 1;

    public final static int REQUIRED_TEST_TIME = 15;


    public final static String TAG_CONFIRM = "Confirm";
    public final static String TAG_CONTINUE = "Continue";


    /**
     * currently, we used only 1 types of exercise to trace the progress
     */
    public final static int RECOMMEND_SETS = 5;
    public final static int RECOMMEND_REPS = 12;
    public final static  String EXERCISE_NAME="Dumbbell Bicep Curl";
}
