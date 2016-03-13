/**************************************************************************************************
 * Filename:       ScanView.java
 * <p/>
 * Copyright (c) 2013 - 2014 Texas Instruments Incorporated
 * <p/>
 * All rights reserved not granted herein.
 * Limited License.
 * <p/>
 * Texas Instruments Incorporated grants a world-wide, royalty-free,
 * non-exclusive license under copyrights and patents it now or hereafter
 * owns or controls to make, have made, use, import, offer to sell and sell ("Utilize")
 * this software subject to the terms herein.  With respect to the foregoing patent
 * license, such license is granted  solely to the extent that any such patent is necessary
 * to Utilize the software alone.  The patent license shall not apply to any combinations which
 * include this software, other than combinations with devices manufactured by or for TI ('TI Devices').
 * No hardware patent is licensed hereunder.
 * <p/>
 * Redistributions must preserve existing copyright notices and reproduce this license (including the
 * above copyright notice and the disclaimer and (if applicable) source code license limitations below)
 * in the documentation and/or other materials provided with the distribution
 * <p/>
 * Redistribution and use in binary form, without modification, are permitted provided that the following
 * conditions are met:
 * <p/>
 * No reverse engineering, decompilation, or disassembly of this software is permitted with respect to any
 * software provided in binary form.
 * any redistribution and use are licensed by TI for use only with TI Devices.
 * Nothing shall obligate TI to provide you with source code for the software licensed and provided to you in object code.
 * <p/>
 * If software source code is provided to you, modification and redistribution of the source code are permitted
 * provided that the following conditions are met:
 * <p/>
 * any redistribution and use of the source code, including any resulting derivative works, are licensed by
 * TI for use only with TI Devices.
 * any redistribution and use of any object code compiled from the source code and any resulting derivative
 * works, are licensed by TI for use only with TI Devices.
 * <p/>
 * Neither the name of Texas Instruments Incorporated nor the names of its suppliers may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 * <p/>
 * DISCLAIMER.
 * <p/>
 * THIS SOFTWARE IS PROVIDED BY TI AND TI'S LICENSORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL TI AND TI'S LICENSORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 **************************************************************************************************/
package ca.utoronto.ee1778.superfit.controller;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import ca.utoronto.ee1778.superfit.R;
import ca.utoronto.ee1778.superfit.common.BleDeviceInfo;
import ca.utoronto.ee1778.superfit.utils.CustomTimer;
import ca.utoronto.ee1778.superfit.utils.CustomTimerCallback;


public class ScanView extends Fragment {
    // private static final String TAG = "ScanView";
    private final int SCAN_TIMEOUT = 10; // Seconds
    private final int CONNECT_TIMEOUT = 20; // Seconds
    private BluetoothSearchActivity mActivity = null;

    private DeviceListAdapter mDeviceAdapter = null;
    private TextView mEmptyMsg;
    private TextView mStatus;
    private Button mBtnScan = null;
    private ListView mConnectedListView = null;
    private ListView mDeviceListView = null;
    private boolean mBusy;

    private CustomTimer mScanTimer = null;
    private CustomTimer mConnectTimer = null;
    @SuppressWarnings("unused")
    private CustomTimer mStatusTimer;
    private Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Log.i(TAG, "onCreateView");
        System.out.println("Ryan: start to initialize the ScanView");
        // The last two arguments ensure LayoutParams are inflated properly.
        View view = inflater.inflate(R.layout.fragment_scan, container, false);

        mActivity = (BluetoothSearchActivity) getActivity();
        mContext = mActivity.getApplicationContext();

        // Initialize widgets
        mStatus = (TextView) view.findViewById(R.id.status);
        mBtnScan = (Button) view.findViewById(R.id.btn_scan);

        mConnectedListView = (ListView) view.findViewById(R.id.connected_device_list);
        mConnectedListView.setClickable(false);


        mDeviceListView = (ListView) view.findViewById(R.id.device_list);
        mDeviceListView.setClickable(true);
        mDeviceListView.setOnItemClickListener(mDeviceClickListener);
        mEmptyMsg = (TextView) view.findViewById(R.id.no_device);
        mBusy = false;
        System.out.println("Ryan: finish initializing the ScanView");
        // Alert parent activity


        mActivity.onScanViewReady(view);

