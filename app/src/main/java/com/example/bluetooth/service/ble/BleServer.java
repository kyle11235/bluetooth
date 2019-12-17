package com.example.bluetooth.service.ble;

import android.bluetooth.BluetoothGattCharacteristic;

public interface BleServer {

    public void sendNotification(BluetoothGattCharacteristic characteristic);
}
