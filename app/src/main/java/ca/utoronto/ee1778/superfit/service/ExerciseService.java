package ca.utoronto.ee1778.superfit.service;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import ca.utoronto.ee1778.superfit.DAO.DbUtils;
import ca.utoronto.ee1778.superfit.object.Exercise;
import ca.utoronto.ee1778.superfit.object.Result;

/**
 * Created by liuwyang on 2016-03-07.
 */
public class ExerciseService {

    private Context mContext;
    private DbUtils dbUtils;
    private boolean isFinished;
    private static ExerciseService mTHis;

    private int suc_cnt = 0;
    private int fail_cnt = 0;


    Result preData;
    public int totalPassed;
    public int numOfStartRegionInOut;
    public int numOfEndRegionInOut;
    public double startDegree;
    public double endDegree;
    public double thresholdDegree;
    public int thisRepResult; //0: not finish; 1: yes, passed; -1: failed
    public int numOfReps;

    /**
     * heart rate
     */
    private float hr;

    public float getHr() {
        return hr;
    }

    public void setHr(float hr) {
        this.hr = hr;
    }

    public ExerciseService(Context context) {
        this.mContext = context;
        dbUtils = new DbUtils(mContext);

        startDegree = -90;
        endDegree = 90;
        thresholdDegree = 25;
        totalPassed = 0;
        numOfReps = 15;
    }

    public static ExerciseService newInstance(Context context) {
        if (mTHis == null) {
            mTHis = new ExerciseService(context);
        }
        return mTHis;
    }


    public List<Exercise> getALlDailyRecords() {
        List<Exercise> exercises = null;
        exercises = dbUtils.queryALlExerciseRecords();

        if (exercises == null) {
            return new ArrayList<>(1);
        }
        return exercises;

    }

    public void record(Exercise exercise) {
        if (exercise.getSuccess_times() == 0) {
            exercise.setSuccess_times(suc_cnt);
        }
        if (exercise.getFailed_times() == 0) {
            exercise.setFailed_times(fail_cnt);
        }

        suc_cnt = 0;
        fail_cnt = 0;
        dbUtils.recordDaily(exercise.getDate(),
                exercise.getName(), exercise.getCompletionRate(),
                exercise.getWeight(), exercise.getNumOfSet(), exercise.getRepetition(), exercise.getScheduleId(), exercise.getSuccess_times(), exercise.getFailed_times());
    }

    public boolean tester(Result result) {

        Result curData = result;

        if (preData == null) {
            preData = curData;
            return false;
        }


        if (curData.getDegree() > 0) { //above horizontal
            if (((endDegree - curData.getDegree()) < thresholdDegree) && ((endDegree - preData.getDegree()) > thresholdDegree)) {
                numOfEndRegionInOut++;
            }

            if (endDegree - curData.getDegree() > thresholdDegree && endDegree - preData.getDegree() < thresholdDegree) {
                numOfEndRegionInOut++;
            }
        } else {


            if (((curData.getDegree() - startDegree) > thresholdDegree) && ((preData.getDegree() - startDegree) < thresholdDegree)) {

                numOfStartRegionInOut++;
            }

            if (((curData.getDegree() - startDegree) < thresholdDegree) && ((preData.getDegree() - startDegree) > thresholdDegree)) {
                numOfStartRegionInOut++;
            }

            System.out.println("Ryan:inout:starttime : start = " + numOfStartRegionInOut + "  currentdegree:" + curData.getDegree() + "  preDeg:" + preData.getDegree());
        }


        if (numOfStartRegionInOut == 2) {
            System.out.println("Ryan:inout:current: start" + " " + numOfStartRegionInOut + "  end:" + numOfEndRegionInOut);
            if (numOfStartRegionInOut == numOfEndRegionInOut) {
                thisRepResult = 1;
                totalPassed++;


            } else {
                thisRepResult = -1;
            }

            numOfStartRegionInOut = 0;
            numOfEndRegionInOut = 0;
        }
        System.out.println("Ryan:inout:clear: start" + " " + numOfStartRegionInOut + "  end:" + numOfEndRegionInOut);


        if (totalPassed >= 15) {


            //result.setRecommendWeight(12); //calculate the weight and push into result object


            preData = null;
            totalPassed = 0;
            numOfStartRegionInOut = 0;
            numOfEndRegionInOut = 0;
            thisRepResult = 0;
            return true;
        }

        preData = curData;

        return false;
    }

    public void recommend(Result result) {


    }

    // return the pitch as degree unit from -180 to 180
    public double calculatePitch(double x, double y, double z) {
        // reusltInRadian range from -pi/2 through pi/2
        double epi = 0.00001;
        if (y * y + z * z < epi) return 90;

        double reusltInRadian = Math.atan(x / (y * y + z * z));

        return Math.toDegrees(reusltInRadian);
    }

    public int totalProgress(List<Exercise> exercises) {


        int total = 0;
        int tmp = 0;

        if (exercises.size() == 0) {
            return 0;
        }
        for (Exercise exercise : exercises) {
            tmp = exercise.getCompletionRate();
            total += tmp;
        }

        return total / (exercises.size());

    }

    public List<Exercise> getCurrentExercises(Long scheduleId) {


        List<Exercise> exercises = dbUtils.queryCurrentExerciseRecords(scheduleId);

        if (exercises == null) {
            exercises = new ArrayList<>();

        }
        return exercises;


    }


    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean isFinished) {
        this.isFinished = isFinished;
    }

    public void refresh() {
        isFinished = false;
    }

    public int getSuc_cnt() {
        return suc_cnt;
    }

    public void setSuc_cnt(int suc_cnt) {
        this.suc_cnt = suc_cnt;
    }

    public int getFail_cnt() {
        return fail_cnt;
    }

    public void setFail_cnt(int fail_cnt) {
        this.fail_cnt = fail_cnt;
    }
}
