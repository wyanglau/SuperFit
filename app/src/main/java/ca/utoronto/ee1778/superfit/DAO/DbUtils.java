package ca.utoronto.ee1778.superfit.DAO;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import ca.utoronto.ee1778.superfit.object.Exercise;
import ca.utoronto.ee1778.superfit.object.User;

/**
 * Created by liuwyang on 2016-02-10.
 */
public class DbUtils {

    private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
    private final Lock r = rwl.readLock();
    private final Lock w = rwl.writeLock();


    private Context mContext;
    private DbHelper dbHelper;
    private SQLiteDatabase db;
    private final static String LOG_TAG = "DbUtils.class";

    public DbUtils(Context context) {
        mContext = context;
        dbHelper = new DbHelper(context);
    }


    public void createUser(String name, String age, String weight) {
        try {
            w.lock();
            db = dbHelper.getWritableDatabase();
            db.beginTransaction();

            String sql = "INSERT INTO `" + DbHelper.TABLE_NAME_USER + "`(`name`,`age`,`weight`) VALUES ('" + name + "','" + age + "','" + weight + "')";
            db.execSQL(sql);
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.e(LOG_TAG, e.getMessage());
        } finally {
            w.unlock();
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        }
    }

    //    public void deleteAll() {
//
//        try {
//            w.lock();
//            db = dbHelper.getWritableDatabase();
//            db.beginTransaction();
//
//            String delAllSql = "DELETE FROM " + DbHelper.TABLE_NAME + ";";
//            String refreshSql = "UPDATE sqlite_sequence set seq=0 where name='" + DbHelper.TABLE_NAME + "'";
//            db.execSQL(delAllSql);
//            db.execSQL(refreshSql);
//            db.setTransactionSuccessful();
//        } catch (Exception e) {
//            Log.e(LOG_TAG, e.getMessage());
//        } finally {
//            w.unlock();
//            if (db != null) {
//                db.endTransaction();
//                db.close();
//            }
//        }
//
//    }
//
    public List<User> getAllUser() {
        List<User> users = null;
        Cursor cursor = null;
        r.lock();
        try {
            db = dbHelper.getReadableDatabase();

            cursor = db.query(DbHelper.TABLE_NAME_USER,
                    new String[]{DbHelper.TABLE_USER_COL_ID, DbHelper.TABLE_USER_COL_NAME,
                            DbHelper.TABLE_USER_COL_AGE, DbHelper.TABLE_USER_COL_WEIGHT},
                    null, null, null, null, null);

            if (cursor.getCount() > 0) {
                users = new ArrayList<>(cursor.getCount());
                while (cursor.moveToNext()) {
                    User user = new User(
                            cursor.getString(cursor.getColumnIndex(DbHelper.TABLE_USER_COL_NAME)),
                            cursor.getString(cursor.getColumnIndex(DbHelper.TABLE_USER_COL_AGE)),
                            cursor.getString(cursor.getColumnIndex(DbHelper.TABLE_USER_COL_WEIGHT)),
                            cursor.getLong(cursor.getColumnIndex(DbHelper.TABLE_USER_COL_ID)));
                    users.add(user);
                }
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        } finally {

            r.unlock();
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }

            if (users == null) {
                return new ArrayList<>(1);
            } else {
                return users;
            }
        }
    }

    public List<Exercise> queryALlExerciseRecords() {
        List<Exercise> exercises = null;
        Cursor cursor = null;
        r.lock();
        try {
            db = dbHelper.getReadableDatabase();

            cursor = db.query(DbHelper.TABLE_NAME_DAILY,
                    new String[]{DbHelper.TABLE_DAILY_COL_EXERCISE, DbHelper.TABLE_DAILY_COL_COMRATE,
                            DbHelper.TABLE_DAILY_COL_DATE, DbHelper.TABLE_DAILY_COL_WEIGHT, DbHelper.TABLE_DAILY_COL_REP, DbHelper.TABLE_DAILY_COL_SET},
                    null, null, null, null, DbHelper.TABLE_DAILY_COL_DATE+" DESC");

            if (cursor.getCount() > 0) {
                exercises = new ArrayList<>(cursor.getCount());
                while (cursor.moveToNext()) {
                    Exercise exercise = new Exercise(
                            cursor.getString(cursor.getColumnIndex(DbHelper.TABLE_DAILY_COL_EXERCISE)),
                            cursor.getString(cursor.getColumnIndex(DbHelper.TABLE_DAILY_COL_DATE)),
                            cursor.getDouble(cursor.getColumnIndex(DbHelper.TABLE_DAILY_COL_WEIGHT)),
                            cursor.getInt(cursor.getColumnIndex(DbHelper.TABLE_DAILY_COL_REP)),
                            cursor.getInt(cursor.getColumnIndex(DbHelper.TABLE_DAILY_COL_SET)));

                    exercise.setCompletionRate(cursor.getDouble(cursor.getColumnIndex(DbHelper.TABLE_DAILY_COL_COMRATE)));
                    exercises.add(exercise);
                }
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        } finally {

            r.unlock();
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
            return exercises;
        }
    }

    public void recordDaily(String date, String exerciseName, double completionRate, double weight, int sets, int reps,Long scheduleId) {
        List<Exercise> exercises = null;
        Cursor cursor = null;
        r.lock();
        try {
            db = dbHelper.getReadableDatabase();
            String sql = "INSERT INTO `" + DbHelper.TABLE_NAME_DAILY
                    + "`(`" + DbHelper.TABLE_DAILY_COL_EXERCISE + "`," +
                    "`" + DbHelper.TABLE_DAILY_COL_USERID + "`," +
                    "`" + DbHelper.TABLE_DAILY_COL_DATE + "`," +
                    "`" + DbHelper.TABLE_DAILY_COL_COMRATE + "`," +
                    "`" + DbHelper.TABLE_DAILY_COL_WEIGHT + "`," +
                    "`" + DbHelper.TABLE_DAILY_COL_REP + "`," +
                    "`" + DbHelper.TABLE_DAILY_COL_SCHDID + "`," +
                    "`" + DbHelper.TABLE_DAILY_COL_SET + "`) " +

                    "VALUES ('" + exerciseName + "','" + 0 + "','" + date
                    + "','" + completionRate + "','" + weight + "','" + reps + "','" + scheduleId + "','" + sets + "')";

            db.execSQL(sql);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        } finally {

            r.unlock();
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }

        }
    }
//
//
//    }
//
//    public int write(List<Celebrity> celebrities) {
//        int count = 0;
//        db = dbHelper.getWritableDatabase();
//        w.lock();
//        try {
//            db.beginTransaction();
//            for (Celebrity celebrity : celebrities) {
//                try {
//                    String sql = "INSERT INTO " + DbHelper.TABLE_NAME + " (name, biography, image_url, visibility,local_path) " +
//                            "values ('" + celebrity.getName() + "','" + celebrity.getBio() + "','" + celebrity.getURL() + "','" + (celebrity.isVisible() ? 1 : 0) + "','" + celebrity.getLocalImagePath() + "');";
//                    db.execSQL(sql);
//                    count++;
//                } catch (Exception e) {
//                    Log.e(LOG_TAG, e.getMessage(), e);
//                    continue;
//                }
//            }
//            db.setTransactionSuccessful();
//
//        } catch (Exception e) {
//            Log.e(LOG_TAG, e.getMessage(), e);
//        } finally {
//
//            w.unlock();
//            if (db != null) {
//                db.endTransaction();
//                db.close();
//            }
//            return count;
//        }
//
//    }
//
//
//    public void exsits() {
//
//    }

}
