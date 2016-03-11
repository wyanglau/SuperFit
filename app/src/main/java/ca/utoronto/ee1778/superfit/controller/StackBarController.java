package ca.utoronto.ee1778.superfit.controller;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.TextView;

import com.db.chart.Tools;
import com.db.chart.listener.OnEntryClickListener;
import com.db.chart.model.BarSet;
import com.db.chart.view.StackBarChartView;
import com.db.chart.view.XController;
import com.db.chart.view.YController;
import com.db.chart.view.animation.Animation;

import ca.utoronto.ee1778.superfit.R;

/**
 * Created by liuwyang on 2016-03-09.
 */
public class StackBarController {


    private final TextView mLegendOneRed;
    private final TextView mLegendOneGreen;
    private final StackBarChartView mChart;

    private final String[] mLabels = {"6.17", "6.18", "6.19", "6.20", "6.21", "6.22", "6.23", "6.17", "6.18", "6.19"};
    private final float[][] mValuesOne = {
            {99f, 80f, 80f, 90f, 77f, 45f, 60f, 70f, 70f, 90f},
            {1f, 20f, 20f, 10f, 23f, 55f, 40f, 30f, 30f, 10f}};

    private static int maxDisplay = 10;
    private BarSet stackBarSet_success;
    private BarSet stackBarSet_failure;

    public StackBarController(View view) {
        mChart = (StackBarChartView) view.findViewById(R.id.stackbar_history);
        mLegendOneRed = (TextView) view.findViewById(R.id.legend_fail);
        mLegendOneGreen = (TextView) view.findViewById(R.id.legend_success);
    }

    public void init() {
        show();
    }

    // Runnable action can be set as end action, but we dont need it here.
    public void show() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            mChart.setOnEntryClickListener(new OnEntryClickListener() {
                @SuppressLint("NewApi")
                @Override
                public void onClick(int setIndex, int entryIndex, Rect rect) {
                    if (setIndex == 1)
                        mLegendOneRed.animate()
                                .scaleY(1.3f)
                                .scaleX(1.3f)
                                .setDuration(100)
                                .withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        mLegendOneRed.animate()
                                                .scaleY(1.0f)
                                                .scaleX(1.0f)
                                                .setDuration(100);
                                    }
                                });
                    else {
                        mLegendOneGreen.animate()
                                .scaleY(1.3f)
                                .scaleX(1.3f)
                                .setDuration(100)
                                .withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        mLegendOneGreen.animate()
                                                .scaleY(1.0f)
                                                .scaleX(1.0f)
                                                .setDuration(100);
                                    }
                                });
                    }
                }
            });

        Paint thresPaint = new Paint();
        thresPaint.setColor(Color.parseColor("#dad8d6"));
        thresPaint.setPathEffect(new DashPathEffect(new float[]{10, 20}, 0));
        thresPaint.setStyle(Paint.Style.STROKE);
        thresPaint.setAntiAlias(true);
        thresPaint.setStrokeWidth(Tools.fromDpToPx(.75f));

        stackBarSet_success = new BarSet(mLabels, mValuesOne[0]);
        stackBarSet_success.setColor(Color.parseColor("#a1d949"));
        mChart.addData(stackBarSet_success);


        stackBarSet_failure = new BarSet(mLabels, mValuesOne[1]);
        stackBarSet_failure.setColor(Color.parseColor("#ff7a57"));
        mChart.addData(stackBarSet_failure);

        mChart.setBarSpacing(Tools.fromDpToPx(15));
        mChart.setRoundCorners(Tools.fromDpToPx(1));

        mChart.setXAxis(false)
                .setXLabels(XController.LabelPosition.OUTSIDE)
                .setYAxis(false)
                .setYLabels(YController.LabelPosition.NONE)
                .setValueThreshold(89.f, 89.f, thresPaint);

        int[] order = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        mChart.show(new Animation()
                        .setOverlap(.5f, order)
                // .setEndAction(action)

        );
    }


    public void update(float[] successes, float[] failures, String[] lables) {

//            mChart.updateValues(0, successes);
//            mChart.updateValues(1, failures);



        mChart.updateValues(0, successes);

        mChart.updateValues(1, failures);




        mChart.notifyDataUpdate();
    }


    public void dismiss(Runnable action) {

        int[] order = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
        mChart.dismiss(new Animation()
                .setOverlap(.5f, order)
                .setEndAction(action));
    }
}
