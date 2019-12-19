package com.example.bluetooth.service.ble;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;

import com.example.bluetooth.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class BleClient extends BleBase {

    public final static UUID HEART_RATE_MEASUREMENT_UUID = UUID.fromString("0000180D-0000-1000-8000-00805f9b34fb");

    private BluetoothGatt gattClient;

    // gattCallback == BtBase.Listener for client, to get read / write result
    // convert gattCallback to Listener who only has onBlueEvent for simplicity
    private BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                log("Connected to GATT server");
                listener.onBlueEvent(Listener.CONNECTED, "Connected to GATT server");

                log("Attempting to start service discovery=" + discoverServices());
                listener.onBlueEvent(Listener.DISCOVERING, "discovering services");

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                log("Disconnected from GATT server");
                listener.onBlueEvent(Listener.DISCONNECTED, "Disconnected from GATT server");
            }
        }

        @Override
        // New services discovered
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                log("new services discovered");
                listener.onBlueEvent(Listener.DISCOVERED, "new services discovered");
            } else {
                log("onServicesDiscovered=" + status);
                listener.onBlueEvent(Listener.DISCOVERED, "onServicesDiscovered=" + status);
            }
        }

        @Override
        // Result of a characteristic read operation
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                log("characteristic read result=" + characteristic.toString());
                read(characteristic);
            }
        }

        @Override
        // write result
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            // xxx
        }

        @Override
        // get notified
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            // xxx
        }

    };

    public BleClient(Context context, Listener listener) {
        super(context, listener);
    }

    public void connect(BluetoothDevice device, Boolean autoReconnect) {
        gattClient = device.connectGatt(context, autoReconnect, gattCallback);
        log("connecting to GATT server");
    }

    public Boolean discoverServices() {
        return gattClient.discoverServices();
    }

    // read started by client
    public Boolean readCharacteristic(BluetoothGattCharacteristic characteristic) {
        log("readCharacteristic");
        if (gattClient == null) {
            return false;
        }
        return gattClient.readCharacteristic(characteristic);
    }

    // read result from server
    private void read(final BluetoothGattCharacteristic characteristic) {
        // This is special handling for the Heart Rate Measurement profile. Data
        // parsing is carried out as per profile specifications.
        if (HEART_RATE_MEASUREMENT_UUID.equals(characteristic.getUuid())) {

            int format = BluetoothGattCharacteristic.FORMAT_UINT8;
            int propertyFlag = characteristic.getProperties();

            // xxxx xxxx1 & 0000 0001 != 0 -> it's 16 bit
            if ((propertyFlag & BleBase.FLAG_FORMAT_UINT16) != 0) {
                format = BluetoothGattCharacteristic.FORMAT_UINT16;
                log("Heart rate format UINT16.");
            }

            final int heartRate = characteristic.getIntValue(format, 1);
            log(String.format("Received heart rate: %d", heartRate));
            listener.onBlueEvent(Listener.READ_CHARACTERISTIC, heartRate);
        } else {
            // For all other profiles, writes the data formatted in HEX.
            final byte[] data = characteristic.getValue();
            if (data != null && data.length > 0) {
                final StringBuilder sb = new StringBuilder(data.length);
                for (byte byteChar : data) {
                    sb.append(String.format("%02X ", byteChar));
                }
                log("string data=" + new String(data));
                log("string data=" + sb.toString()); // hex(B) hex(B) hex(B) -> BB BB BB
                listener.onBlueEvent(Listener.READ_CHARACTERISTIC, new String(data));
            }
        }
    }

    // Receive GATT notifications
    public Boolean setCharacteristicNotification(BluetoothGattCharacteristic characteristic, Boolean enabled) {
        // -> onCharacteristicChanged
        return gattClient.setCharacteristicNotification(characteristic, enabled);
    }

    public void displayDetails() {

        if (gattClient == null) {
            return;
        }


        List<BluetoothGattService> serviceList = gattClient.getServices();
        log("displayDetails, service size=" + serviceList.size());

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : serviceList) {
            String uuid = gattService.getUuid().toString();
            log("service uuid=" + uuid);

            List<BluetoothGattCharacteristic> characteristicsList = gattService.getCharacteristics();
            log("characteristic size=" + characteristicsList.size());

            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic characteristic : characteristicsList) {
                uuid = characteristic.getUuid().toString();
                log("characteristic uuid=" + uuid);
                this.read(characteristic);
            }
        }
    }


    public void close() {
        if (gattClient == null) {
            return;
        }
        gattClient.close();
        gattClient = null;
    }

}
