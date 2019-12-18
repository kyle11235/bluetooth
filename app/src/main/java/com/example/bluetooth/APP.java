package com.example.bluetooth;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

// add android:name=".APP" into application of manifest
public class APP extends Application {

    public static final String TAG = "HOOK";
    private static Application application;
    private static WifiInfo info;

    // permission
    public static final int REQ_PERMISSION_CODE = 0x1000;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        info = manager.getConnectionInfo();
        log("APP mac address=" + info.getMacAddress());
    }

    public static android.app.Application getApp() {
        return application;
    }

    public static String getStr(int name) {
        return application.getString(name);
    }

    // load file from assets
    public static String getMetaData(String key){
        ApplicationInfo ai = null;
        try {
            ai = application.getPackageManager().getApplicationInfo(application.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            return bundle.getString(key);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void log(String str) {
        Log.e(APP.TAG, str);
    }

    // Android 6.0动态请求权限
    public static void requestPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permissions = {
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.ACCESS_FINE_LOCATION

            };
            for (String str : permissions) {
                if (activity.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    activity.requestPermissions(permissions, REQ_PERMISSION_CODE);
                    break;
                }
            }
        }
    }

    public static String getMacAddress(){
        return info.getMacAddress();
    }
}
