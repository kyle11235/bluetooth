package com.example.bluetooth.service.ble;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.content.Context;

import com.example.bluetooth.service.ble.service.Service;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BleServer extends BleBase{

    // server
    private BluetoothGattServer gattServer;
    private Map<String, Service> serviceMap = new ConcurrentHashMap<>();
    private BluetoothDevice connectedDevice; // here only hold 1 device
    private Boolean isServing = false;

    // gattServerCallback == BtBase.Listener for server
    private final BluetoothGattServerCallback gattServerCallback = new BluetoothGattServerCallback() {
        @Override
        public void onConnectionStateChange(BluetoothDevice device, final int status, int newState) {
            super.onConnectionStateChange(device, status, newState);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (newState == BluetoothGatt.STATE_CONNECTED) {
                    connectedDevice = device;
                    log("Connected to device, address=" + device.getAddress());
                } else if (newState == BluetoothGatt.STATE_DISCONNECTED) {
                    connectedDevice = null;
                    log("Disconnected from device");
                }
            } else {
                connectedDevice = null;
                log("Error when connecting=" + status);
            }
        }

        @Override
        public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicReadRequest(device, requestId, offset, characteristic);
            log("Device tried to read characteristic, uuid=" + characteristic.getUuid());
            log("Value=" + Arrays.toString(characteristic.getValue())); // [b,b,b]
            if (offset != 0) {
                gattServer.sendResponse(device, requestId, BluetoothGatt.GATT_INVALID_OFFSET, offset,
                        /* value (optional) */ null);
                return;
            }
            gattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, characteristic.getValue());
        }

        @Override
        public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId,
                                                 BluetoothGattCharacteristic characteristic, boolean preparedWrite, boolean responseNeeded,
                                                 int offset, byte[] value) {
            super.onCharacteristicWriteRequest(device, requestId, characteristic, preparedWrite,
                    responseNeeded, offset, value);
            log("Device tried to read characteristic, uuid=" + characteristic.getUuid());
            log("Characteristic Write request, value=" + Arrays.toString(value));
            Service service = serviceMap.get(characteristic.getUuid().toString());
            int status = service.writeCharacteristic(characteristic, offset, value);
            if (responseNeeded) {
                gattServer.sendResponse(device, requestId, status,
                        /* No need to respond with an offset */ 0,
                        /* No need to respond with a value */ null);
            }
        }

        @Override
        public void onNotificationSent(BluetoothDevice device, int status) {
            super.onNotificationSent(device, status);
            log("Notification sent, Status=" + status);
        }

        // on read descriptor

        // on write descriptor

    };

    public BleServer(Context context, Listener listener){
        super(context, listener);
        if (enableAdapter()) {
            gattServer = manager.openGattServer(context, gattServerCallback);
            isServing = true;
        }
    }

    public Boolean addService(Service service) {
        if (this.isServing()) {
            Boolean result = gattServer.addService(service.getGattService());
            if(result){
                service.setBleServer(this);
                serviceMap.put(service.getServiceUUID().getUuid().toString(), service);
            }
            return result;
        }
        return false;
    }

    public void sendNotification(BluetoothGattCharacteristic characteristic) {
        boolean indicate = (characteristic.getProperties()
                & BluetoothGattCharacteristic.PROPERTY_INDICATE)
                == BluetoothGattCharacteristic.PROPERTY_INDICATE;
        // true for indication (acknowledge) and false for notification (unacknowledge).
        gattServer.notifyCharacteristicChanged(connectedDevice, characteristic, indicate);
    }

    public Boolean isServing() {
        return isServing;
    }

    public void close() {
        if (gattServer != null) {
            gattServer.close();
            isServing = false;
        }
        serviceMap.clear();
    }
}
