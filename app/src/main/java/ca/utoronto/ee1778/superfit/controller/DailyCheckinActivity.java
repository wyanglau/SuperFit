package ca.utoronto.ee1778.superfit.controller;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ca.utoronto.ee1778.superfit.R;
import ca.utoronto.ee1778.superfit.common.BluetoothGATTDefines;
import ca.utoronto.ee1778.superfit.common.BluetoothLeService;
import ca.utoronto.ee1778.superfit.common.Constant;
import ca.utoronto.ee1778.superfit.common.GattInfo;
import ca.utoronto.ee1778.superfit.common.GenericBluetoothProfile;
import ca.utoronto.ee1778.superfit.object.Exercise;
import ca.utoronto.ee1778.superfit.object.Schedule;
import ca.utoronto.ee1778.superfit.object.User;
import ca.utoronto.ee1778.superfit.profile.HeartRateMonitorProfile;
import ca.utoronto.ee1778.superfit.profile.SensorTagAccelerometerProfile;
import ca.utoronto.ee1778.superfit.profile.SensorTagMovementProfile;
import ca.utoronto.ee1778.superfit.service.ExerciseService;
import ca.utoronto.ee1778.superfit.service.ScheduleService;
import ca.utoronto.ee1778.superfit.utils.PreferenceWR;

public class DailyCheckinActivity extends Activity {


    private ExerciseService exerciseService;
    private User user;


    private static final String TAG = "DailyCheckinActivity";
    // Activity
    public static final String EXTRA_DEVICE = "EXTRA_DEVICE";
    private static final int PREF_ACT_REQ = 0;
    private static final int FWUPDATE_ACT_REQ = 1;

    // BLE
    private BluetoothLeService mBtLeService = null;

    private List<BluetoothGatt> gatts = null;
    private List<BluetoothGattService> mServiceList = null;
    private boolean mServicesRdy = false;
    private boolean mIsReceiving = false;

    // SensorTagGatt
    private BluetoothGattService mOadService = null;
    private BluetoothGattService mConnControlService = null;
    private BluetoothGattService mTestService = null;
    //GUI
    private List<GenericBluetoothProfile> mProfiles;


    private TextView heartRateTextView;
    private TextView angleTextview;
    private TextView exerciseTextview;
    private TextView setsTextView;
    private TextView repTextView;
    private TextView successTimes;
    private ImageView resultImageView;

    private Button startNewTestBtn;
    private Button confirmBtn;
    private Button skipBtn;


    private Button checkInButton;

    private ScheduleService scheduleService;

    private int activity_mode;

    public DailyCheckinActivity() {
    }

    public static DailyCheckinActivity getInstance() {
        return (DailyCheckinActivity) new DailyCheckinActivity();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_checkin);

        heartRateTextView = (TextView) findViewById(R.id.textview_heartRate);
        angleTextview = (TextView) findViewById(R.id.textview_tile);
        exerciseTextview = (TextView) findViewById(R.id.textview_daily_exercise);
        setsTextView = (TextView) findViewById(R.id.textview_daily_sets);
        repTextView = (TextView) findViewById(R.id.textview_daily_rep);
        resultImageView = (ImageView) findViewById(R.id.imageview_daily_result);
        successTimes = (TextView) findViewById(R.id.testview_daily_success_times);
        exerciseService = ExerciseService.newInstance(this);
        checkInButton = (Button) findViewById(R.id.button_checkin);

        startNewTestBtn = (Button) findViewById(R.id.button_test_start_new);
        confirmBtn = (Button) findViewById(R.id.button_test_confirm);
        skipBtn = (Button) findViewById(R.id.button_test_skip);


        Intent intent = getIntent();
        activity_mode = intent.getIntExtra(Constant.EXTRAS_TAG_TEST_MODE, 1);
        user = (User) intent.getSerializableExtra(Constant.EXTRAS_TAG_USER);

