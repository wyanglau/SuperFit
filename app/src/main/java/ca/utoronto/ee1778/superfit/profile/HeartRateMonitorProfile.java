package ca.utoronto.ee1778.superfit.profile;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import ca.utoronto.ee1778.superfit.common.BluetoothLeService;
import ca.utoronto.ee1778.superfit.common.GenericBluetoothProfile;
import ca.utoronto.ee1778.superfit.service.ExerciseService;


/**
 * Created by liuwyang on 2016-02-25.
 */
public class HeartRateMonitorProfile extends GenericBluetoothProfile {

    private static final String TAG = "HeartRateMonitorProfile";
    private static final String UUID_SERVICE = "0000180d-0000-1000-8000-00805f9b34fb";
    private static final String UUID_DEVICE_NAME = "00002a00-0000-1000-8000-00805f9b34fb";
    private static final String UUID_DATA = "00002a37-0000-1000-8000-00805f9b34fb";
    // private static final String UUID_DEVICE_NAME = "00002a05-0000-1000-8000-00805f9b34fb";
    private static String UUID_CHAR_BODY_LOCATION = "00002a38-0000-1000-8000-00805f9b34fb";
    private static String UUID_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
    private static final String UUID_PPCP = "00002a04-0000-1000-8000-00805f9b34fb";
    private ExerciseService exerciseService;

    public HeartRateMonitorProfile(Context con, BluetoothDevice device, BluetoothGattService service, BluetoothLeService controller, BluetoothGatt mBluetoothGatt, ExerciseService exerciseService) {
        super(con, device, service, controller, mBluetoothGatt);

        this.exerciseService = exerciseService;
        List<BluetoothGattCharacteristic> characteristics = this.mBTService.getCharacteristics();
        for (BluetoothGattCharacteristic c : characteristics) {
            if (c.getUuid().toString().equals(UUID_DATA.toString())) {
                this.dataC = c;
            }
            if (c.getUuid().toString().equals(UUID_CHAR_BODY_LOCATION.toString())) {
                this.configC = c;
            }
            if (c.getUuid().toString().equals(UUID_PPCP.toString())) {
                this.periodC = c;
            }
        }

    }

    @Override
    public void didUpdateValueForCharacteristic(BluetoothGattCharacteristic c, View view) {

        if (c.equals(this.dataC)) {
            float[] value = parse(c);
            ((TextView) view).setText(String.valueOf(value[0]));
           exerciseService.setHr(value[0]);


        }

    }

    public float[] parse(BluetoothGattCharacteristic c) {

        double heartRate = extractHeartRate(c);
        double contact = extractContact(c);
        double energy = extractEnergyExpended(c);
        Integer[] interval = extractBeatToBeatInterval(c);

        float[] result = null;
        if (interval != null) {
            result = new float[interval.length + 1];
        } else {
            result = new float[2];
            result[1] = -1.0f;
        }
        result[0] = (float) heartRate;

        if (interval != null) {
            for (int i = 0; i < interval.length; i++) {
                result[i + 1] = interval[i].floatValue();
            }
        }

        return result;
    }

    private static double extractHeartRate(
            BluetoothGattCharacteristic characteristic) {

        int flag = characteristic.getProperties();
        Log.d(TAG, "Heart rate flag: " + flag);
        int format = -1;
        // Heart rate bit number format
        if ((flag & 0x01) != 0) {
            format = BluetoothGattCharacteristic.FORMAT_UINT16;
            Log.d(TAG, "Heart rate format UINT16.");
        } else {
            format = BluetoothGattCharacteristic.FORMAT_UINT8;
            Log.d(TAG, "Heart rate format UINT8.");
        }
        final int heartRate = characteristic.getIntValue(format, 1);
        Log.d(TAG, String.format("Received heart rate: %d", heartRate));
        return heartRate;
    }

    private static double extractEnergyExpended(
            BluetoothGattCharacteristic characteristic) {

        int flag = characteristic.getProperties();
        int format = -1;
        // Energy calculation status
        if ((flag & 0x08) != 0) {
            Log.d(TAG, "Heart rate energy calculation exists.");
        } else {
            Log.d(TAG, "Heart rate energy calculation doesn't exists.");
        }
        //final int heartRate = characteristic.getIntValue(format, 1);
        //Log.d(TAG, String.format("Received heart rate: %d", heartRate));
        return 0.0d;
    }

    private static Integer[] extractBeatToBeatInterval(
            BluetoothGattCharacteristic characteristic) {

        int flag = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
        int format = -1;
        int energy = -1;
        int offset = 1; // This depends on hear rate value format and if there is energy data
        int rr_count = 0;

        if ((flag & 0x01) != 0) {
            format = BluetoothGattCharacteristic.FORMAT_UINT16;
            Log.d(TAG, "Heart rate format UINT16.");
            offset = 3;
        } else {
            format = BluetoothGattCharacteristic.FORMAT_UINT8;
            Log.d(TAG, "Heart rate format UINT8.");
            offset = 2;
        }
        if ((flag & 0x08) != 0) {
            // calories present
            energy = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, offset);
            offset += 2;
            Log.d(TAG, "Received energy: {}" + energy);
        }
        if ((flag & 0x16) != 0) {
            // RR stuff.
            Log.d(TAG, "RR stuff found at offset: " + offset);
            Log.d(TAG, "RR length: " + (characteristic.getValue()).length);
            rr_count = ((characteristic.getValue()).length - offset) / 2;
            Log.d(TAG, "RR length: " + (characteristic.getValue()).length);
            Log.d(TAG, "rr_count: " + rr_count);
            if (rr_count > 0) {
                Integer[] mRr_values = new Integer[rr_count];
                for (int i = 0; i < rr_count; i++) {
                    mRr_values[i] = characteristic.getIntValue(
                            BluetoothGattCharacteristic.FORMAT_UINT16, offset);
                    offset += 2;
                    Log.d(TAG, "Received RR: " + mRr_values[i]);
                }
                return mRr_values;
            }
        }
        Log.d(TAG, "No RR data on this update: ");
        return null;
    }

    private static double extractContact(
            BluetoothGattCharacteristic characteristic) {

        int flag = characteristic.getProperties();
        int format = -1;
        // Sensor contact status
        if ((flag & 0x02) != 0) {
            Log.d(TAG, "Heart rate sensor contact info exists");
            if ((flag & 0x04) != 0) {
                Log.d(TAG, "Heart rate sensor contact is ON");
            } else {
                Log.d(TAG, "Heart rate sensor contact is OFF");
            }
        } else {
            Log.d(TAG, "Heart rate sensor contact info doesn't exists");
        }
        //final int heartRate = characteristic.getIntValue(format, 1);
        //Log.d(TAG, String.format("Received heart rate: %d", heartRate));
        return 0.0d;
    }

    public static boolean isCorrectService(BluetoothGattService service) {
        if ((service.getUuid().toString().compareTo(UUID_SERVICE)) == 0) {
            return true;
        } else return false;
    }
}
