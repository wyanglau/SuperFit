package ca.utoronto.ee1778.superfit.controller;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ca.utoronto.ee1778.superfit.R;
import ca.utoronto.ee1778.superfit.common.BleDeviceInfo;
import ca.utoronto.ee1778.superfit.common.BluetoothLeService;
import ca.utoronto.ee1778.superfit.common.HCIDefines;


public class BluetoothSearchActivity extends ViewPagerActivity {

    // Requests to other activities
    private static final int REQ_ENABLE_BT = 0;
    private static final int REQ_DEVICE_ACT = 1;

    // GUI
    private static BluetoothSearchActivity mThis = null;
    private ScanView mScanView;
    private Intent mDeviceIntent;
    private static final int STATUS_DURATION = 5;

    // BLE management
    private boolean mBtAdapterEnabled = false;
    private boolean mBleSupported = true;
    private boolean mScanning = false;
    private int mNumDevs = 0;
    private int mConnIndex = NO_DEVICE;
    private List<BleDeviceInfo> mDeviceInfoList;
    private static BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBtAdapter = null;
    private BluetoothDevice mBluetoothDevice = null;
    private BluetoothLeService mBluetoothLeService = null;
    private IntentFilter mFilter;
    private String[] mDeviceFilter = null;

    private Context mContext;

    // Housekeeping
    private static final int NO_DEVICE = -1;
    private boolean mInitialised = false;
    SharedPreferences prefs = null;

    public BluetoothSearchActivity() {
        mThis = this;
        mResourceFragmentPager = R.layout.fragment_pager;
        mResourceIdPager = R.id.pager;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);

        mContext = this.getApplicationContext();

        // Check for Bluetooth support, if not exit application.
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBtAdapter == null) {
            AlertDialog.Builder aB = new AlertDialog.Builder(this);
            aB.setTitle("Error !");
            aB.setMessage("This Android device does not have Bluetooth or there is an error in the " +
                    "bluetooth setup. Application cannot start, will exit.");
            aB.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    System.exit(0);
                }
            });
            AlertDialog a = aB.create();
            a.show();
            return;
        }


        prefs = PreferenceManager.getDefaultSharedPreferences(this);


        // Initialize device list container and device filter
        mDeviceInfoList = new ArrayList<BleDeviceInfo>();
        Resources res = getResources();
        mDeviceFilter = res.getStringArray(R.array.device_filter);

        // Create the fragments and add them to the view pager and tabs
        mScanView = new ScanView();
        mSectionsPagerAdapter.addSection(mScanView, "BLE Device List");

