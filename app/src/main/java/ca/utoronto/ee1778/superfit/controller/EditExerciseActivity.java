package ca.utoronto.ee1778.superfit.controller;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import ca.utoronto.ee1778.superfit.R;
import ca.utoronto.ee1778.superfit.object.Schedule;
import ca.utoronto.ee1778.superfit.service.ScheduleService;

public class EditExerciseActivity extends Activity {

    private TextView exerciseName;
    private EditText weight;
    private EditText sets;
    private EditText reps;
    private Schedule schedule;
    private ScheduleService scheduleService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_exercise);

        exerciseName = (TextView) findViewById(R.id.textview_edit_exerciseName);
        weight = (EditText) findViewById(R.id.editTextView_edit_weight);
        sets = (EditText) findViewById(R.id.editTextView_edit_sets);
        reps = (EditText) findViewById(R.id.editText_edit_rep);

        scheduleService = new ScheduleService(this);
        schedule = scheduleService.findSchedule();

        exerciseName.setText(schedule.getExercise());
        weight.setText(String.valueOf(schedule.getWeight()));
        sets.setText(String.valueOf(schedule.getSets()));
        reps.setText(String.valueOf(schedule.getRep()));
    }


    public void editConfirm(View view) {

        String weight_text = weight.getText().toString();
        String set_text = sets.getText().toString();
        String reps_text = reps.getText().toString();

        if (weight_text.isEmpty() || set_text.isEmpty() || reps_text.isEmpty()) {
            Toast.makeText(this, "WEIGHT/SET/REPETITION cannot be empty.", Toast.LENGTH_SHORT).show();
            return;
        }
        double weight_new = Double.valueOf(weight_text);
        int sets_new = Integer.valueOf(set_text);
        int reps_new = Integer.valueOf(reps_text);
        schedule.setWeight(weight_new);
        schedule.setRep(reps_new);
        schedule.setSets(sets_new);
        scheduleService.update(schedule);

        finish();

    }

    public void cancel(View view) {
        finish();
    }

}
