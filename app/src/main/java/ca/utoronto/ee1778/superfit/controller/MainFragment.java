package ca.utoronto.ee1778.superfit.controller;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import ca.utoronto.ee1778.superfit.R;
import ca.utoronto.ee1778.superfit.controller.adapter.ExerciseTrackerAdapter;
import ca.utoronto.ee1778.superfit.object.Exercise;
import ca.utoronto.ee1778.superfit.service.ExerciseService;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {

    private OnFragmentInteractionListener mListener;


    private RecyclerView mRecyclerView;
    private Context mContext;
    private View mView;
    private ExerciseService exerciseService;
    private ExerciseTrackerAdapter mAdapter;

    public MainFragment() {
        // Required empty public constructor
    }


    public static MainFragment newInstance(int position) {
        MainFragment fragment = new MainFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View mView = inflater.inflate(R.layout.fragment_main, container, false);
        mContext = getActivity().getApplicationContext();
        mRecyclerView = (RecyclerView) mView.findViewById(R.id.fragment_main);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        exerciseService = new ExerciseService(mContext);

        List<Exercise> exerciseList = exerciseService.getALlDailyRecords();
        mAdapter = new ExerciseTrackerAdapter(mContext, exerciseList);
        mRecyclerView.setAdapter(mAdapter);

        return mView;
    }

    public void insertData(Exercise exercise){
        mAdapter.insert(exercise,0);
    }

    public List<Exercise> gettestdata() {
        //---test data---
        List<Exercise> exerciseList = new ArrayList<>(5);

        Exercise exercise = new Exercise("wanghaha", "2016-05-12", 100, 1000,5);
        exercise.setLogoId(R.drawable.cat);
        exercise.setCompletionRate(99);

        Exercise exercise1 = new Exercise("wanghaha", "2016-05-12", 100, 1000,5);
        exercise1.setLogoId(R.drawable.cat);
        exercise1.setCompletionRate(50);
        Exercise exercise2 = new Exercise("wanghaha", "2016-05-12", 50, 1000,5);
        exercise2.setLogoId(R.drawable.cat);
        exercise2.setCompletionRate(66);
        exerciseList.add(exercise);
        exerciseList.add(exercise1);
        exerciseList.add(exercise2);
        return exerciseList;
        //--test end
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
