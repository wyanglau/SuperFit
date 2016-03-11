/**************************************************************************************************
 * Filename:       Sensor.java
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
package ca.utoronto.ee1778.superfit.common;

import android.bluetooth.BluetoothGattCharacteristic;

import java.util.UUID;

import ca.utoronto.ee1778.superfit.utils.Point3D;

import static ca.utoronto.ee1778.superfit.common.SensorTagGatt.*;
import static java.lang.Math.pow;


/**
 * This enum encapsulates the differences amongst the sensors. The differences include UUID values and how to interpret the
 * characteristic-containing-measurement.
 */
public enum Sensor {


    MOVEMENT_ACC(UUID_MOV_SERV, UUID_MOV_DATA, UUID_MOV_CONF, (byte) 3) {
        @Override
        public Point3D convert(final byte[] value) {
            // Range 8G
            final float SCALE = (float) 4096.0;

            int x = (value[7] << 8) + value[6];
            int y = (value[9] << 8) + value[8];
            int z = (value[11] << 8) + value[10];
            return new Point3D(((x / SCALE) * -1), y / SCALE, ((z / SCALE) * -1));
        }
    },
    MOVEMENT_GYRO(UUID_MOV_SERV, UUID_MOV_DATA, UUID_MOV_CONF, (byte) 3) {
        @Override
        public Point3D convert(final byte[] value) {

            final float SCALE = (float) 128.0;

            int x = (value[1] << 8) + value[0];
            int y = (value[3] << 8) + value[2];
            int z = (value[5] << 8) + value[4];
            return new Point3D(x / SCALE, y / SCALE, z / SCALE);
        }
    },
    MOVEMENT_MAG(UUID_MOV_SERV, UUID_MOV_DATA, UUID_MOV_CONF, (byte) 3) {
        @Override
        public Point3D convert(final byte[] value) {
            final float SCALE = (float) (32768 / 4912);
            if (value.length >= 18) {
                int x = (value[13] << 8) + value[12];
                int y = (value[15] << 8) + value[14];
                int z = (value[17] << 8) + value[16];
                return new Point3D(x / SCALE, y / SCALE, z / SCALE);
            } else return new Point3D(0, 0, 0);
        }
    };

    /**
     * Gyroscope, Magnetometer, Barometer, IR temperature all store 16 bit two's complement values as LSB MSB, which cannot be directly parsed
     * as getIntValue(FORMAT_SINT16, offset) because the bytes are stored as little-endian.
     * <p/>
     * This function extracts these 16 bit two's complement values.
     */
    private static Integer shortSignedAtOffset(byte[] c, int offset) {
        Integer lowerByte = (int) c[offset] & 0xFF;
        Integer upperByte = (int) c[offset + 1]; // // Interpret MSB as signed
        return (upperByte << 8) + lowerByte;
    }

    private static Integer shortUnsignedAtOffset(byte[] c, int offset) {
        Integer lowerByte = (int) c[offset] & 0xFF;
        Integer upperByte = (int) c[offset + 1] & 0xFF;
        return (upperByte << 8) + lowerByte;
    }

    private static Integer twentyFourBitUnsignedAtOffset(byte[] c, int offset) {
        Integer lowerByte = (int) c[offset] & 0xFF;
        Integer mediumByte = (int) c[offset + 1] & 0xFF;
        Integer upperByte = (int) c[offset + 2] & 0xFF;
        return (upperByte << 16) + (mediumByte << 8) + lowerByte;
    }

    public void onCharacteristicChanged(BluetoothGattCharacteristic c) {
        throw new UnsupportedOperationException("Error: the individual enum classes are supposed to override this method.");
    }


    public Point3D convert(byte[] value) {
        throw new UnsupportedOperationException("Error: the individual enum classes are supposed to override this method.");
    }

    private final UUID service, data, config;
    private byte enableCode; // See getEnableSensorCode for explanation.
    public static final byte DISABLE_SENSOR_CODE = 0;
    public static final byte ENABLE_SENSOR_CODE = 1;
    public static final byte CALIBRATE_SENSOR_CODE = 2;

    /**
     * Constructor called by the Gyroscope and Accelerometer because it more than a boolean enable
     * code.
     */
    private Sensor(UUID service, UUID data, UUID config, byte enableCode) {
        this.service = service;
        this.data = data;
        this.config = config;
        this.enableCode = enableCode;
    }

    /**
     * Constructor called by all the sensors except Gyroscope
     */
    private Sensor(UUID service, UUID data, UUID config) {
        this.service = service;
        this.data = data;
        this.config = config;
        this.enableCode = ENABLE_SENSOR_CODE; // This is the sensor enable code for all sensors except the gyroscope
    }

    /**
     * @return the code which, when written to the configuration characteristic, turns on the sensor.
     */
    public byte getEnableSensorCode() {
        return enableCode;
    }

    public UUID getService() {
        return service;
    }

    public UUID getData() {
        return data;
    }

    public UUID getConfig() {
        return config;
    }

    public static Sensor getFromDataUuid(UUID uuid) {
        for (Sensor s : Sensor.values()) {
            if (s.getData().equals(uuid)) {
                return s;
            }
        }
        throw new RuntimeException("unable to find UUID.");
    }

}
