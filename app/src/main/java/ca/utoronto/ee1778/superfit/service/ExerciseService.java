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

    private static ExerciseService mTHis;



    public ExerciseService(Context context) {
        this.mContext = context;
        dbUtils = new DbUtils(mContext);
    }

    public static ExerciseService newInstance(Context context){
        if(mTHis==null){
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
        double rt = calculatePitch(result.getX(), result.getY(), result.getZ());
        if (rt > 0 && rt < 90) {

            return true;
        } else {
            return false;
        }
    }


    public void recommend(Result result){



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

        if(exercises.size()==0){
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

        if(exercises==null){
            exercises = new ArrayList<>();

        }
        return exercises;


    }
}