//
//        //add scan view to current activity
//        getSupportFragmentManager().beginTransaction()
//                .add(R.id.fragment_container, mScanView).addToBackStack(null).commit();


        // Register the BroadcastReceiver
        mFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        mFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        mFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);

        System.out.println("Ryan: init of BluetoothSearchActivity done");
    }


    @Override
    public void onDestroy() {

        super.onDestroy();


        mBtAdapter = null;

        // Clear cache
        File cache = getCacheDir();
        String path = cache.getPath();
        try {
            Runtime.getRuntime().exec(String.format("rm -rf %s", path));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        unregisterReceiver(mReceiver);

    }

    public void onBtnScan(View view) {
        if (mScanning) {
            stopScan();
        } else {
            startScan();
        }
    }

    public void onBtnFinish(View view) {

        checkSchan();
        finish();
    }

    private void checkSchan() {
        if (mScanning)
            stopScan();

        setBusy(true);

    }

    private void startScan() {
        // Start device discovery
        if (mBleSupported) {
            mNumDevs = 0;
            mDeviceInfoList.clear();
            mScanView.notifyDataSetChanged();
            scanLeDevice(true);
            mScanView.updateGui(mScanning);
            if (!mScanning) {
                setError("Device discovery start failed");
                setBusy(false);
            }
        } else {
            setError("BLE not supported on this device");
        }

    }


    private void onUrl(final Uri uri) {
        Intent web = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(web);
    }

    private void onBluetooth() {
        Intent settingsIntent = new Intent(
                android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
        startActivity(settingsIntent);
    }


    void onScanViewReady(View view) {
        System.out.println("Ryan:begin to get the view the bluetooth Instance:" + BluetoothLeService.getInstance());

        if (!mInitialised) {
            // Broadcast receiver
            mBluetoothLeService = BluetoothLeService.getInstance();


            mBluetoothManager = mBluetoothLeService.getBtManager();
            mBtAdapter = mBluetoothManager.getAdapter();
            if (mBtAdapter == null) {
                Toast.makeText(this, "Bluetooth is not supported", Toast.LENGTH_LONG).show();
                return;
            }
            registerReceiver(mReceiver, mFilter);
            mBtAdapterEnabled = mBtAdapter.isEnabled();
            if (mBtAdapterEnabled) {
                // Start straight away
                //startBluetoothLeService();
            } else {
                // Request BT adapter to be turned on
                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableIntent, REQ_ENABLE_BT);
            }
            mInitialised = true;
        } else {
            mScanView.notifyDataSetChanged();
        }
        // Initial state of widgets
        updateGuiState();
        mScanView.showConnected();
    }

    // ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // GUI methods
    //
    public void updateGuiState() {
        boolean mBtEnabled = mBtAdapter.isEnabled();

        if (mBtEnabled) {
            if (mScanning) {
                // BLE Host connected
                if (mConnIndex != NO_DEVICE) {
                    String txt = mBluetoothDevice.getName() + " connected";
                    mScanView.setStatus(txt);
                } else {
                    mScanView.setStatus(mNumDevs + " devices");
                }
            }
        } else {
            mDeviceInfoList.clear();
            mScanView.notifyDataSetChanged();
        }
    }

    private void setBusy(boolean f) {
        mScanView.setBusy(f);
    }

    void setError(String txt) {
        mScanView.setError(txt);
        //CustomToast.middleBottom(this, "Turning BT adapter off and on again may fix Android BLE stack problems");
    }


    /**
     * BLUETOOTH METHODS
     */

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                // Bluetooth adapter state change
                switch (mBtAdapter.getState()) {
                    case BluetoothAdapter.STATE_ON:
                        mConnIndex = NO_DEVICE;
                        //startBluetoothLeService();
                        break;
                    case BluetoothAdapter.STATE_OFF:
                        Toast.makeText(context, "App Closed, thanks!", Toast.LENGTH_LONG)
                                .show();
                        finish();
                        break;
                    default:
                        // Log.w(TAG, "Action STATE CHANGED not processed ");
                        break;
                }

                updateGuiState();
            } else if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                // GATT connect
                int status = intent.getIntExtra(BluetoothLeService.EXTRA_STATUS,
                        BluetoothGatt.GATT_FAILURE);
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    setBusy(false);
                    //Ryan: Stop switching to the Device Activity, until pressing the button of transfer.
                    Toast.makeText(mContext, "Connected", Toast.LENGTH_SHORT).show();
                    //  startDeviceActivity();
                } else
                    setError("Connect failed. Status: " + status);
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                // GATT disconnect
                int status = intent.getIntExtra(BluetoothLeService.EXTRA_STATUS,
                        BluetoothGatt.GATT_FAILURE);
                stopDeviceActivity();
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    setBusy(false);
                    mScanView.setStatus(mBluetoothDevice.getName() + " disconnected",
                            STATUS_DURATION);
                } else {
                    setError("Disconnect Status: " + HCIDefines.hciErrorCodeStrings.get(status));
                }
                mConnIndex = NO_DEVICE;
                mBluetoothLeService.close();
            } else {
                // Log.w(TAG,"Unknown action: " + action);
            }

        }
    };

    // Device scan callback.
    // NB! Nexus 4 and Nexus 7 (2012) only provide one scan result per scan
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        public void onLeScan(final BluetoothDevice device, final int rssi,
                             byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                public void run() {
                    // Filter devices
                    if (checkDeviceFilter(device.getName())) {
                        if (!deviceInfoExists(device.getAddress())) {
                            // New device
                            BleDeviceInfo deviceInfo = createDeviceInfo(device, rssi);
                            addDevice(deviceInfo);
                        } else {
                            // Already in list, update RSSI info
                            BleDeviceInfo deviceInfo = findDeviceInfo(device);
                            deviceInfo.updateRssi(rssi);
                            mScanView.notifyDataSetChanged();
                        }
                    }
                }

            });
        }
    };

    List<BleDeviceInfo> getDeviceInfoList() {
        return mDeviceInfoList;
    }

    private void stopScan() {
        mScanning = false;
        mScanView.updateGui(false);
        scanLeDevice(false);


    }

    private boolean scanLeDevice(boolean enable) {
        if (enable) {
            mScanning = mBtAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBtAdapter.stopLeScan(mLeScanCallback);
        }
        return mScanning;
    }

    @Override
    public void onBackPressed() {
        if (mScanning)
            stopScan();
        else finish();
        setBusy(true);

    }

    private void startDeviceActivity() {
        // TODO: 2016-03-10  The Search Activity going to jump to test activity, not yet decided

//        mDeviceIntent = new Intent(this, DeviceActivity.class);
//        mDeviceIntent.putExtra(DeviceActivity.EXTRA_DEVICE, mBluetoothDevice);
//        startActivityForResult(mDeviceIntent, REQ_DEVICE_ACT);
    }

    private void stopDeviceActivity() {
        finishActivity(REQ_DEVICE_ACT);
    }

    public void onDeviceClick(final int pos) {

        if (mScanning)
            stopScan();

        setBusy(true);
        mBluetoothDevice = mDeviceInfoList.get(pos).getBluetoothDevice();
        //test
        mConnIndex = pos;
        onConnect();
//        if (mConnIndex == NO_DEVICE) {
//            mScanView.setStatus("Connecting");
//            mConnIndex = pos;
//            onConnect();
//        } else {
//            mScanView.setStatus("Disconnecting");
//            if (mConnIndex != NO_DEVICE) {
//                mBluetoothLeService.disconnect(mBluetoothDevice.getAddress());
//            }
//        }
    }

    void onConnect() {
        if (mNumDevs > 0) {

            int connState = mBluetoothManager.getConnectionState(mBluetoothDevice,
                    BluetoothGatt.GATT);

            switch (connState) {
//                case BluetoothGatt.STATE_CONNECTED:
//                    mBluetoothLeService.disconnect(null);
//                    break;
                case BluetoothGatt.STATE_DISCONNECTED:
                    boolean ok = mBluetoothLeService.connect(mBluetoothDevice.getAddress());
                    if (!ok) {
                        setError("Connect failed");
                    }
                    break;
                default:
                    setError("Device busy (connecting/disconnecting)");
                    break;
            }
        }
    }

    private BleDeviceInfo createDeviceInfo(BluetoothDevice device, int rssi) {
        BleDeviceInfo deviceInfo = new BleDeviceInfo(device, rssi);

        return deviceInfo;
    }


    private boolean deviceInfoExists(String address) {
        for (int i = 0; i < mDeviceInfoList.size(); i++) {
            if (mDeviceInfoList.get(i).getBluetoothDevice().getAddress()
                    .equals(address)) {
                return true;
            }
        }
        return false;
    }

    private void addDevice(BleDeviceInfo device) {
        mNumDevs++;
        mDeviceInfoList.add(device);
        mScanView.notifyDataSetChanged();
        if (mNumDevs > 1)
            mScanView.setStatus(mNumDevs + " devices");
        else
            mScanView.setStatus("1 device");
    }

    private BleDeviceInfo findDeviceInfo(BluetoothDevice device) {
        for (int i = 0; i < mDeviceInfoList.size(); i++) {
            if (mDeviceInfoList.get(i).getBluetoothDevice().getAddress()
                    .equals(device.getAddress())) {
                return mDeviceInfoList.get(i);
            }
        }
        return null;
    }

    boolean checkDeviceFilter(String deviceName) {
        if (deviceName == null)
            return false;

        int n = mDeviceFilter.length;
        if (n > 0) {
            boolean found = false;
            for (int i = 0; i < n && !found; i++) {
                found = deviceName.equals(mDeviceFilter[i]);
            }
            return found;
        } else
            // Allow all devices if the device filter is empty
            return true;
    }

    public void onScanTimeout() {
        runOnUiThread(new Runnable() {
            public void run() {
                stopScan();
            }
        });
    }

    public void onConnectTimeout() {
        runOnUiThread(new Runnable() {
            public void run() {
                setError("Connection timed out");
            }
        });
        if (mConnIndex != NO_DEVICE) {
            mBluetoothLeService.disconnect(mBluetoothDevice.getAddress());
            mConnIndex = NO_DEVICE;
        }
    }

    public List<BleDeviceInfo> getConnectedDevices() {
        List<BluetoothDevice> devices = mBluetoothLeService.getConnectedDevices();

        List<BleDeviceInfo> connectedDevices = new ArrayList<>(5);

        for (BluetoothDevice device : devices) {
            connectedDevices.add(createDeviceInfo(device, BleDeviceInfo.NOSSI));
        }

        return connectedDevices;

    }

}
