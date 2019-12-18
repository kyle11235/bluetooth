package com.example.bluetooth.service.ble;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.ParcelUuid;

import com.example.bluetooth.APP;
import com.example.bluetooth.service.ble.service.BatteryService;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import android.os.Handler;

public class BleCentral extends BleBase{

    // scanner
    private BluetoothLeScanner scanner;
    private Handler handler = new Handler();

    // scan callback == BtReceiver, to return founded device
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

            // name
            ScanRecord scanRecord = result.getScanRecord();
            if(device.getName() != null || scanRecord.getDeviceName() != null){
                log("--- found device has name=" + device.getName());
            }
            if(APP.getMacAddress().equals(device.getAddress())){
                log("--- found my device");
            }
            log("device name=" + device.getName() + ", record name=" + scanRecord.getDeviceName() + ", address=" + device.getAddress());

            // service
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

            // return device
            listener.onBlueEvent(Listener.FOUND_DEVICE, device);
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
        super(context, listener);
        scanner = adapter.getBluetoothLeScanner();
    }



    public void scan() {

        // stop
        scanner.stopScan(scanCallback);

        // start
        List<ScanFilter> filters = new ArrayList<ScanFilter>();

        ScanFilter filter = new ScanFilter.Builder()
                .setServiceUuid(new ParcelUuid(BatteryService.BATTERY_SERVICE_UUID))
                .build();
        filters.add(filter);

        ScanSettings settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build();

        log("startScan");
        // filter has bug, with filter you got nothing
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
