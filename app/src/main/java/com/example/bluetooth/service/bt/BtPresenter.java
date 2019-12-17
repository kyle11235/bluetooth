package com.example.bluetooth.service.bt;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

import java.util.Set;

public class BtPresenter {

    private boolean isViewDestroyed;
    private BtView view;
    private Context context;
    private BtReceiver btReceiver;

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

}
