package ca.utoronto.ee1778.superfit.DAO;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import ca.utoronto.ee1778.superfit.common.Constant;

/**
 * Created by liuwyang on 2016-02-10.
 */
public class DbHelper extends SQLiteOpenHelper {

    public static final int VERSION = 1;
    public static final String DB_NAME = "superFit";

    /**
     * TABLE: user
     */
    public static final String TABLE_NAME_USER = "user";
    public static final String TABLE_USER_COL_NAME = "name";
    public static final String TABLE_USER_COL_AGE = "age";
    public static final String TABLE_USER_COL_WEIGHT = "weight";
    public static final String TABLE_USER_COL_ID = "id";

    /**
     * TABLE:daily_check_in
     */
    public static final String TABLE_NAME_DAILY = "daily_check_in";
    public static final String TABLE_DAILY_COL_ID = "id";
    public static final String TABLE_DAILY_COL_EXERCISE = "exercise";
    public static final String TABLE_DAILY_COL_USERID = "userId";
    public static final String TABLE_DAILY_COL_DATE = "date";
    public static final String TABLE_DAILY_COL_COMRATE = "completion_rate";
    public static final String TABLE_DAILY_COL_WEIGHT = "weight";
    public static final String TABLE_DAILY_COL_REP = "repetition";
    public static final String TABLE_DAILY_COL_SET = "num_of_set";
    public static final String TABLE_DAILY_COL_SCHDID = "schedule_id";
    public static final String TABLE_DAILY_COL_FAILED = "failed_times";
    public static final String TABLE_DAILY_COL_SUCCESS = "success_times";


    public static final String TABLE_NAME_EXERCISE = "exercise";
    public static final String TABLE_NAME_SCHEDULE = "schedule";

    public static final String TABLE_SCHEDULE_COL_ID ="id";
    public static final String TABLE_SCHEDULE_COL_USER_ID="user_id";
    public static final String TABLE_SCHEDULE_COL_EXERCISE="exercise";
    public static final String TABLE_SCHEDULE_COL_REP = "repetition";
    public static final String TABLE_SCHEDULE_COL_NUM_OF_SET="num_of_set";
    public static final String TABLE_SCHEDULE_COL_WEIGHT="weight";
    public static final String TABLE_SCHEDULE_COL_ACTIVE="active";

    public DbHelper(Context context) {
        super(context, DB_NAME, null, VERSION);


    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String sql_user = "CREATE TABLE if not exists `" + TABLE_NAME_USER + "` (" +
                "`" + TABLE_USER_COL_ID + "`INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                "`" + TABLE_USER_COL_NAME + "`TEXT NOT NULL," +
                "`" + TABLE_USER_COL_AGE + "`INTEGER NOT NULL," +
                "`" + TABLE_USER_COL_WEIGHT + "`NUMERIC NOT NULL" +
                ")";
        String sql_checkin = "CREATE TABLE if not exists `" + TABLE_NAME_DAILY + "` (" +
                "`" + TABLE_DAILY_COL_ID + "`INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE," +
                "`" + TABLE_DAILY_COL_EXERCISE + "`TEXT NOT NULL," +
                "`" + TABLE_DAILY_COL_USERID + "`INTEGER NOT NULL," +
                "`" + TABLE_DAILY_COL_DATE + "`TEXT NOT NULL," +
                "`" + TABLE_DAILY_COL_COMRATE + "`TEXT NOT NULL," +
                "`" + TABLE_DAILY_COL_WEIGHT + "`TEXT NOT NULL," +
                "`" + TABLE_DAILY_COL_SCHDID + "`TEXT NOT NULL," +
                "`" + TABLE_DAILY_COL_REP + "`TEXT NOT NULL," +
                "`" + TABLE_DAILY_COL_FAILED + "`TEXT NOT NULL," +
                "`" + TABLE_DAILY_COL_SUCCESS + "`TEXT NOT NULL," +
                "`" + TABLE_DAILY_COL_SET + "`TEXT NOT NULL" +
                ")";
        String sql_schedule = "CREATE TABLE if not exists`" + TABLE_NAME_SCHEDULE + "` (" +
                "`"+TABLE_SCHEDULE_COL_ID+"`INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE," +
                "`"+TABLE_SCHEDULE_COL_USER_ID+"`INTEGER NOT NULL," +
                "`"+TABLE_SCHEDULE_COL_EXERCISE+"`TEXT NOT NULL," +
                "`"+TABLE_SCHEDULE_COL_REP+"`INTEGER NOT NULL," +
                "`"+TABLE_SCHEDULE_COL_NUM_OF_SET+"`INTEGER NOT NULL," +
                "`"+TABLE_SCHEDULE_COL_WEIGHT+"`TEXT NOT NULL," +
                "`"+TABLE_SCHEDULE_COL_ACTIVE+"`INTEGER NOT NULL" +
                ")";
        String sql_exercise = "CREATE TABLE if not exists`" + TABLE_NAME_EXERCISE + "` (" +
                "`id`INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE," +
                "`name`TEXT NOT NULL" +
                ")";
        db.execSQL(sql_user);
        db.execSQL(sql_checkin);
        db.execSQL(sql_exercise);
        db.execSQL(sql_schedule);

        String sql_data = "INSERT INTO `schedule`(`exercise`,`user_id`,`repetition`,`num_of_set`,`weight`,`active`) VALUES ('"+ Constant.EXERCISE_NAME+"','1','"+Constant.RECOMMEND_REPS+"','"+Constant.RECOMMEND_SETS+"','32.5','1')";
        db.execSQL(sql_data);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        String sql_user = "DROP TABLE IF EXISTS " + TABLE_NAME_USER;
        String sql_exercise = "DROP TABLE IF EXISTS " + TABLE_NAME_EXERCISE;
        String sql_checkin = "DROP TABLE IF EXISTS " + TABLE_NAME_DAILY;
        String sql_schedule = "DROP TABLE IF EXISTS " + TABLE_NAME_SCHEDULE;

        db.execSQL(sql_user);
        db.execSQL(sql_checkin);
        db.execSQL(sql_exercise);
        db.execSQL(sql_schedule);
        this.onCreate(db);

    }
}