        // BLE
        mBtLeService = BluetoothLeService.getInstance();
        mServiceList = new ArrayList<BluetoothGattService>();
        gatts = mBtLeService.getGatts();

        scheduleService = new ScheduleService(this);


        if (activity_mode == Constant.MODE_CHECK_IN) {
            initScheduleView();
        } else {
            initTestView();
            exerciseService.refresh();
        }

    }

    private void initTestView() {
        successTimes.setVisibility(View.VISIBLE);
        startNewTestBtn.setVisibility(View.VISIBLE);
        confirmBtn.setVisibility(View.VISIBLE);
        skipBtn.setVisibility(View.VISIBLE);
    }

    private void initScheduleView() {
        exerciseTextview.setVisibility(View.VISIBLE);
        setsTextView.setVisibility(View.VISIBLE);
        repTextView.setVisibility(View.VISIBLE);
        checkInButton.setVisibility(View.VISIBLE);

        Schedule schedule = scheduleService.findSchedule();
        exerciseTextview.setText(schedule.getExercise());
        setsTextView.setText(String.valueOf(schedule.getSets()));
        repTextView.setText(String.valueOf(schedule.getRep()));
    }

    public void recordAndReturn(View view) {

        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy.MM.dd hh:mm:ss");
        String date = sDateFormat.format(new java.util.Date());
        // --test data
        Exercise exercise = new Exercise("wanghaha", date, 100, 1000, 5);
        exercise.setLogoId(R.drawable.cat);

        Random r = new Random();
        int Low = 10;
        int High = 100;
        int Result = r.nextInt(High - Low) + Low;

        exercise.setCompletionRate(Result);
        exercise.setScheduleId(Long.valueOf(1));

        Random r2 = new Random();
        int Low_2 = 60;
        int High_2 = 100;
        int Result_2 = r2.nextInt(High_2 - Low_2) + Low_2;

        exercise.setFailed_times(100 - Result_2);
        exercise.setSuccess_times(Result_2);

        exerciseService.record(exercise);


        this.finish();
    }

    public void onBtnSkip(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constant.EXTRAS_TAG_USER, user);
        intent.putExtras(bundle);
        startActivity(intent);
        this.finish();
    }

    public void onBtnSetupBle(View view) {
        Intent intent = new Intent(this, BluetoothSearchActivity.class);
        startActivity(intent);
    }

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        List<BluetoothGattService> serviceList;
        List<BluetoothGattCharacteristic> charList = new ArrayList<BluetoothGattCharacteristic>();

        @Override
        public void onReceive(final Context context, Intent intent) {

            final String action = intent.getAction();
            Log.d("DailyCheckinActivity", "Received intent, action =" + action);
            final int status = intent.getIntExtra(BluetoothLeService.EXTRA_STATUS,
                    BluetoothGatt.GATT_SUCCESS);


            serviceList = mBtLeService.getSupportedGattServices();
            if (serviceList.size() > 0) {
                for (int ii = 0; ii < serviceList.size(); ii++) {
                    BluetoothGattService s = serviceList.get(ii);
                    List<BluetoothGattCharacteristic> c = s.getCharacteristics();
                    if (c.size() > 0) {
                        for (int jj = 0; jj < c.size(); jj++) {
                            charList.add(c.get(jj));
                        }
                    }
                }
            }


            gatts = mBtLeService.getGatts();
            if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                if (status == BluetoothGatt.GATT_SUCCESS) {


                    BluetoothGatt tmpBluetoothGatt = null;
                    for (final BluetoothGatt g : gatts) {
                        String device_addr = intent.getStringExtra(BluetoothLeService.EXTRA_ADDRESS);
                        if (device_addr.equals(g.getDevice().getAddress())) {
                            tmpBluetoothGatt = g;
                            break;
                        }
                    }
                    if (tmpBluetoothGatt == null) {
                        Log.e("DailyCheckinActivity", "Gettng a new bluetooth gatt from broadcast.");
                        return;

                    }
                    final BluetoothGatt bluetoothGatt = tmpBluetoothGatt;
                    Log.d("DailyCheckinActivity", "Total characteristics " + charList.size());
                    Thread worker = new Thread(new Runnable() {
                        @Override
                        public void run() {

                            //Iterate through the services and add GenericBluetoothServices for each service
                            int nrNotificationsOn = 0;
                            int maxNotifications;
                            int servicesDiscovered = 0;
                            int totalCharacteristics = 0;
                            //serviceList = mBtLeService.getSupportedGattServices();
                            // Ryan: we need to get the new gatts from BLE service again, to find the supported services
                            List<GenericBluetoothProfile> currentProfiles = new ArrayList<GenericBluetoothProfile>(3);
                            final List<BluetoothGattService> currentServices = bluetoothGatt.getServices();
                            final BluetoothDevice currentDevice = bluetoothGatt.getDevice();
                            for (BluetoothGattService s : currentServices) {
                                List<BluetoothGattCharacteristic> chars = s.getCharacteristics();
                                totalCharacteristics += chars.size();
                            }
                            if (totalCharacteristics == 0) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                                context);
                                        alertDialogBuilder.setTitle("Error !");
                                        alertDialogBuilder.setMessage(currentServices.size() + " Services found, but no characteristics found, device will be disconnected !");
                                        alertDialogBuilder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                mBtLeService.refreshDeviceCache(bluetoothGatt);
                                                discoverServices(bluetoothGatt);
                                            }

                                        });
                                        alertDialogBuilder.setNegativeButton("Disconnect", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                mBtLeService.disconnect(currentDevice.getAddress());
                                            }
                                        });
                                        AlertDialog a = alertDialogBuilder.create();
                                        a.show();
                                    }
                                });
                                return;
                            }
                            final int final_totalCharacteristics = totalCharacteristics;
                            if (Build.VERSION.SDK_INT > 18) maxNotifications = 7;
                            else {
                                maxNotifications = 4;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(context, "Android version 4.3 detected, max 4 notifications enabled", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                            for (int ii = 0; ii < currentServices.size(); ii++) {
                                BluetoothGattService s = currentServices.get(ii);
                                List<BluetoothGattCharacteristic> chars = s.getCharacteristics();
                                if (chars.size() == 0) {

                                    Log.d("DailyCheckinActivity", "No characteristics found for this service !!!");
                                    return;
                                }
                                servicesDiscovered++;
                                final float serviceDiscoveredcalc = (float) servicesDiscovered;
                                final float serviceTotalcalc = (float) currentServices.size();
                                Log.d("DailyCheckinActivity", "Configuring service with uuid : " + s.getUuid().toString());

                                if (SensorTagMovementProfile.isCorrectService(s)) {
                                    SensorTagMovementProfile mov = new SensorTagMovementProfile(context, currentDevice, s, mBtLeService, bluetoothGatt, exerciseService);
                                    currentProfiles.add(mov);
                                    if (nrNotificationsOn < maxNotifications) {
                                        mov.configureService();
                                        nrNotificationsOn++;
                                    }
                                    Log.d("DailyCheckinActivity", "Found Motion !");
                                }
                                if (SensorTagAccelerometerProfile.isCorrectService(s)) {
                                    SensorTagAccelerometerProfile acc = new SensorTagAccelerometerProfile(context, currentDevice, s, mBtLeService, bluetoothGatt);
                                    currentProfiles.add(acc);
                                    if (nrNotificationsOn < maxNotifications) {
                                        acc.configureService();
                                        nrNotificationsOn++;
                                    }
                                    Log.d("DailyCheckinActivity", "Found Motion !");

                                }


                                if (HeartRateMonitorProfile.isCorrectService(s)) {
                                    HeartRateMonitorProfile devInfo = new HeartRateMonitorProfile(context, currentDevice, s, mBtLeService, bluetoothGatt);
                                    currentProfiles.add(devInfo);
                                    devInfo.configureService();
                                    Log.d("DailyCheckinActivity", "Found Heart Rate!");
                                }

                                if ((s.getUuid().toString().compareTo("f000ccc0-0451-4000-b000-000000000000")) == 0) {
                                    mConnControlService = s;
                                }
                            }

//                            mProfiles.addAll(currentProfiles);
                            mBtLeService.getmProfiles().addAll(currentProfiles);
                            for (final GenericBluetoothProfile p : currentProfiles) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Log.d(TAG, "Adding row to view for DEVICE:" + p.getDeviceName());
                                        //// TODO: 2016-03-10 need to handle the data to be presented here. The mDeviceView used to add one row of device to display
                                        // mDeviceView.addRowToTable(p.getTableRow());
                                        p.enableService();
                                        //  progressDialog.setProgress(progressDialog.getProgress() + 1);
                                    }
                                });
                            }
                        }

                    });
                    worker.start();
                } else {
                    Toast.makeText(getApplication(), "Service discovery failed",
                            Toast.LENGTH_LONG).show();
                    return;
                }
            } else if (BluetoothLeService.ACTION_DATA_NOTIFY.equals(action)) {
                // Notification
                Log.d("DailyCheckinActiviy", "Received Data Changed Notification, now we have profiles+" + mProfiles.toString());
                byte[] value = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
                String uuidStr = intent.getStringExtra(BluetoothLeService.EXTRA_UUID);
                //Log.d("DailyCheckinActivity","Got Characteristic : " + uuidStr);

                if (!exerciseService.isFinished()) {
                    for (int ii = 0; ii < charList.size(); ii++) {
                        BluetoothGattCharacteristic tempC = charList.get(ii);
                        if ((tempC.getUuid().toString().equals(uuidStr))) {
                            for (int jj = 0; jj < mProfiles.size(); jj++) {
                                GenericBluetoothProfile p = mProfiles.get(jj);
                                if (p.isDataC(tempC)) {
                                    if (p instanceof SensorTagMovementProfile) {
                                        ((SensorTagMovementProfile) p).didUpdateValueForCharacteristic(tempC, angleTextview, resultImageView);
                                    } else {
                                        p.didUpdateValueForCharacteristic(tempC, heartRateTextView);
                                    }
                                }
                            }
                            break;
                        }
                    }
                }

                //onCharacteristicChanged(uuidStr, value);
            } else if (BluetoothLeService.ACTION_DATA_WRITE.equals(action)) {
                // Data written
                byte[] value = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
                String uuidStr = intent.getStringExtra(BluetoothLeService.EXTRA_UUID);
                for (int ii = 0; ii < charList.size(); ii++) {
                    BluetoothGattCharacteristic tempC = charList.get(ii);
                    if ((tempC.getUuid().toString().equals(uuidStr))) {
                        for (int jj = 0; jj < mProfiles.size(); jj++) {
                            GenericBluetoothProfile p = mProfiles.get(jj);
                            p.didWriteValueForCharacteristic(tempC);
                        }
                        break;
                    }
                }
            } else if (BluetoothLeService.ACTION_DATA_READ.equals(action)) {
                // Data read
                byte[] value = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
                String uuidStr = intent.getStringExtra(BluetoothLeService.EXTRA_UUID);
                for (int ii = 0; ii < charList.size(); ii++) {
                    BluetoothGattCharacteristic tempC = charList.get(ii);
                    if ((tempC.getUuid().toString().equals(uuidStr))) {
                        for (int jj = 0; jj < mProfiles.size(); jj++) {
                            GenericBluetoothProfile p = mProfiles.get(jj);
                            p.didReadValueForCharacteristic(tempC);
                        }
                        break;
                    }
                }
            }
            if (status != BluetoothGatt.GATT_SUCCESS) {
                try {
                    Log.d("DailyCheckinActivity", "Failed UUID was " + intent.getStringExtra(BluetoothLeService.EXTRA_UUID));
                    setError("GATT error code: " + BluetoothGATTDefines.gattErrorCodeStrings.get(status));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    protected void onResume() {
        // Log.d(TAG, "onResume");
        super.onResume();

        mProfiles = mBtLeService.getmProfiles();

        if (!mIsReceiving) {
            registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
            mIsReceiving = true;
        }
        for (GenericBluetoothProfile p : mProfiles) {
            if (p.isConfigured != true) p.configureService();
            if (p.isEnabled != true) p.enableService();
        }

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        refresh();

        // GATT database
        Resources res = getResources();
        XmlResourceParser xpp = res.getXml(R.xml.gatt_uuid);
        new GattInfo(xpp);


//        this.mBtLeService.abortTimedDisconnect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        if (mqttProfile != null) {
//            mqttProfile.disconnect();
//
//        }
        if (mIsReceiving) {
            unregisterReceiver(mGattUpdateReceiver);
            mIsReceiving = false;
        }

//        if (!this.isEnabledByPrefs("keepAlive")) {
//            this.mBtLeService.timedDisconnect();
//        }
        //View should be started again from scratch
        //Ryan: try to unrelease the profile. Dont know what will happen; but in fact it is not right.
        this.mProfiles = null;
//        finishActivity(PREF_ACT_REQ);
//        finishActivity(FWUPDATE_ACT_REQ);
    }

    public boolean isEnabledByPrefs(String prefName) {
        String preferenceKeyString = "pref_"
                + prefName;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mBtLeService);

        Boolean defaultValue = true;
        return prefs.getBoolean(preferenceKeyString, defaultValue);
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter fi = new IntentFilter();
        fi.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        fi.addAction(BluetoothLeService.ACTION_DATA_NOTIFY);
        fi.addAction(BluetoothLeService.ACTION_DATA_WRITE);
        fi.addAction(BluetoothLeService.ACTION_DATA_READ);
        return fi;
    }


    void refresh() {

        for (BluetoothGatt g : gatts) {
            PreferenceWR p = new PreferenceWR(g.getDevice().getAddress(), this);
            if (p.getBooleanPreference(PreferenceWR.PREFERENCEWR_NEEDS_REFRESH) == true) {
                Log.d("DailyCheckinActivity", "Need to refresh device cache, calling refreshDeviceCache()");

                boolean refresh = false;

                boolean rt = this.mBtLeService.refreshDeviceCache(g);
                refresh = rt && refresh;


                if (refresh) {
                    if (!mServicesRdy && g != null) {
                        if (mBtLeService.getNumServices() == 0) {

                            discoverServices(g);

                        } else {
                        }
                    }
                }
                p.setBooleanPreference(PreferenceWR.PREFERENCEWR_NEEDS_REFRESH, false);
            } else {
                // Start service discovery
                if (!mServicesRdy && g != null) {
                    if (mBtLeService.getNumServices() == 0)

                        discoverServices(g);

                    else {
                    }
                }
            }
        }
    }


    public BluetoothGattService getOadService() {
        return mOadService;
    }

    public BluetoothGattService getConnControlService() {
        return mConnControlService;
    }

    public BluetoothGattService getTestService() {
        return mTestService;
    }


    // Activity result handling
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            default:
                break;
        }
    }

    private void setError(String txt) {
        Toast.makeText(this, txt, Toast.LENGTH_LONG).show();
    }


    private void discoverServices(BluetoothGatt gatt) {
        mBtLeService.discoverServices(gatt);
    }

    @Override
    public void onStop() {

        super.onStop();
        // mBtLeService.closeAllGatts();
    }

    public void toastit(Context context) {
        Toast.makeText(context, "haha", Toast.LENGTH_SHORT).show();
    }
}
