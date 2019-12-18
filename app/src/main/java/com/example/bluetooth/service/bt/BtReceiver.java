package com.example.bluetooth.service.bt;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * 监听蓝牙广播-各种状态
 */
public class BtReceiver extends BroadcastReceiver {

    public interface Listener {
        void onFoundDevice(BluetoothDevice device);
    }

    private Listener listener;

    public BtReceiver(Context context, Listener listener) {
        super();
        this.listener = listener;

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);//蓝牙开关状态
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);//蓝牙开始搜索
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);//蓝牙搜索结束

        filter.addAction(BluetoothDevice.ACTION_FOUND);//蓝牙发现新设备(未配对的设备)
        filter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);//在系统弹出配对框之前(确认/输入配对码)
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);//设备配对状态改变
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);//最底层连接建立
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);//最底层连接断开

        filter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED); //BluetoothAdapter连接状态
        filter.addAction(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED); //BluetoothHeadset连接状态
        filter.addAction(BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED); //BluetoothA2dp连接状态
        filter.addAction(BluetoothA2dp.ACTION_PLAYING_STATE_CHANGED); //BluetoothA2dp playing状态
        context.registerReceiver(this, filter);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null)
            return;
        BtBase.log("BtReceiver action=" + action);
        switch (action) {
            case BluetoothAdapter.ACTION_STATE_CHANGED:
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                BtBase.log("bluetooth adapter state changed, state=" + state);
                break;
            case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                BtBase.log("discovery started");
                break;
            case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                BtBase.log("discovery finished");
                break;
            case BluetoothDevice.ACTION_FOUND:
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device != null)
                    BtBase.log("remote device found, name=" + device.getName() + ", address=" + device.getAddress());

                short rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MAX_VALUE);
                BtBase.log("RSSI=" + rssi);

                listener.onFoundDevice(device);
                break;
            case BluetoothDevice.ACTION_PAIRING_REQUEST: //在系统弹出配对框之前，实现自动配对，取消系统配对框
                /*try {
                    abortBroadcast();//终止配对广播，取消系统配对框
                    boolean ret = device.setPin("1234".getBytes()); //设置PIN配对码(必须是固定的)
                } catch (Exception e) {
                    e.printStackTrace();
                }*/
                break;
            case BluetoothDevice.ACTION_BOND_STATE_CHANGED:
                BtBase.log("device bond state changed, state=" + intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, 0));
                break;
            case BluetoothDevice.ACTION_ACL_CONNECTED:
                BtBase.log("acl connected");
                break;
            case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                BtBase.log("acl disconnected");
                break;
            case BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED:
                BtBase.log("adapter connection state changed, state=" + intent.getIntExtra(BluetoothAdapter.EXTRA_CONNECTION_STATE, 0));
                break;
            case BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED:
                BtBase.log("headset connection state changed, state=" + intent.getIntExtra(BluetoothHeadset.EXTRA_STATE, 0));
                break;
            case BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED:
                BtBase.log("A2DP connection state changed, state=" + intent.getIntExtra(BluetoothA2dp.EXTRA_STATE, 0));
                break;
            case BluetoothA2dp.ACTION_PLAYING_STATE_CHANGED:
                BtBase.log("A2DP playing state changed, state=" + intent.getIntExtra(BluetoothA2dp.EXTRA_STATE, 0));
                break;
            default:
                break;
        }
    }


}