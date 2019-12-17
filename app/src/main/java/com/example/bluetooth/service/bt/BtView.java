package com.example.bluetooth.service.bt;

import android.bluetooth.BluetoothDevice;

import java.util.Set;

public interface BtView {
    public void updateDeviceList(Set<BluetoothDevice> boundedDevices);
    public void addDevice(BluetoothDevice device);
    public void showNoAdaptor();
}
