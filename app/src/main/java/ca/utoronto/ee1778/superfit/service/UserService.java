package ca.utoronto.ee1778.superfit.service;

import android.content.Context;

import java.util.List;

import ca.utoronto.ee1778.superfit.DAO.DbUtils;
import ca.utoronto.ee1778.superfit.object.User;

/**
 * Created by liuwyang on 2016-03-07.
 */
public class UserService {

    private Context mContext;
    private DbUtils dbUtils;


    public UserService(Context context) {
        mContext = context;
        dbUtils = new DbUtils(mContext);
    }


    /**
     * only return the first USER since we right now only support one user exactly.
     * @return
     */
    public User getUser() {

        List<User> users = dbUtils.getAllUser();
        if (users.isEmpty()) {
            return null;
        } else {
            return users.get(0);
        }
    }

    public void create(User user) {

        dbUtils.createUser(user.getName(), user.getAge(), user.getWeight());

    }

}
