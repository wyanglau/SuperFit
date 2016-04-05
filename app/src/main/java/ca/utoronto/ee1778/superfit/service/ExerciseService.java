package ca.utoronto.ee1778.superfit.service;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ca.utoronto.ee1778.superfit.DAO.DbUtils;
import ca.utoronto.ee1778.superfit.common.Constant;
import ca.utoronto.ee1778.superfit.object.Exercise;
import ca.utoronto.ee1778.superfit.object.Result;
import ca.utoronto.ee1778.superfit.object.Schedule;

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
    public Result preData;
    public int totalPassed;
    public int numOfStartRegionInOut;
    public int numOfEndRegionInOut;
    public double startDegree;
    public double endDegree;
    public double startThresholdDegree;
    public double endThresholdDegree;
    public int thisRepResult; //0: not finish; 1: yes, passed; -1: failed
    public int numOfReps;

    private Info _info;
    private double nextWeight;

    private Schedule tobeScheduled;
    public int mode = Constant.MODE_CHECK_IN;


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

    public void setNextWeight(double nextWeight) {
        this.nextWeight = nextWeight;
    }

    public double getNextWeight() {
        return this.nextWeight;
    }

    public ExerciseService(Context context) {
        this.mContext = context;

        dbUtils = new DbUtils(mContext);

        startDegree = -90;
        endDegree = 90;
        startThresholdDegree = 20;
        endThresholdDegree = 50;
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

        dbUtils.recordDaily(exercise.getDate(),
                exercise.getName(), exercise.getCompletionRate(),
                exercise.getWeight(), exercise.getNumOfSet(), exercise.getRepetition(), exercise.getScheduleId(), exercise.getSuccess_times(), exercise.getFailed_times());
    }

    public boolean tester(Result result) {

        Log.d("ExerciseService", "entry:tester:recommended weight:" + nextWeight + "  " + " heartRate:" + getHr() + "  " + result.toString());

        Result curData = result;


        if (preData == null && curData.getDegree() != null) {
            preData = curData;
            return false;
        } else if (preData == null && curData.getDegree() == null) {
            return false;
        }


        if (curData.getDegree() > 0) { //above horizontal
            if (((endDegree - curData.getDegree()) < endThresholdDegree) && ((endDegree - preData.getDegree()) > endThresholdDegree)) {
                numOfEndRegionInOut++;
            }

            if (endDegree - curData.getDegree() > endThresholdDegree && endDegree - preData.getDegree() < endThresholdDegree) {
                numOfEndRegionInOut++;
            }
        } else {


            if (((curData.getDegree() - startDegree) > startThresholdDegree) && ((preData.getDegree() - startDegree) < startThresholdDegree)) {

                numOfStartRegionInOut++;
            }

            if (((curData.getDegree() - startDegree) < startThresholdDegree) && ((preData.getDegree() - startDegree) > startThresholdDegree)) {

                if (numOfStartRegionInOut == 1) {
                    numOfStartRegionInOut++;
                }
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

        Log.d("ExerciseService", "tester:exit:preData" + preData.toString());
        if ((totalPassed >= Constant.REQUIRED_TEST_TIME) && (mode == Constant.MODE_TEST)) {


            Log.d("ExerciseService", "final HR:" + getHr());
            if (_info == null) {
                _info = new Info(getNextWeight(), getHr(), curData.getAge());
            } else {
                _info.setCurWeight(getNextWeight());
            }

            double recommeded = _info.calculateWeight();
            setNextWeight(recommeded);
            preData = null;
            totalPassed = 0;
            numOfStartRegionInOut = 0;
            numOfEndRegionInOut = 0;
            thisRepResult = 0;
            isFinished = true;
            Log.d("ExerciseService", "tester:exit:result:" + result.toString());
            Log.d("ExerciseService", "tester:exit:recommend:" + recommeded);
            if (_info.getIsFinish()) {
                result.setRecommendWeight(recommeded); //calculate the weight and push into result object
                return true;
            }
            result.setRecommendWeight(recommeded);
            return false;
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


        preData = null;
        totalPassed = 0;
        numOfStartRegionInOut = 0;
        numOfEndRegionInOut = 0;
        thisRepResult = 0;

        suc_cnt = 0;
        fail_cnt = 0;

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

    private class Info {
        double _preWeight;
        double _curWeight;
        double _hr;
        double _age;
        boolean _isFinish;

        Info(double curWeight, double hr, double age) {
            _curWeight = curWeight;
            _hr = hr;
            _age = age;
            _preWeight = 0;
            _isFinish = false;
        }

        boolean getIsFinish() {
            return this._isFinish;
        }

        void setCurWeight(double newCurWeight) {
            _curWeight = newCurWeight;
        }

        void setHr(double newHr) {
            _hr = newHr;
        }

        void clear() {
            _preWeight = 0;
            _hr = 0;
            _isFinish = false;
        }

        double calculateWeight() {

            double mhr = _hr / (220 - _age);
            double epsi = 0.000000001;

            if (mhr - 0.625 > epsi && mhr - 0.675 < epsi) {
                _preWeight = _curWeight;
                _isFinish = true;
                return _curWeight * 1.5;
            } else if (mhr < 0.625) {
                if (_preWeight > _curWeight) {
                    _isFinish = true;
                    return (_preWeight + _curWeight) * 1.5 / 2;
                }

                _preWeight = _curWeight;
                return _curWeight + 5;
            }

            if (_preWeight < _curWeight && (_curWeight - _preWeight) < 6.0) {
                _isFinish = true;
                return (_preWeight + _curWeight) * 1.5 / 2;
            }

            _preWeight = _curWeight;
            return _curWeight - 2.5;
        }

    }


    public void createSchedule(Schedule schedule) {
        Schedule currentSchedule = dbUtils.getCurrentSchedule();
        dbUtils.updateSchedule(currentSchedule.getId(), currentSchedule.getWeight(), currentSchedule.getRep(), currentSchedule.getSets(), 0);
        dbUtils.createSchedule(schedule.getWeight(), schedule.getRep(), schedule.getSets(), schedule.getUserId(), schedule.getExercise());

    }

    public Schedule getTobeScheduled() {
        return tobeScheduled;
    }

    public void setTobeScheduled(Schedule tobeScheduled) {
        this.tobeScheduled = tobeScheduled;
    }
}