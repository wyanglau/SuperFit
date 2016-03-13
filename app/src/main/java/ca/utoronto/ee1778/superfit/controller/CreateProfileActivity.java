package ca.utoronto.ee1778.superfit.controller;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import ca.utoronto.ee1778.superfit.R;
import ca.utoronto.ee1778.superfit.common.Constant;
import ca.utoronto.ee1778.superfit.object.User;
import ca.utoronto.ee1778.superfit.service.UserService;

public class CreateProfileActivity extends AppCompatActivity {

    private EditText name;
    private EditText age;
    private EditText weight;
    private Button createBtn;
    private Context mContext;
    private UserService userService;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);
        mContext = this;
        userService = new UserService(mContext);
        name = (EditText) findViewById(R.id.editText_person_name);
        age = (EditText) findViewById(R.id.editText_person_age);
        weight = (EditText) findViewById(R.id.editText_person_weight);
        createBtn = (Button) findViewById(R.id.createUserBtn);
    }


    public void createNewProfile(View view) {

        String userName = name.getText().toString();
        int userAge = Integer.valueOf(age.getText().toString());
        String userWeight = weight.getText().toString();
        user = new User(userName, userAge, userWeight, null);
        userService.create(user);
        startMainActivity();

    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constant.EXTRAS_TAG_USER, user);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();

    }


}
