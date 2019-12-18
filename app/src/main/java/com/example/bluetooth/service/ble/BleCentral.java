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

public class BleCentral extends BleBase{

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

            log("device name=" + device.getName() + ", address=" + device.getAddress());
            listener.onFoundDevice(device);

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
