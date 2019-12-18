package com.example.bluetooth.service.ble;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

public class BleBase {

    // for GATT server and client role
    public static final String TAG = "HOOK";
    public static final int REQ_PERMISSION_CODE = 0x1000;
    protected Context context;
    protected BluetoothManager manager;
    protected BluetoothAdapter adapter;

    public BleBase(Context context) {
        this.context = context;
        this.manager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        this.adapter = manager.getAdapter();
    }

    // for android 6.0
    public static void requestPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            log("requestPermission");
            String[] permissions = {
                    Manifest.permission.ACCESS_COARSE_LOCATION
            };
            for (String str : permissions) {
                if (activity.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    activity.requestPermissions(permissions, REQ_PERMISSION_CODE);
                    break;
                }
            }
        }
    }

    public boolean enableAdapter() {
        if (adapter == null) {
            return false;
        }
        if (!adapter.isEnabled()) {
            log("adapter is not enabled");
            Boolean result = adapter.enable();
            log("adapter is enabled=" + result);
            return result;
        }
        if (!adapter.isMultipleAdvertisementSupported()) {
            log("Multiple advertisement not supported");
            return false;
        }
        return true;
    }

    public static void log(String str) {
        Log.e(TAG, str);
    }
}
