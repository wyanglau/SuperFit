/**************************************************************************************************
 * Filename:       SensorTagAccelerometerProfile.java
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
import android.view.View;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import ca.utoronto.ee1778.superfit.common.BluetoothLeService;
import ca.utoronto.ee1778.superfit.common.GenericBluetoothProfile;
import ca.utoronto.ee1778.superfit.common.Sensor;
import ca.utoronto.ee1778.superfit.common.SensorTagGatt;
import ca.utoronto.ee1778.superfit.utils.Point3D;

public class SensorTagAccelerometerProfile extends GenericBluetoothProfile {
    public SensorTagAccelerometerProfile(Context con, BluetoothDevice device, BluetoothGattService service, BluetoothLeService controller, BluetoothGatt mBluetoothGatt) {
        super(con, device, service, controller, mBluetoothGatt);

        List<BluetoothGattCharacteristic> characteristics = this.mBTService.getCharacteristics();
        for (BluetoothGattCharacteristic c : characteristics) {

            if (c.getUuid().toString().equals(SensorTagGatt.UUID_ACC_DATA.toString())) {
                this.dataC = c;
            }
            if (c.getUuid().toString().equals(SensorTagGatt.UUID_ACC_CONF.toString())) {
                this.configC = c;
            }
            if (c.getUuid().toString().equals(SensorTagGatt.UUID_ACC_PERI.toString())) {
                this.periodC = c;
            }
        }



    }

    public static boolean isCorrectService(BluetoothGattService service) {
        if ((service.getUuid().toString().compareTo(SensorTagGatt.UUID_ACC_SERV.toString())) == 0) {
            return true;
        } else return false;
    }

    @Override
    public void didUpdateValueForCharacteristic(BluetoothGattCharacteristic c,View view) {
//        if (c.equals(this.dataC)) {
//            Point3D v = Sensor.ACCELEROMETER.convert(this.dataC.getValue());
//            if (this.tRow.config == false)
//                this.tRow.value.setText(String.format("X:%.2fG, Y:%.2fG, Z:%.2fG", v.x, v.y, v.z));
//            this.tRow.sl1.addValue((float) v.x);
//            this.tRow.sl2.addValue((float) v.y);
//            this.tRow.sl3.addValue((float) v.z);
//        }
    }

}

