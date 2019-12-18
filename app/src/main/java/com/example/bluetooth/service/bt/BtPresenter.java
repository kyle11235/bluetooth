package com.example.bluetooth.service.bt;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

import java.util.Set;

public class BtPresenter {

    private boolean isViewDestroyed;
    private BtView view;
    private Context context;
    private BtReceiver btReceiver;
    private BtClient client;

    public BtPresenter(BtView view, Context context) {
        this.view = view;
        this.context = context;

        final BtView finalView = this.view;
        // receiver as service, listen to broadcast
        btReceiver = new BtReceiver(context, new BtReceiver.Listener() {
            @Override
            public void onFoundDevice(BluetoothDevice device) {
                if (isViewDestroyed) {
                    return;
                }
                finalView.addDevice(device);
            }
        });

        // client
        client = new BtClient(new BtBase.Listener() {
            @Override
            public void onBlueEvent(String type, Object object) {
                BtBase.log("onBlueEvent, type=" + type + ", object=" + object.toString());
            }
        });
    }

    public void destroy() {
        isViewDestroyed = true;
        this.view = null;
        this.context.unregisterReceiver(btReceiver);
    }

    public void scan() {
        // tool as service
        if (BtClient.enableAdapter()) {
            Set<BluetoothDevice> boundedDevices = BtClient.scan();
            view.updateDeviceList(boundedDevices);
        } else {
            view.showNoAdaptor();
        }
    }

    public void connect(BluetoothDevice device){
        client.connect(device);
    }

    public void sendMessage(String message){
        BtBase.log("client isConnected=" + client.isConnected());
        if(client.isConnected()){
            client.sendMessage(message);
        }
    }

}
