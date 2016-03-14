package ca.utoronto.ee1778.superfit.service;

import android.content.Context;

import ca.utoronto.ee1778.superfit.DAO.DbUtils;
import ca.utoronto.ee1778.superfit.object.Schedule;

/**
 * Created by liuwyang on 2016-03-11.
 */
public class ScheduleService {

    private Context mContext;
    private DbUtils dbUtils;

    public ScheduleService(Context context) {
        this.mContext = context;
        dbUtils = new DbUtils(mContext);
    }


    public Schedule findSchedule() {

        return dbUtils.getCurrentSchedule();

    }

    public void update(Schedule schedule) {

        dbUtils.updateSchedule(schedule.getId(), schedule.getWeight(), schedule.getRep(), schedule.getSets(), schedule.getActive());
    }

}
