package ca.utoronto.ee1778.superfit.controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ca.utoronto.ee1778.superfit.R;
import ca.utoronto.ee1778.superfit.common.Constant;
import ca.utoronto.ee1778.superfit.object.Exercise;
import ca.utoronto.ee1778.superfit.object.Schedule;
import ca.utoronto.ee1778.superfit.object.User;
import ca.utoronto.ee1778.superfit.service.ExerciseService;
import ca.utoronto.ee1778.superfit.service.ScheduleService;

public class MainActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private String LOG_TAG = "MainActivity";
    private Context mContext;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        setContentView(R.layout.activity_main);

        mContext = this;
        user = (User) getIntent().getSerializableExtra(Constant.EXTRAS_TAG_USER);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(mContext, DailyCheckinActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constant.EXTRAS_TAG_USER, user);

                intent.putExtras(bundle);
                intent.putExtra(Constant.EXTRAS_TAG_TEST_MODE, Constant.MODE_CHECK_IN);

                startActivity(intent);

            }
        });

    }
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
    @Override
    protected void onResume() {
        super.onResume();
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);


    }


    //// TODO: 2016-03-09  only test data here now
    public void updateBarchart(View view) {
        HistoryFragment fragment = (HistoryFragment) mSectionsPagerAdapter.getFragments().get(1);
        final String[] mLabels = {"6.11", "6.12", "6.13", "6.14", "6.15", "6.16", "6.17", "6.18", "6.19", "6.20"};
        final float[][] mValuesOne = {
                {25f, 40f, 40f, 40f, 40f, 25f, 25f, 30f, 30f, 30f},
                {25f, 25f, 30f, 25f, 25f, 30f, 25f, 30f, 25f, 25f},
                {30f, 30f, 30f, 25f, 25f, 25f, 25f, 25f, 40f, 25f}};
        fragment.updateBarchart(mValuesOne[0], mValuesOne[1], mLabels);
    }

    public void notifyInsert(Exercise exercise) {
        MainFragment fragment = (MainFragment) mSectionsPagerAdapter.getFragments().get(0);
        fragment.insertData(exercise);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_exercise) {


            editExercise();

            return true;
        } else if (id == R.id.action_profile) {
            Toast.makeText(this, "action_profile", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_new_exercise) {
            newExercise();
            return true;
        } else if (id == R.id.action_ble) {
            onBleAction();
        }

        return super.onOptionsItemSelected(item);
    }

    private void newExercise() {
        Intent intent = new Intent(this, DailyCheckinActivity.class);
        intent.putExtra(Constant.EXTRAS_TAG_TEST_MODE, Constant.MODE_TEST);
        startActivity(intent);
        finish();
    }

    private void editExercise() {
        Intent intent = new Intent(this, EditExerciseActivity.class);
        startActivity(intent);


    }

    private void onBleAction() {
        Intent intent = new Intent(this, BluetoothSearchActivity.class);
        startActivity(intent);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> fragments;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            this.fragments = new ArrayList<>(2);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).

            switch (position) {
                case (0):
                    MainFragment mainFragment = MainFragment.newInstance(position);
                    fragments.add(mainFragment);
                    return mainFragment;
                default:
                    HistoryFragment historyFragment = HistoryFragment.newInstance(position);
                    fragments.add(historyFragment);
                    return historyFragment;
            }

        }


        public List<Fragment> getFragments() {
            return this.fragments;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "main";
                case 1:
                    return "hisotry";

            }
            return null;
        }
    }


}
