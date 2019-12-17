package com.example.bluetooth.service.bt;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bluetooth.R;


import java.util.Set;


public class BtActivity extends Activity implements BtView {

    // layout
    Button btnScan = null;
    RecyclerView listView = null;
    private BtItemAdapter btItemAdapter = null;

    // presenter
    private BtPresenter presenter = null;

    // common begin
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1.layout
        setContentView(R.layout.activity_bluetooth);
        btnScan = findViewById(R.id.btnScan);

        // 2.presenter
        presenter = new BtPresenter(this, this);
        listView = findViewById(R.id.listView);
        listView.setLayoutManager(new LinearLayoutManager(this));

        btItemAdapter = new BtItemAdapter(this);
        listView.setAdapter(btItemAdapter);

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

        // item click
        final Activity finalContext = this;
        btItemAdapter.addItemClickListener(new BtItemAdapter.ItemClickListener() {
            @Override
            public void onItemClick(BluetoothDevice item) {
                // post event
                // EventBus.getDefault().post(new Event(Event.CONNECT_BLUE, item));
                BtBase.log("send event to connect device, name=" + item.getName());
                finalContext.finish();
            }
        });

        // scan click
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.scan();
            }
        });

    }

    @Override
    public void updateDeviceList(final Set<BluetoothDevice> boundedDevices) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btItemAdapter.update(boundedDevices);
            }
        });
    }

    @Override
    public void addDevice(final BluetoothDevice device) {
        // called by presenter
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btItemAdapter.add(device);
            }
        });
    }

    @Override
    public void showNoAdaptor() {
        Toast.makeText(this, getString(R.string.blue_no_adaptor), Toast.LENGTH_SHORT);
    }

}