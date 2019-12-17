package com.example.bluetooth.service.ble.service;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattService;
import android.os.ParcelUuid;

import com.example.bluetooth.service.ble.BleCentral;
import com.example.bluetooth.service.ble.BleServer;

public interface Service {

    public ParcelUuid getServiceUUID();
    public BluetoothGattService getGattService();
    public int writeCharacteristic(BluetoothGattCharacteristic characteristic, int offset, byte[] value);
    public void setBleServer(BleServer bleServer);
    public void sendNotification();
}
