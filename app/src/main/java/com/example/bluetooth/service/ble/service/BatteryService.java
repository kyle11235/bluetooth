package com.example.bluetooth.service.ble.service;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.os.Build;
import android.os.ParcelUuid;

import androidx.annotation.RequiresApi;

import com.example.bluetooth.service.ble.BleServer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.UUID;

public class BatteryService implements Service{

    // official -> 16 bit + constant tail
    // non-official -> 128 bit

    // e.g. official heart rate service
    // 0X180D -> 4x4 = 16 bit
    // 0000180D-0000-1000-8000-00805f9b34fb -> 32x4 = 128 bit
    public static final UUID BATTERY_SERVICE_UUID = UUID.fromString("0000180F-0000-1000-8000-00805f9b34fb");
    public static final UUID BATTERY_LEVEL_UUID = UUID.fromString("00002A19-0000-1000-8000-00805f9b34fb");
    private static final int INITIAL_BATTERY_LEVEL = 50;
    private static final int MIN_BATTERY_LEVEL = 0;
    private static final int MAX_BATTERY_LEVEL = 100;

    // GATT
    private BluetoothGattService gattService;
    private BluetoothGattCharacteristic gattCharacteristic;
    private BleServer bleServer;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public BatteryService() {

        // service
        gattService = new BluetoothGattService(BATTERY_SERVICE_UUID,
                BluetoothGattService.SERVICE_TYPE_PRIMARY);

        // character
        gattCharacteristic = new BluetoothGattCharacteristic(BATTERY_LEVEL_UUID,
                BluetoothGattCharacteristic.PROPERTY_READ
                        | BluetoothGattCharacteristic.PROPERTY_WRITE
                        | BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE
                        | BluetoothGattCharacteristic.PROPERTY_NOTIFY,
                BluetoothGattCharacteristic.PERMISSION_READ
                        | BluetoothGattCharacteristic.PERMISSION_WRITE);

        gattService.addCharacteristic(gattCharacteristic);

        // init value
        this.setBatteryLevel(INITIAL_BATTERY_LEVEL);

    }

    // publish service
    @Override
    public BluetoothGattService getGattService() {
        return gattService;
    }

    @Override
    public ParcelUuid getServiceUUID() {
        return new ParcelUuid(BATTERY_SERVICE_UUID);
    }

    // update by GATT client
    @Override
    public int writeCharacteristic(BluetoothGattCharacteristic characteristic, int offset, byte[] value) {
        if (offset != 0) {
            return BluetoothGatt.GATT_INVALID_OFFSET;
        }
        // Measurement Interval is a 16bit gattCharacteristic
        if (value.length != 2) {
            return BluetoothGatt.GATT_INVALID_ATTRIBUTE_LENGTH;
        }
        ByteBuffer byteBuffer = ByteBuffer.wrap(value);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        final int valueInt = byteBuffer.getShort();
        if (!isValidValue(valueInt)) {
            return BluetoothGatt.GATT_FAILURE;
        }
        this.setBatteryLevel(valueInt);
        return BluetoothGatt.GATT_SUCCESS;
    }

    @Override
    public void setBleServer(BleServer bleServer){
        this.bleServer = bleServer;
    }

    @Override
    public void sendNotification() {
        if(bleServer != null){
            bleServer.sendNotification(gattCharacteristic);
        }
    }

    // update by GATT server
    public void setBatteryLevel(int level) {
        gattCharacteristic.setValue(level, BluetoothGattCharacteristic.FORMAT_UINT8, /* offset */ 0);
        this.sendNotification();
    }

    private boolean isValidValue(int value) {
        return (value >= MIN_BATTERY_LEVEL) && (value <= MAX_BATTERY_LEVEL);
    }

}