        return view;
    }

    public void showConnected() {
        List<BleDeviceInfo> devices = mActivity.getConnectedDevices();
        if(!devices.isEmpty()){
            mConnectedListView.setVisibility(View.VISIBLE);
        }

        DeviceListAdapter connectedAdapter = new DeviceListAdapter(mActivity, devices, true);
        mConnectedListView.setAdapter(connectedAdapter);
    }

    @Override
    public void onDestroy() {
        // Log.i(TAG, "onDestroy");
        super.onDestroy();
    }

    public void setStatus(String txt) {
        mStatus.setText(txt);
        mStatus.setTextAppearance(mContext, R.style.statusStyle_Success);
    }

    public void setStatus(String txt, int duration) {
        setStatus(txt);
        mStatusTimer = new CustomTimer(null, duration, mClearStatusCallback);
    }

    void setError(String txt) {
        setBusy(false);
        stopTimers();
        mStatus.setText(txt);
        mStatus.setTextAppearance(mContext, R.style.statusStyle_Failure);
    }

    public void notifyDataSetChanged() {
        List<BleDeviceInfo> deviceList = mActivity.getDeviceInfoList();
        if (mDeviceAdapter == null) {
            mDeviceAdapter = new DeviceListAdapter(mActivity, deviceList, false);
        }
        mDeviceListView.setAdapter(mDeviceAdapter);
        mDeviceAdapter.notifyDataSetChanged();
        if (deviceList.size() > 0) {
            mEmptyMsg.setVisibility(View.GONE);
        } else {
            mEmptyMsg.setVisibility(View.VISIBLE);
        }
    }

    void setBusy(boolean f) {
        if (f != mBusy) {
            mBusy = f;
            if (!mBusy) {
                stopTimers();
                mBtnScan.setEnabled(true);    // Enable in case of connection timeout
                mDeviceAdapter.notifyDataSetChanged(); // Force enabling of all Connect buttons
            }
            //	mActivity.showBusyIndicator(f);
        }
    }

    void updateGui(boolean scanning) {
        if (mBtnScan == null)
            return; // UI not ready

        setBusy(scanning);

        if (scanning) {
            // Indicate that scanning has started
            mScanTimer = new CustomTimer(null, SCAN_TIMEOUT, mPgScanCallback);
            mBtnScan.setText("Stop");
            // mBtnScan.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_action_cancel, 0);
            mStatus.setTextAppearance(mContext, R.style.statusStyle_Busy);
            mStatus.setText("Scanning...");
            mEmptyMsg.setText("No device found. Try turn on your bluetooth devices and try again.");
            mActivity.updateGuiState();
        } else {
            // Indicate that scanning has stopped
            mStatus.setTextAppearance(mContext, R.style.statusStyle_Success);
            mBtnScan.setText("Scan");
            //mBtnScan.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_action_refresh, 0);
            mEmptyMsg.setText("You can start scanning by pressing the scan button.");
            mActivity.setProgressBarIndeterminateVisibility(false);
            mDeviceAdapter.notifyDataSetChanged();
        }
    }

    // Listener for device list
    private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
            // Log.d(TAG,"item click");
            mConnectTimer = new CustomTimer(null, CONNECT_TIMEOUT, mPgConnectCallback);
            mBtnScan.setEnabled(false);
            mDeviceAdapter.notifyDataSetChanged(); // Force disabling of all Connect buttons
            mActivity.onDeviceClick(pos);
        }
    };

    // Listener for progress timer expiration
    private CustomTimerCallback mPgScanCallback = new CustomTimerCallback() {
        public void onTimeout() {
            mActivity.onScanTimeout();
        }

        public void onTick(int i) {
            //mActivity.refreshBusyIndicator();
        }
    };

    // Listener for connect/disconnect expiration
    private CustomTimerCallback mPgConnectCallback = new CustomTimerCallback() {
        public void onTimeout() {
            mActivity.onConnectTimeout();
            mBtnScan.setEnabled(true);
        }

        public void onTick(int i) {
            //mActivity.refreshBusyIndicator();
        }
    };

    // Listener for connect/disconnect expiration
    private CustomTimerCallback mClearStatusCallback = new CustomTimerCallback() {
        public void onTimeout() {
            mActivity.runOnUiThread(new Runnable() {
                public void run() {
                    setStatus("");
                }
            });
            mStatusTimer = null;
        }

        public void onTick(int i) {
        }
    };

    private void stopTimers() {
        if (mScanTimer != null) {
            mScanTimer.stop();
            mScanTimer = null;
        }
        if (mConnectTimer != null) {
            mConnectTimer.stop();
            mConnectTimer = null;
        }
    }

    //
    // CLASS DeviceAdapter: handle device list
    //
    @SuppressLint("InflateParams")
    class DeviceListAdapter extends BaseAdapter {
        private List<BleDeviceInfo> mDevices;
        private LayoutInflater mInflater;
        private boolean isConnected;

        public DeviceListAdapter(Context context, List<BleDeviceInfo> devices, boolean isConnected) {
            mInflater = LayoutInflater.from(context);
            mDevices = devices;
            this.isConnected = isConnected;
        }

        public int getCount() {
            return mDevices.size();
        }

        public Object getItem(int position) {
            return mDevices.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewGroup vg;

            if (convertView != null) {
                vg = (ViewGroup) convertView;
            } else {
                vg = (ViewGroup) mInflater.inflate(R.layout.element_device, null);
            }

            BleDeviceInfo deviceInfo = mDevices.get(position);
            BluetoothDevice device = deviceInfo.getBluetoothDevice();
            int rssi = deviceInfo.getRssi();
            String name;
            name = device.getName();
            if (name == null) {
                name = new String("Unknown device");
            }
            String descr = null;
            if (rssi == BleDeviceInfo.NOSSI) {
                descr = name + "\n" + device.getAddress();
            } else {
                descr = name + "\n" + device.getAddress() + "\nRssi: " + rssi + " dBm";
            }


            ((TextView) vg.findViewById(R.id.descr)).

                    setText(descr);

            ImageView iv = (ImageView) vg.findViewById(R.id.devImage);
            if (name.equals("SensorTag2") || name.equals("CC2650 SensorTag"))
                iv.setImageResource(R.drawable.icon_ti);
            else

            {
                iv.setImageResource(R.drawable.icon_wahoo);
            }

            ImageView imageView = (ImageView) vg.findViewById(R.id.ble_stat);
            if (isConnected)

            {
                imageView.setImageResource(R.drawable.ic_bluetooth_connected_black_24dp);
            }
            // Disable connect button when connecting or connected
//      Button bv = (Button)vg.findViewById(R.id.devImage);
//      bv.setEnabled(mConnectTimer == null);

            return vg;
        }
    }

}
