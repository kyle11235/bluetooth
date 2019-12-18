package com.example.bluetooth.service.ble;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;

import com.example.bluetooth.APP;
import com.example.bluetooth.service.ble.service.BatteryService;
import com.example.bluetooth.service.bt.BtBase;
import com.example.bluetooth.service.bt.BtClient;
import com.example.bluetooth.service.bt.BtReceiver;
import com.example.bluetooth.service.bt.BtView;

import java.util.HashSet;
import java.util.Set;

public class BlePresenter {

    private boolean isViewDestroyed;
    private BleView view;
    private Context context;
    private BleCentral central;
    public static BleClient client;

    public BlePresenter(BleView view, Context context) {
        this.view = view;
        this.context = context;

        final BleView finalView = this.view;

        // central
        central = new BleCentral(context, new BleBase.Listener() {
            @Override
            public void onBlueEvent(String type, Object object) {
                if (BleBase.Listener.FOUND_DEVICE.equals(type)) {
                    BluetoothDevice device = (BluetoothDevice) object;
                    finalView.addDevice(device);
                }
            }
        });

        // client
        client = new BleClient(context, new BleBase.Listener() {
            @Override
            public void onBlueEvent(String type, Object object) {
                APP.log("onBlueEvent, type=" + type + ", object=" + object.toString());
            }
        });
    }

    public void destroy() {
        isViewDestroyed = true;
        this.view = null;
    }

    public void scan() {
        // tool as service
        if (central.enableAdapter()) {
            view.updateDeviceList(new HashSet<BluetoothDevice>());
            central.scan();
        } else {
            view.showNoAdaptor();
        }
    }

    public void connect(BluetoothDevice device) {
        client.connect(device, false);
    }

    public static void sendMessage(String message) {
        if (client == null) {
            return;
        }

        // todo, write message into ble GATT server

        BluetoothGattCharacteristic characteristic = new BluetoothGattCharacteristic(BatteryService.BATTERY_SERVICE_UUID,
                BluetoothGattCharacteristic.PROPERTY_READ
                        | BluetoothGattCharacteristic.PROPERTY_WRITE
                        | BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE
                        | BluetoothGattCharacteristic.PROPERTY_NOTIFY,
                BluetoothGattCharacteristic.PERMISSION_READ);

        client.readCharacteristic(characteristic);
    }

    public static void displayDetails(){
        if (client == null) {
            return;
        }
        client.displayDetails();
    }

}
