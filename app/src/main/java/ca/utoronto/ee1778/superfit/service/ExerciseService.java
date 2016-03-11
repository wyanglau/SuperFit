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

    public ExerciseService(Context context) {
        this.mContext = context;
        dbUtils = new DbUtils(mContext);
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
        return false;
    }
    // return the pitch as degree unit from -180 to 180
    public  double calculatePitch(double x, double y, double z){
        // reusltInRadian range from -pi/2 through pi/2
        double epi = 0.00001;
        if (y*y + z*z < epi) return 90;

        double reusltInRadian = Math.atan(x/(y*y + z*z));

        return Math.toDegrees(reusltInRadian);
    }
}
