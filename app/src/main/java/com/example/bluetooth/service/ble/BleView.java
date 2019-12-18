package com.example.bluetooth.service.ble;

import android.bluetooth.BluetoothDevice;

import java.util.Set;

public interface BleView {
    public void updateDeviceList(Set<BluetoothDevice> deviceSet);
    public void addDevice(BluetoothDevice device);
    public void showNoAdaptor();
}
