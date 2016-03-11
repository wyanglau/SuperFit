/**************************************************************************************************
 * Filename:       SensorTagMovementProfile.java
 * <p/>
 * Copyright (c) 2013 - 2015 Texas Instruments Incorporated
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
package ca.utoronto.ee1778.superfit.profile;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.List;
import java.util.UUID;

import ca.utoronto.ee1778.superfit.common.BluetoothLeService;
import ca.utoronto.ee1778.superfit.common.GattInfo;
import ca.utoronto.ee1778.superfit.common.GenericBluetoothProfile;
import ca.utoronto.ee1778.superfit.common.Sensor;
import ca.utoronto.ee1778.superfit.common.SensorTagGatt;
import ca.utoronto.ee1778.superfit.service.ExerciseService;
import ca.utoronto.ee1778.superfit.utils.Point3D;


public class SensorTagMovementProfile extends GenericBluetoothProfile {


    private int SAMPLING_PERIOD = 500;
    private int SAMPLING_PROGREE = SAMPLING_PERIOD / 10;

    private ExerciseService exerciseService;

    public SensorTagMovementProfile(Context con, BluetoothDevice device, BluetoothGattService service, BluetoothLeService controller, BluetoothGatt mBluetoothGatt) {
        super(con, device, service, controller, mBluetoothGatt);
        exerciseService = new ExerciseService(con);
        List<BluetoothGattCharacteristic> characteristics = this.mBTService.getCharacteristics();

        for (BluetoothGattCharacteristic c : characteristics) {
            if (c.getUuid().toString().equals(SensorTagGatt.UUID_MOV_DATA.toString())) {
                this.dataC = c;
            }
            if (c.getUuid().toString().equals(SensorTagGatt.UUID_MOV_CONF.toString())) {
                this.configC = c;
            }
            if (c.getUuid().toString().equals(SensorTagGatt.UUID_MOV_PERI.toString())) {
                this.periodC = c;
            }
        }


    }

    public static boolean isCorrectService(BluetoothGattService service) {
        if ((service.getUuid().toString().compareTo(SensorTagGatt.UUID_MOV_SERV.toString())) == 0) {
            return true;
        } else return false;
    }

    @Override
    public void enableService() {
        byte b[] = new byte[]{0x7F, 0x00};
        // if (row.WOS.isChecked()) b[0] = (byte)0xFF;
        int error = mBTLeService.writeCharacteristic(this.configC, b, mBluetoothGatt);
        if (error != 0) {
            if (this.configC != null)
                Log.d("SensorTagMvementProfile", "Sensor config failed: " + this.configC.getUuid().toString() + " Error: " + error);
        }
        error = this.mBTLeService.setCharacteristicNotification(this.dataC, true, mBluetoothGatt);
        if (error != 0) {
            if (this.dataC != null)
                Log.d("SensorTagMvementProfile", "Sensor notification enable failed: " + this.configC.getUuid().toString() + " Error: " + error);
        }

        this.periodWasUpdated(SAMPLING_PERIOD);
        this.isEnabled = true;
    }

    @Override
    public void disableService() {
        int error = mBTLeService.writeCharacteristic(this.configC, new byte[]{0x00, 0x00}, mBluetoothGatt);
        if (error != 0) {
            if (this.configC != null)
                Log.d("SensorTagMvementProfile", "Sensor config failed: " + this.configC.getUuid().toString() + " Error: " + error);
        }
        error = this.mBTLeService.setCharacteristicNotification(this.dataC, false, mBluetoothGatt);
        if (error != 0) {
            if (this.dataC != null)
                Log.d("SensorTagMvementProfile", "Sensor notification disable failed: " + this.configC.getUuid().toString() + " Error: " + error);
        }
        this.isEnabled = false;
    }

    public void didWriteValueForCharacteristic(BluetoothGattCharacteristic c) {

    }

    public void didReadValueForCharacteristic(BluetoothGattCharacteristic c) {

    }

    public void didUpdateValueForCharacteristic(BluetoothGattCharacteristic c, View movementTextview, View resultImage
    ) {
        byte[] value = c.getValue();
        if (c.equals(this.dataC)) {
            System.out.println("Ryan:Movement:-------------------Getting Movement data----------------------------------");
            Point3D v;
            v = Sensor.MOVEMENT_ACC.convert(value);

            float X = (float) v.x;
            float Y = (float) v.y;
            float Z = (float) v.z;
            double current_angle = exerciseService.calculatePitch(v.x, v.y, v.z);

            ((TextView) movementTextview).setText(String.valueOf(current_angle));

            System.out.println("Ryan:approximate:new:angle: " + current_angle);
            System.out.println("Ryan:approximate:angle: " + Math.toDegrees((Math.asin(Double.valueOf(v.y) > 1 ? 1 : Double.valueOf(v.y)))));
            System.out.println("Ryan:Movement:Acc:x=" + v.x + " y=" + v.y + " z=" + v.z);

        }
    }


//    @Override
//    public Map<String,String> getMQTTMap() {
//        System.out.println("Ryan:Movement:------------------Getting MQTTMAP-----------------------------------");
//        Point3D v = Sensor.MOVEMENT_ACC.convert(this.dataC.getValue());
//        Map<String,String> map = new HashMap<String, String>();
//        map.put("acc_x", String.format("%.2f", v.x));
//        map.put("acc_y", String.format("%.2f", v.y));
//        map.put("acc_z", String.format("%.2f", v.z));
//        v = Sensor.MOVEMENT_GYRO.convert(this.dataC.getValue());
//        map.put("gyro_x", String.format("%.2f", v.x));
//        map.put("gyro_y", String.format("%.2f", v.y));
//        map.put("gyro_z", String.format("%.2f", v.z));
//        v = Sensor.MOVEMENT_MAG.convert(this.dataC.getValue());
//        map.put("compass_x", String.format("%.2f", v.x));
//        map.put("compass_y", String.format("%.2f", v.y));
//        map.put("compass_z", String.format("%.2f", v.z));
//        System.out.println("Ryan:Movement:MQTTMAP" + map.toString());
//        System.out.println("Ryan:Movement:-----------------------------------------------------");
//        return map;
//    }
}
