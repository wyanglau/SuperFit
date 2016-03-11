package ca.utoronto.ee1778.superfit.controller.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ca.utoronto.ee1778.superfit.R;
import ca.utoronto.ee1778.superfit.object.Exercise;

/**
 * Created by liuwyang on 2016-03-08.
 * <p/>
 * Adapter for exercise daily log. Not the list for exercise list. We only provide 1 or 2 exercises as demo within this app.
 */
public class ExerciseTrackerAdapter extends RecyclerView.Adapter<ExerciseTrackerAdapter.RecyclerViewHolder> {

    private static final String SUFFIX_WEIGHT = "lbs";
    private static final String SUFFIX_RATE ="%";
    private Context mContext;
    private List<Exercise> exerciseList;

    public ExerciseTrackerAdapter(Context context, List<Exercise> exercises) {

        this.mContext = context;
        this.exerciseList = exercises;
        if (exerciseList == null) {
            exerciseList = new ArrayList<>(1);
        }

    }


    public void insert(Exercise exercise,int position){
        exerciseList.add(position,exercise);
        notifyItemInserted(position);


    }
    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.cardview_daily_detail, parent, false);
        RecyclerViewHolder holder = new RecyclerViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {


        //// TODO: 2016-03-08 we can add onClickListener for the cardview or the buttons on the cardview here.

        Exercise exercise = exerciseList.get(position);
        holder.imageView_exercise_logo.setImageResource(exercise.getLogoId());
        holder.textView_completionRate.setText(String.valueOf(exercise.getCompletionRate()) + SUFFIX_RATE);
        holder.textView_date.setText(exercise.getDate());
        holder.textView_weight.setText(exercise.getWeight()+SUFFIX_WEIGHT);
        holder.textView_rep.setText(String.valueOf(exercise.getRepetition()));


    }

    @Override
    public int getItemCount() {
        return exerciseList.size();
    }

    static class RecyclerViewHolder extends RecyclerView.ViewHolder {
        protected CardView cardView;
        protected ImageView imageView_exercise_logo;
        protected TextView textView_weight;
        protected TextView textView_rep;
        protected TextView textView_date;
        protected TextView textView_completionRate;


        public RecyclerViewHolder(View itemView) {
            super(itemView);

            cardView = (CardView) itemView.findViewById(R.id.card_view);
            imageView_exercise_logo = (ImageView) itemView.findViewById(R.id.imageView_exercise_logo);
            textView_weight = (TextView) itemView.findViewById(R.id.textview_weight);
            textView_completionRate = (TextView) itemView.findViewById(R.id.textview_completion_rate);
            textView_rep = (TextView) itemView.findViewById(R.id.textview_rep);
            textView_date = (TextView) itemView.findViewById(R.id.textview_date);


        }
    }
}
