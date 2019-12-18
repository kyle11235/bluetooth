package com.example.bluetooth.service.bt;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.content.Context;

import java.lang.reflect.Method;
import java.util.Set;

public class BtClient extends BtBase {

    // headset
    private BluetoothHeadset headset;
    private BluetoothProfile.ServiceListener headsetProfileListener;

    // a2dp
    private BluetoothA2dp a2dp;
    private BluetoothProfile.ServiceListener a2dpProfileListener;

    public BtClient(Listener listener) {
        super(listener);
    }

    public static Set<BluetoothDevice> scan(){
        if (adapter.isDiscovering()) {
            adapter.cancelDiscovery();
        }
        // discover new devices
        adapter.startDiscovery();

        // return bonded devices
        return adapter.getBondedDevices();
    }

    public void connect(BluetoothDevice device) {
        super.close();
        try {
            // final BluetoothSocket socket = device.createRfcommSocketToServiceRecord(SPP_UUID); //加密传输，Android系统强制配对，弹窗显示配对码
            final BluetoothSocket socket = device.createInsecureRfcommSocketToServiceRecord(SPP_UUID); //明文传输(不安全)，无需配对

            log("BtClient -> connect, name=" + device.getName());

            // connection can be built directly to paired device
            super.EXECUTOR.execute(new Runnable() {
                @Override
                public void run() {
                    loopRead(socket);
                }
            });
        } catch (Throwable e) {
            this.close();
        }
    }

    public void getHeadsetProxy(Context context){
        headsetProfileListener = new BluetoothProfile.ServiceListener() {
            public void onServiceConnected(int profile, BluetoothProfile proxy) {
                if (profile == BluetoothProfile.HEADSET) {
                    headset = (BluetoothHeadset) proxy;
                    listener.onBlueEvent(Listener.HEADSET_CONNECTED, null);
                }
            }
            public void onServiceDisconnected(int profile) {
                if (profile == BluetoothProfile.HEADSET) {
                    headset = null;
                    listener.onBlueEvent(Listener.HEADSET_DISCONNECTED, null);
                }
            }
        };

        // Establish connection to the proxy.
        adapter.getProfileProxy(context, headsetProfileListener, BluetoothProfile.HEADSET);
    }

    public void getA2DPProxy(Context context){
        a2dpProfileListener = new BluetoothProfile.ServiceListener() {
            public void onServiceConnected(int profile, BluetoothProfile proxy) {
                if (profile == BluetoothProfile.A2DP) {
                    a2dp = (BluetoothA2dp) proxy;
                    listener.onBlueEvent(Listener.A2DP_CONNECTED, null);
                }
            }
            public void onServiceDisconnected(int profile) {
                if (profile == BluetoothProfile.A2DP) {
                    a2dp = null;
                    listener.onBlueEvent(Listener.A2DP_DISCONNECTED, null);
                }
            }
        };

        // Establish connection to the proxy.
        adapter.getProfileProxy(context, a2dpProfileListener, BluetoothProfile.A2DP);
    }

    public Boolean connectA2dp(BluetoothDevice device){
        if (a2dp == null) {
            return false;
        }
        setPriority(device, 100);
        try {
            Method connectMethod =BluetoothA2dp.class.getMethod("connect", BluetoothDevice.class);
            connectMethod.invoke(a2dp, device);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private Boolean disConnectA2dp(BluetoothDevice device){
        if (a2dp == null) {
            return false;
        }
        setPriority(device, 0);
        try {
            Method connectMethod =BluetoothA2dp.class.getMethod("disconnect", BluetoothDevice.class);
            connectMethod.invoke(a2dp, device);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void setPriority(BluetoothDevice device, int priority) {
        if (a2dp == null) {
            return;
        }
        try {
            Method connectMethod =BluetoothA2dp.class.getMethod("setPriority",
                    BluetoothDevice.class,int.class);
            connectMethod.invoke(a2dp, device, priority);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void close(){
        // Close proxy connection after use.
        adapter.closeProfileProxy(BluetoothProfile.HEADSET, headset);
        adapter.closeProfileProxy(BluetoothProfile.A2DP, a2dp);
        super.close();
    }

}