package com.example.bluetooth.service.ble;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bluetooth.R;
import com.example.bluetooth.service.bt.BtItemAdapter;
import com.example.bluetooth.service.bt.BtPresenter;
import com.example.bluetooth.service.bt.BtView;
import com.example.bluetooth.ui.central.CentralFragment;

import java.util.Set;


public class BleActivity extends Activity implements BleView {

    // layout
    Button btnScan = null;
    RecyclerView listView = null;
    private BleItemAdapter bleItemAdapter = null;

    // presenter
    private BlePresenter presenter = null;

    // common begin
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1.layout
        setContentView(R.layout.activity_bluetooth);
        btnScan = findViewById(R.id.btnScan);

        // 2.presenter
        presenter = new BlePresenter(this, this);
        listView = findViewById(R.id.listView);
        listView.setLayoutManager(new LinearLayoutManager(this));

        bleItemAdapter = new BleItemAdapter(this);
        listView.setAdapter(bleItemAdapter);

        // 3.listener
        this.initListener();

        // scan
        presenter.scan();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.destroy();
    }
    // common end

    private void initListener() {

        // scan click
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.scan();
            }
        });

        // item click
        final Activity finalContext = this;
        bleItemAdapter.addItemClickListener(new BleItemAdapter.ItemClickListener() {
            @Override
            public void onItemClick(BluetoothDevice item) {

                // connect it
                // or you can post event and connect it somewhere else
                // EventBus.getDefault().post(new Event(Event.CONNECT_BLUE, item));

                presenter.connect(item);
                finalContext.finish();
            }
        });

    }

    @Override
    public void updateDeviceList(final Set<BluetoothDevice> deviceSet) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                bleItemAdapter.update(deviceSet);
            }
        });
    }

    @Override
    public void addDevice(final BluetoothDevice device) {
        // called by presenter
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                bleItemAdapter.add(device);
            }
        });
    }

    @Override
    public void showNoAdaptor() {
        Toast.makeText(this, getString(R.string.blue_no_adaptor), Toast.LENGTH_SHORT);
    }

}