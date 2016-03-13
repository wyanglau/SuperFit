package ca.utoronto.ee1778.superfit.controller;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.lzyzsd.circleprogress.DonutProgress;

import java.util.Collections;
import java.util.List;

import ca.utoronto.ee1778.superfit.R;
import ca.utoronto.ee1778.superfit.object.Exercise;
import ca.utoronto.ee1778.superfit.object.Schedule;
import ca.utoronto.ee1778.superfit.service.ExerciseService;
import ca.utoronto.ee1778.superfit.service.ScheduleService;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HistoryFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistoryFragment extends Fragment {


    private OnFragmentInteractionListener mListener;


    private DonutProgress donutProgress;

    private StackBarController stackBarController;
    private ScheduleService scheduleService;
    private ExerciseService exerciseService;
    private Context mContext;

    public HistoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment HistoryFragment.
     */
    public static HistoryFragment newInstance(int position) {
        HistoryFragment fragment = new HistoryFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = getActivity().getApplicationContext();
        View view = inflater.inflate(R.layout.fragment_history, container, false);


        donutProgress = (DonutProgress) view.findViewById(R.id.donut_progress);
        stackBarController = new StackBarController(view);


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        scheduleService = new ScheduleService(mContext);
        Schedule schedule = scheduleService.findSchedule();
        exerciseService = new ExerciseService(mContext);
        List<Exercise> exerciseList = exerciseService.getCurrentExercises(schedule.getId());


        if (!exerciseList.isEmpty()) {
            //initial progress bar
            updateProgress(exerciseService.totalProgress(exerciseList));

            int border = exerciseList.size() > 10 ? 10 : exerciseList.size();

            List<Exercise> subList = exerciseList.subList(0,border);
            Collections.reverse(subList);

            float[] success = new float[border];
            float[] failures = new float[border];
            String[] lables = new String[border];

            //initial statck bar data
            for (int i = 0; i < border; i++) {

                Exercise exercise = subList.get(i);


                lables[i] = exercise.getDate().substring(6, 10);

                success[i] = exercise.getSuccess_times();
                failures[i] = exercise.getFailed_times();

            }

            for (int i = 0; i < success.length; i++) {
                float total = success[i] + failures[i];
                success[i] = (success[i] / (total)) * 100;
                failures[i] = (failures[i] / total) * 100;
            }



            stackBarController.init(lables, success, failures);
        }


    }


    public void updateProgress(final int progress) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                donutProgress.setProgress(progress);
            }
        });
    }

    //// TODO: 2016-03-09
    public void updateBarchart(float[] successes, float[] failures, String[] lables) {
        stackBarController.update(successes, failures, lables);
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


}
