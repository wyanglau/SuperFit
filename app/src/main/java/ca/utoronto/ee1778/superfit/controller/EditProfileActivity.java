package ca.utoronto.ee1778.superfit.controller;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.EditText;

import ca.utoronto.ee1778.superfit.R;
import ca.utoronto.ee1778.superfit.common.Constant;
import ca.utoronto.ee1778.superfit.object.User;
import ca.utoronto.ee1778.superfit.service.UserService;

public class EditProfileActivity extends Activity {

    private EditText name;
    private EditText age;
    private EditText weight;
    private User user;

    private UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        userService = new UserService(this);
        user = userService.getUser();
        name = (EditText) findViewById(R.id.edittext_edit_name);
        age = (EditText) findViewById(R.id.editText_edit_age);
        weight = (EditText) findViewById(R.id.editText_edit_weight);
        name.setText(user.getName());
        age.setText(String.valueOf(user.getAge()));
        weight.setText(String.valueOf(user.getWeight()));

    }

    public void onBtnConfirm(View view) {
        user.setName(name.getText().toString());
        user.setAge(Integer.valueOf(age.getText().toString()));
        user.setWeight(weight.getText().toString());
        userService.update(user);
        finish();
    }

    public void onBtnCancel(View view) {
        finish();
    }


}
