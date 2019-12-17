package com.example.bluetooth.service.ble;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.ParcelUuid;
import android.util.Log;

import com.example.bluetooth.service.ble.service.BatteryService;
import com.example.bluetooth.service.ble.service.Service;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import android.os.Handler;

public class BleCentral extends BleBase implements BleServer{

    // server
    private BluetoothGattServer gattServer;
    private Map<String, Service> serviceMap = new ConcurrentHashMap<>();
    private BluetoothDevice connectedDevice; // here only hold 1 device
    private Boolean isServing = false;

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
                log("Error when connecting: " + status);
            }
        }

        @Override
        public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicReadRequest(device, requestId, offset, characteristic);
            log("Device tried to read characteristic, uuid=" + characteristic.getUuid());
            log("Value=" + Arrays.toString(characteristic.getValue()));
            if (offset != 0) {
                gattServer.sendResponse(device, requestId, BluetoothGatt.GATT_INVALID_OFFSET, offset,
                        /* value (optional) */ null);
                return;
            }
            gattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, characteristic.getValue());
        }

        @Override
        public void onNotificationSent(BluetoothDevice device, int status) {
            super.onNotificationSent(device, status);
            log("Notification sent, Status=" + status);
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

        // on read descriptor

        // on write descriptor

    };

    // scanner
    private BluetoothLeScanner scanner;
    private Handler handler = new Handler();
    public interface Listener {
        void onFoundDevice(BluetoothDevice device);
    }

    private Listener listener;
    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            log("onScanResult");

            if (result == null) {
                return;
            }
            BluetoothDevice device = result.getDevice();
            if (device == null) {
                return;
            }

            listener.onFoundDevice(device);
            log("device name=" + device.getName() + ", address=" + device.getAddress());

            ScanRecord scanRecord = result.getScanRecord();
            List<ParcelUuid> serviceList = scanRecord.getServiceUuids();
            if (serviceList != null) {
                log("serviceList size=" + serviceList.size());
                for (ParcelUuid serviceID : serviceList) {
                    log("service ID=" + serviceID);

                    byte[] data = scanRecord.getServiceData(serviceID);
                    if (data != null && data.length != 0) {
                        log("service data=" + new String(data, Charset.forName("UTF-8")));
                    }

                }
            }

        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            log("onBatchScanResults");
            super.onBatchScanResults(results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            log("onScanFailed: " + errorCode);
            super.onScanFailed(errorCode);
        }
    };


    public BleCentral(Context context, Listener listener) {
        super(context);
        this.listener = listener;
        scanner = adapter.getBluetoothLeScanner();
    }

    public void serve() {
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

    @Override
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

    public void scan() {
        List<ScanFilter> filters = new ArrayList<ScanFilter>();

        ScanFilter filter = new ScanFilter.Builder()
                .setServiceUuid(new ParcelUuid(BatteryService.BATTERY_SERVICE_UUID))
                .build();
        filters.add(filter);

        ScanSettings settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build();

        log("startScan");
        // filter has bug
        // mBluetoothLeScanner.startScan(filters, settings, scanCallback);
        scanner.startScan(scanCallback);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                log("stopScan");
                scanner.stopScan(scanCallback);
            }
        }, 10000);
    }

}
